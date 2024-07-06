package com.example.project3

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityVotingSessionResultBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.SpecialSymbol.index


class VotingResultActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVotingSessionResultBinding
    val firestore = FirebaseFirestore.getInstance()
    private lateinit var session: VotingSession
    private var candidateList= mutableListOf<Candidate>()
    private var pieEntryList= arrayListOf<PieEntry>()
    private lateinit var pieDataSet :PieDataSet
    private lateinit var viewModel: VotingResultAdapter
    private val idMap = mutableMapOf<String,Int>()
    private val pieEntryIdMap= mutableMapOf<String,Int>()
    private var candidateListAdapter=VotingResultAdapter(this)
    private lateinit var pieData:PieData
    private lateinit var pieChart:PieChart
    private var overallTotalVotes=0.0
//    private var overallVotes=0.0

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVotingSessionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pieChart=binding.pieChat

        binding.candidateResultListRv.adapter=candidateListAdapter
        binding.candidateResultListRv.layoutManager= LinearLayoutManager(this)

        val mySession=intent.getSerializableExtra("votingSession",VotingSession::class.java)
        if(mySession!=null) {
            session=mySession
            firestore.collection("VOTINGSESSIONS").document(mySession.id)
                .collection("CANDIDATES").get().addOnSuccessListener {documents ->
                    var tempList= mutableListOf<Candidate>()
                    for(doc in documents){
                        val id=doc.getString("id")?:""
                        val name=doc.getString("name") as String
                        val regNo=doc.getString("regNo") ?:""
                        val totalVotes=doc.getDouble("totalVotes") as Double
                        val image=doc.getString("imageName") ?:""
                        val candidate=Candidate(name, regNo, id,totalVotes.toInt(), imageName = image)
                        tempList.add(candidate)
                        overallTotalVotes+=totalVotes.toFloat()
                    }

                    candidateList=tempList.sortedByDescending { it.totalVotes }.toMutableList()
                    var count=0
                    for(value in candidateList){
                        idMap[value.id]=count
                        count+=1
                    }
                    candidateListAdapter.populateArray(candidateList)
                    candidateListAdapter.notifyDataSetChanged()
                    var leftOverVotes=overallTotalVotes
                    for(i in 0..2){
//                        if (candidateList[i].id!="") pieEntryIdMap[candidateList[i].id]=i
                        pieEntryList.add(PieEntry(candidateList[i].totalVotes.toFloat(),candidateList[i].name))
                        leftOverVotes-=candidateList[i].totalVotes.toFloat()
                    }
                    pieEntryList.add(PieEntry(leftOverVotes.toFloat(),"Others"))
                    pieDataSet=PieDataSet(pieEntryList,"Candidates")
                    pieDataSet.colors=ColorTemplate.MATERIAL_COLORS.toList()
                    val formatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()} %"
                        }
                    }
                    pieDataSet.valueFormatter=formatter
                    pieDataSet.valueTextSize=16f


                    pieData=PieData(pieDataSet)
                    pieChart.setUsePercentValues(true);
                    pieChart.setHoleRadius(0f);
                    pieChart.setTransparentCircleRadius(10f);
                    pieChart.data=pieData
                    pieChart.description.isEnabled=false
                    pieChart.animateY(1000)
                    pieChart.setEntryLabelTextSize(16f)
                    pieChart.legend.isEnabled = false
                    pieChart.invalidate()

                }

            addFirebaseListener()

        }

        binding.reportButton.setOnClickListener {
            val intent= Intent(this,ReportActivity::class.java)
            intent.putExtra("votingSession",mySession)
            startActivity(intent)
        }



    }

    fun addFirebaseListener(){
        firestore.collection("VOTINGSESSIONS").document(session.id)
            .collection("CANDIDATES").addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("TAG voting activity", "addListener: "+error.message,error )
                    return@addSnapshotListener
                }
                var checkChanges=false

                if (value != null) {
                    for (dc in value.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.MODIFIED -> {
                                val modifiedData = dc.document.data
                                val dataId=(modifiedData["id"] ?:"").toString()
                                val name=modifiedData["name"] as String
                                val regNo=modifiedData["regNo"] as String
                                val totalVotes=modifiedData["totalVotes"] as Long
                                val image=(modifiedData["imageName"] ?:"").toString()
//                                var tempCandidateList= mutableListOf<Candidate>()
//                                tempCandidateList.addAll(candidateList)
                                if(dataId!=""){//to remove later once dataId is made mandatory
                                    checkChanges=true
                                    val candidateIndex=idMap[dataId]!!
//                                    val pieIndex=pieEntryIdMap[dataId]!!
                                    val candidateData=Candidate(name,regNo,dataId,totalVotes.toInt(), imageName = image)
                                    overallTotalVotes+=candidateData.totalVotes-candidateList[candidateIndex].totalVotes
                                    candidateList[candidateIndex]=candidateData
//                                    candidateListAdapter.candidateDataChanged(candidateIndex,candidateData)
//
//                                    val entry = pieEntryList[pieIndex]
//                                    entry.y = totalVotes.toFloat()
//                                    pieDataSet.notifyDataSetChanged(); // Let the data set know about the change
//                                    pieData.notifyDataChanged(); // Let the data object know about the change
//                                    pieChart.notifyDataSetChanged(); // Let the chart know about the change
//                                    pieChart.invalidate(); // Refresh the chart
                                }
                            }
                            else -> {}
                        }
                    }

                    if(checkChanges) {
                        candidateList.sortByDescending { it.totalVotes }
                        var leftOverVotes=overallTotalVotes
                        pieEntryList.clear()
                        for (i in 0..2) {
                            pieEntryList.add(
                                PieEntry(
                                    candidateList[i].totalVotes.toFloat(),
                                    candidateList[i].name
                                )
                            )
                            leftOverVotes -= candidateList[i].totalVotes.toFloat()
                        }
                        var count=0
                        for(elem in candidateList){
                            idMap[elem.id]=count
                            count+=1
                        }
                        pieEntryList.add(PieEntry(leftOverVotes.toFloat(),"Others"))
                        pieDataSet.notifyDataSetChanged(); // Let the data set know about the change
                        pieData.notifyDataChanged(); // Let the data object know about the change
                        pieChart.notifyDataSetChanged(); // Let the chart know about the change
                        pieChart.invalidate(); // Refresh the chart
                        candidateListAdapter.populateArray(candidateList)
                        candidateListAdapter.notifyDataSetChanged()
                    }
                }


            }

    }
}