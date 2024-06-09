package com.example.project3

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project3.databinding.ActivityReportBinding
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.List
import com.itextpdf.text.ListItem
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


class ReportActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReportBinding
    val firestore = FirebaseFirestore.getInstance()
    private var candidateList= mutableListOf<Candidate>()
    var mTotalVotes:Double=0.0
    var winnerName=""
    var topVotes=0
    private lateinit  var session:VotingSession
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mySession = intent.getSerializableExtra("votingSession", VotingSession::class.java)
        if (mySession != null) {
            session=mySession
            firestore.collection("VOTINGSESSIONS").document(mySession.id)
                .collection("CANDIDATES").get().addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val id = doc.getString("id") as String
                        val name = doc.getString("name") as String
                        val regNo = doc.getString("regNo") as String
                        val totalVotes = doc.getDouble("totalVotes") as Double
                        val candidate = Candidate(name, regNo, id, totalVotes.toInt())
                        candidateList.add(candidate)
                        mTotalVotes += totalVotes
                        if (topVotes < totalVotes) {
                            topVotes=totalVotes.toInt()
                            winnerName = name
                        }
                    }
                    createPDF()
                }
        }


    }
    private fun createPDF(){
        //check if they exist, if not create them(directory)
        val storagePath = File(getExternalFilesDir(""), "Reports");
        if (!storagePath.exists()) {
            storagePath.mkdirs()
        }

        val unixTime = System.currentTimeMillis() / 1000L
        val reportFile = File(storagePath, "Report$unixTime.pdf")
        val document = Document(PageSize.A4)
        try {
            val output = FileOutputStream(reportFile)
            PdfWriter.getInstance(document, output)
            document.open()
            addParagraph(
                arrayOf("REPORT FOR VOTING SESSION " + session.title),
                document,
                isTitle = true,
                spacing = 20f
            )

            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            addParagraph(arrayOf("REPORT TIME: ", currentTime.format(formatter)), document, hasLable = true)

            val currentDate = Date()
            val status=if (session.endTime<currentDate){
                "Finnished"
            }else if (session.startTime>currentDate){
                "Pending start"
            }else{
                "Ongoing"
            }
            addParagraph(
                arrayOf("Status: ", status),
                document,
                hasLable = true
            )


            val dateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val startDate=dateFormat.format(session.startTime)
            val endDate=dateFormat.format(session.endTime)
            val duration="from $startDate to $endDate"
            addParagraph(
                arrayOf("DURATION:  ", duration),
                document,
                hasLable = true,
                spacing = 10f
            )

            val paragraph = Paragraph("")
            document.add(paragraph)
            addParagraph(arrayOf("Candidate"), document, isSubTitle = true, spacing = 5f)


            val members = mutableListOf<String>()
            val resultStatements= mutableListOf<String>()
            for(candidate in candidateList){
                val name=candidate.name
                val votes=candidate.totalVotes
                val percentageDouble=(votes/mTotalVotes)*100
                val percentage=percentageDouble.roundToInt()
                members.add(name)
                resultStatements.add("$name with $votes votes, $percentage% of total")
            }


            val memberList = List(List.ORDERED)

            for (member in members) {
                memberList.add(ListItem(member))
            }
            document.add(memberList)


            addParagraph(
                arrayOf("RESULTS"),
                document,
                isTitle = true,
                spacing = 20f,
                color = Color.GREEN
            )

            addParagraph(arrayOf("Total number of votes: ", mTotalVotes.toInt().toString()), document, hasLable = true)


            val winner=if (session.endTime<currentDate){
                winnerName
            }else if (session.startTime>currentDate){
                "Votting pending start"
            }else{
                "Votting Ongoing"
            }
            addParagraph(
                arrayOf("Winner: ", winner),
                document,
                hasLable = true
            )


            addParagraph(arrayOf("Statistics"), document, isSubTitle = true, spacing = 5f)


            val results = mutableListOf<String>()
            for(res in resultStatements){
                results.add(res)
            }

            val resultsList = List(List.ORDERED)
            for (result in results) {
                resultsList.add(ListItem(result))
            }
            document.add(resultsList)
            document.close()
        }catch (e: DocumentException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val pdfView=binding.reportPdfViewer

        pdfView.initWithUrl(
            url = getFileUrl(reportFile),
            lifecycleCoroutineScope = lifecycleScope,
            lifecycle = lifecycle
        )
    }
    private fun getFileUrl(file: File): String {
        return file.toURI().toURL().toString()
    }

    //data class BaseColor(val red: Int, val green: Int, val blue: Int)
//
//// Function to get the base color
    private fun getBaseColor(color: Int): BaseColor {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return BaseColor(red, green, blue)
    }

    private fun addParagraph(texts:Array<String>, document:Document, isTitle:Boolean=false, isSubTitle:Boolean=false, spacing:Float=0f, hasLable:Boolean=false, color:Int=Color.BLUE){

        var paragraph = Paragraph("")
        document.add(paragraph)
        val titleFont=FontFactory.getFont(FontFactory.HELVETICA_BOLD,22f,Font.UNDERLINE, getBaseColor(color))
        val subTitleFont=FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f, Font.UNDERLINE)
        val lableFont=FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f)
        val normalFont=FontFactory.getFont(FontFactory.HELVETICA, 16f)

        var font = if(isTitle){
            titleFont
        }else if (hasLable){
            lableFont
        }else if (isSubTitle){
            subTitleFont
        }else{
            normalFont
        }
        for(text in texts){
            val newText = Chunk(text, font)
            paragraph.add(newText)
            font=normalFont
        }
        paragraph.spacingAfter=spacing
        if(isTitle){
            paragraph.alignment = Element.ALIGN_CENTER
        }
        document.add(paragraph)
    }
}





