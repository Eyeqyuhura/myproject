package com.example.project3

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.project3.databinding.ActivityVotingSesionsDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VotingSesionsDetailsActivity : AppCompatActivity() {
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



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVotingSesionsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mySession=intent.getSerializableExtra("votingSession",VotingSession::class.java)
        if(mySession!=null) {
            sessionSetOnCreateFlag=true
            session = mySession
            val dateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date1 = dateFormat.format(session.startTime)
            val date2= dateFormat.format(session.endTime)
            binding.titleEdt.setText(session.title)
            binding.startTimeEdt.setText(date1)
            binding.endTimeEdt.setText(date2)
            startTime=date1
            endTime=date2
            level=session.level
            sessionTitle=session.title
        }
        setUpSpinners()

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

        binding.addCandidateBtn.setOnClickListener {
            if(validateCandidateInputs()){
                val candidateName=binding.studNameEdt.text.toString()
                val candidateRegNo=binding.studRegNoEdt.text.toString()
                val candidate=Candidate(candidateName,candidateRegNo)
                firestore.collection("USERS").document(candidateRegNo).get().addOnSuccessListener {
                    if (!it.exists()) {
                        Toast.makeText(this, "Candidate not found in database", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Candidate added successfully", Toast.LENGTH_SHORT).show()
                        firestore.collection("VOTINGSESSIONS")
                            .document(session.id).collection("CANDIDATES").add(candidate)
                            .addOnSuccessListener {
                                firestore.collection("VOTINGSESSIONS")
                                    .document(session.id).collection("CANDIDATES")
                                    .document(it.id).update("id",it.id)
                            }
                    }
                }
            }
        }

    }

    private fun setUpSpinners(){
        levelSpinner=binding.levelSpinnerV
        selectedLevelSpinner=binding.selectedLevelSpinnerV
        val highLevelAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, highLevelValues)
        highLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val levelAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, levelValues)
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val courseLevelAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, courseValues)
        courseLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val emptyAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, emptyArray<String>())
        emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


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
}