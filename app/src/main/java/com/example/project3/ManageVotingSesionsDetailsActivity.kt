package com.example.project3

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.project3.databinding.ActivityVotingSesionsDetailsBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ManageVotingSesionsDetailsActivity : AppCompatActivity() {
    private lateinit var binding :ActivityVotingSesionsDetailsBinding
    private lateinit var sessionTitle:String
    private lateinit var startTime: String
    private lateinit var endTime:String
    private lateinit var session: VotingSession
    private var sessionSetOnCreateFlag=false
    private var level=""
    private var selectedLevel=""
    private var highLevelValues= arrayListOf<String>("high level","school level","course level")
    private val firestore=FirebaseFirestore.getInstance()
    private lateinit var levelSpinner:Spinner
    private lateinit var selectedLevelSpinner:Spinner
    private var levelValues= ArrayList<String>()
    private var courseValues= ArrayList<String>()
    private var candidateList= mutableListOf<Candidate>()
    private lateinit var viewModel: ManageVotingSesionsDetailsViewModel
    private var checkBoxValueList=ArrayList<Boolean>()
    private val dateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val currentDate = LocalDate.now()
    private val date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri
    private var imageName=""
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private lateinit var manageCandidateListAdapter:ManageCandidateListAdapter
    private val idMap = mutableMapOf<String,Int>()




    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVotingSesionsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ManageVotingSesionsDetailsViewModel::class.java)
        val mySession=intent.getSerializableExtra("votingSession",VotingSession::class.java)
        manageCandidateListAdapter=ManageCandidateListAdapter(viewModel,this)
        binding.candidateListRv.adapter=manageCandidateListAdapter
        binding.candidateListRv.layoutManager= LinearLayoutManager(this)
        if (mySession != null) {
            session = mySession
            setUpViews()
        } else {
            binding.saveVotingDetailsBtn.visibility = View.VISIBLE
        }
        setUpSpinners()
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?:Intent()
                imageUri = data.data!!
                imageName=System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
                showImage(imageUri.toString(),binding.addCandidateImage)
            }
        }

        binding.saveVotingDetailsBtn.setOnClickListener {
            if(validateVotingInputs()) {
                val dateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date1=dateFormat.parse(startTime)!!
                val date2=dateFormat.parse(endTime)!!
                session = VotingSession(sessionTitle,date1,date2,level,selectedLevel)
                firestore.collection("VOTINGSESSIONS").add(session).addOnSuccessListener {
                    sessionSetOnCreateFlag=true
                    Toast.makeText(this, "Session added successfully", Toast.LENGTH_SHORT).show()
                    session.id=it.id
                    firestore.collection("VOTINGSESSIONS").document(it.id).update("id",it.id)
                }.addOnFailureListener{
                    Toast.makeText(this, "Session not added, check on internet", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.startTimeEdt.setOnClickListener{
            showDatePicker("date1")
        }

        binding.endTimeEdt.setOnClickListener {
            showDatePicker("date2")
        }

        binding.addCandidateImage.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            val galleryIntent = Intent()
//            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//            galleryIntent.setType("image/*");
            startForResult.launch(galleryIntent);
        }

        binding.addCandidateBtn.setOnClickListener {
            val startDate=dateFormat.parse(startTime)!!
//            if(startDate>date) { //to return once testing ends
            if(validateCandidateInputs()){
                val candidateName=binding.studNameEdt.text.toString()
                val candidateRegNo=binding.studRegNoEdt.text.toString()
                val candidate=Candidate(candidateName,candidateRegNo,imageName=imageName)

                firestore.collection("VOTINGSESSIONS")
                    .document(session.id).collection("CANDIDATES").add(candidate)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Candidate added successfully", Toast.LENGTH_SHORT).show()
                        firestore.collection("VOTINGSESSIONS")
                            .document(session.id).collection("CANDIDATES")
                            .document(it.id).update("id",it.id)
                        uploadToFirebase(imageUri)
                    }
            }
//            }else{
//                Toast.makeText(this, "Can't add candidates once voting has started", Toast.LENGTH_SHORT).show()
//            }
        }

        binding.deleteCandidateBtn.setOnClickListener {

            val startDate=dateFormat.parse(startTime)!!
//            if(startDate>date) { //to return once testing ends
                val size = viewModel.checkBoxValueList.value?.size ?: 0
                val toDeleteIdList = mutableListOf<String>()
                if (size > 0) {//check bound length error
                    for (i in 0..<size) {
                        val check = viewModel.checkBoxValueList.value!![i]
                        if (check) {
                            toDeleteIdList.add(candidateList[i].id)
                        }
                    }
                }
                for (id in toDeleteIdList) {
                    if(id!="") {
                        firestore.collection("VOTINGSESSIONS")
                            .document(session.id).collection("CANDIDATES").document(id).delete()
                    }
                }


        }

    }

    private fun getFileExtension(mUri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }

    private fun uploadToFirebase(uriRes:Uri) {
        val fileRef = firebaseStorage.child(imageName);
        val uploadTask=fileRef.putFile(uriRes)
        uploadTask.addOnFailureListener{
            Log.e("TAG_MYAPPLICATION", "uploadToFirebase: "+it.message, )
            Toast.makeText(this, "Image upload Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setUpViews( ) {
            sessionSetOnCreateFlag = true
            val date1 = dateFormat.format(session.startTime)
            val date2 = dateFormat.format(session.endTime)
            binding.titleEdt.setText(session.title)
            binding.titleEdt.isEnabled = false
            binding.startTimeEdt.setText(date1)
            binding.startTimeEdt.isEnabled = false
            binding.endTimeEdt.setText(date2)
            binding.endTimeEdt.isEnabled = false
            binding.selectedLevelSpinnerV.isEnabled = false
            binding.levelSpinnerV.isEnabled = false
            binding.frameLayout.visibility = View.VISIBLE
            startTime = date1
            endTime = date2
            level = session.level
            sessionTitle = session.title
            addFirebaseListener()

    }

    private fun setUpSpinners(){
        levelSpinner=binding.levelSpinnerV
        selectedLevelSpinner=binding.selectedLevelSpinnerV
        val highLevelAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, highLevelValues)
        highLevelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        val levelAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, levelValues)
        levelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        val courseLevelAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, courseValues)
        courseLevelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        val emptyAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, emptyArray<String>())
        emptyAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)


        levelSpinner.adapter = highLevelAdapter
        levelSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                level= levelSpinner.selectedItem as String
                when (level) {
                    "school level" -> {
                        selectedLevelSpinner.adapter=levelAdapter
                        if(sessionSetOnCreateFlag){
                            levelValues.forEachIndexed { index, s ->
                                if(s==session.selectedLevel){
                                    selectedLevelSpinner.setSelection(index)
                                }
                            }
                        }
                    }
                    "high level" -> {
                        selectedLevelSpinner.adapter=emptyAdapter
                    }
                    "course level" -> {
                        selectedLevelSpinner.adapter=courseLevelAdapter
                        if(sessionSetOnCreateFlag){
                            courseValues.forEachIndexed { index, s ->
                                if(s==session.selectedLevel){
                                    selectedLevelSpinner.setSelection(index)
                                }
                            }
                        }
                    }
                }
                // Do something with the selected item
                Log.d("Selected item:", level)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
                selectedLevelSpinner.adapter=emptyAdapter
            }
        }

        selectedLevelSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLevel= selectedLevelSpinner.selectedItem as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        firestore.collection("courses").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.data
                    courseValues.add( data["courseTitle"] as String)
                }
                if(sessionSetOnCreateFlag) {
                    highLevelValues.forEachIndexed { index, s ->
                        if (s == session.level) {
                            levelSpinner.setSelection(index)
                        }
                    }
                }


            }
            .addOnFailureListener { exception ->
                val TAG="course get error"
                Log.w(TAG, "Error getting documents.", exception)
            }
        firestore.collection("levels").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.data
                    levelValues.add( data["levelTitle"] as String)
                }
                if(sessionSetOnCreateFlag) {
                    highLevelValues.forEachIndexed { index, s ->
                        if (s == session.level) {
                            levelSpinner.setSelection(index)
                        }
                    }
                }

            }
            .addOnFailureListener { exception ->
                val TAG="level get error"
                Log.w(TAG, "Error getting documents.", exception)
            }


    }



    private fun validateCandidateInputs():Boolean{
        val candidateName=binding.studNameEdt.text.toString()
        val candidateRegNo=binding.studRegNoEdt.text.toString()
        var res=inputValidation(
            mapOf(candidateName to "student name",candidateRegNo to "student RegNo")
        )
        if(!sessionSetOnCreateFlag){
            res=false
            Toast.makeText(this, "The voting session is not saved", Toast.LENGTH_SHORT).show()
        }
        candidateList.forEach{candidate ->
            if(candidateRegNo==candidate.regNo){
                res=false
                Toast.makeText(this, "Candidate already exists", Toast.LENGTH_SHORT).show()
            }
        }
        if (imageName==""){
            res=false
            Toast.makeText(this, "Candidate Photo should be added", Toast.LENGTH_SHORT).show()
        }
        return res

    }

    private fun validateVotingInputs():Boolean{
        sessionTitle=binding.titleEdt.text.toString()
        startTime=binding.startTimeEdt.text.toString()
        endTime=binding.endTimeEdt.text.toString()
        var res=inputValidation(
            mapOf(
                sessionTitle to "sessionTitle",startTime to "startTime",
                endTime to "endTime"))

        if(res){
            val dateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date1=dateFormat.parse(startTime)!!
            val date2=dateFormat.parse(endTime)!!
            if(date2<date1){
                    Toast.makeText(this, "End date should not be less than start date", Toast.LENGTH_SHORT)
                        .show()
                res=false
            }
        }

        return res
    }

    private fun inputValidation(input :Map<String,String>):Boolean{
        for((elem,label) in input) {
            if (elem.isEmpty()) {
                Toast.makeText(this, "$label Should not be Empty", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        }
        return true
    }

    fun showImage(url: String?, imgView: CircleImageView) {
        if (url != null && url.isEmpty() == false) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Glide.with(this).load(url).override(width * 1 / 2, width * 2 / 3)
                .centerCrop().into(imgView)
        }
    }

    private fun showDatePicker(dateType:String) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
//                 Use the selected date in your application logic
//                 For example, you can display it in a TextView
                when(dateType){
                    "date1"->binding.startTimeEdt.setText("$selectedDate 06:00")
                    "date2"->binding.endTimeEdt.setText("$selectedDate 18:00")
                }

            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
    private fun addFirebaseListener(){
        firestore.collection("VOTINGSESSIONS").document(session.id)
            .collection("CANDIDATES").addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("TAG voting activity", "addListener: "+error.message,error )
                    return@addSnapshotListener
                }
                if (value != null) {
//                    resumeChannel.receive()
                    for (dc in value.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val addedData = dc.document.data
                                val id=(addedData["id"] ?:"").toString()
                                val name=addedData["name"] as String
                                val regNo=addedData["regNo"] as String
                                val image=(addedData["imageName"] ?: "").toString()
                                val candidate = Candidate(name, regNo, id,imageName=image)
                                candidateList.add(candidate)
                                checkBoxValueList.add(false)
                                viewModel.checkBoxValueList.value!!.add(false)
                                manageCandidateListAdapter.addCandidate(candidate)
                                idMap[id]=candidateList.size-1
                            }
                            DocumentChange.Type.REMOVED->{
                                val deletedData = dc.document.data
                                val id=(deletedData["id"] ?:"").toString()
                                val index =idMap[id]?:-1
                                if (index!=-1) {
                                    candidateList.removeAt(index)
                                    checkBoxValueList.removeAt(index)
                                    viewModel.checkBoxValueList.value!!.removeAt(index)
                                    manageCandidateListAdapter.removeCandidate(index)
                                    idMap.remove(id)
                                }


                            }
                            DocumentChange.Type.MODIFIED->{
                                val modifiedData = dc.document.data
                                val id=(modifiedData["id"] ?:"").toString()
                                val name=modifiedData["name"] as String
                                val regNo=modifiedData["regNo"] as String
                                val image=(modifiedData["imageName"] ?: "").toString()
                                val candidate = Candidate(name, regNo, id,imageName=image)
                                var index =idMap[id]?:-1
                                if (index!=-1) {
                                    candidateList[index]=candidate
                                    manageCandidateListAdapter.modifiedCandidate(index,candidate)
                                }else{
                                    index=candidateList.size-1
                                    idMap[id]=candidateList.size-1
                                    candidateList[index]=candidate
                                    manageCandidateListAdapter.modifiedCandidate(index,candidate)
                                }

                            }
                            else -> {}
                        }
                    }
                }


            }

    }
}