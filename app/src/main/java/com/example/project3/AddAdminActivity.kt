package com.example.project3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.project3.databinding.ActivityAddAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddAdminActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddAdminBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var studentRegNo:String
    private var level=""
    private var selectedLevel=""
    private var selectedLevelType=""
    private var selectedLevelId=""
    private lateinit var levelSpinner: Spinner
    private lateinit var selectedLevelSpinner: Spinner
    private var highLevelValues= arrayListOf<String>("high manager","department manager")
//    private var levelValues= ArrayList<String>()
//    private var levelValuesId= ArrayList<String>()
    private var courseValues= ArrayList<String>()
    private var courseValuesId= ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val regNo=intent.getSerializableExtra("userId",String::class.java)?:""
        setUpSpinners()

        binding.saveAdminBtn.setOnClickListener {
//            Log.w(TAG, "Error getting documents.", exception)
            if(validateInputs()){
                firestore.collection("USERS")
                    .document(studentRegNo).get().addOnSuccessListener{document ->
                        val validate= document.getString("validated")
                        if(validate=="accepted"){
                            firestore.collection("USERS").document(studentRegNo).update("admin",true)
                                .addOnSuccessListener {
                                    firestore.collection("ADMIN").document(studentRegNo)
                                        .set(mapOf("level" to level,"selectedLevel" to selectedLevelId))
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "SAVE SUCCESSFUL", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                        .addOnFailureListener{
                                            Toast.makeText(this, "SAVE UNSUCCESSFUL", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                }.addOnFailureListener{
                                    Toast.makeText(this, "Check on Internet", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }else{
                            Toast.makeText(this, "USER NOT VERIFIED", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

            }
//            Log.d(getInputValues)
        }
    }

    private fun setUpSpinners(){
        levelSpinner=binding.levelSpinner
        selectedLevelSpinner=binding.selectionLevelSpinner
        val highLevelAdapter =ArrayAdapter(this, android.R.layout.simple_spinner_item, highLevelValues)
        highLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        val levelAdapter =ArrayAdapter(this, android.R.layout.simple_spinner_item, levelValues)
//        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val courseLevelAdapter =ArrayAdapter(this, android.R.layout.simple_spinner_item, courseValues)
        courseLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val emptyAdapter =ArrayAdapter(this, android.R.layout.simple_spinner_item, emptyArray<String>())
        emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        levelSpinner.adapter = highLevelAdapter

        firestore.collection("courses").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.data
                    courseValues.add( data["courseTitle"] as String)
                    courseValuesId.add(document.id)
                }
            }
            .addOnFailureListener { exception ->
                val TAG="course get error"
                Log.w(TAG, "Error getting documents.", exception)
            }
//        firestore.collection("levels").get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val data = document.data
//                    levelValues.add( data["levelTitle"] as String)
//                    levelValuesId.add(document.id)
//                }
//            }
//            .addOnFailureListener { exception ->
//                val TAG="level get error"
//                Log.w(TAG, "Error getting documents.", exception)
//            }
        levelSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                level= levelSpinner.selectedItem as String
                when (level) {
//                    "school manager" -> {
//                        selectedLevelSpinner.adapter=levelAdapter
//                        selectedLevelType="school"
//                    }
                    "high manager" -> {
                        selectedLevelSpinner.adapter=emptyAdapter
                    }
                    else -> {
                        selectedLevelSpinner.adapter=courseLevelAdapter
                        selectedLevelType="department"
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
                when(selectedLevelType){
//                    "school"->{
//                        selectedLevelId=levelValuesId[levelValues.indexOf(selectedLevel)]
//                    }
                    "department"->{
                        selectedLevelId=courseValuesId[courseValues.indexOf(selectedLevel)]
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


    }

    private fun validateInputs():Boolean{
        studentRegNo=binding.edRegNo.text.toString()
        if(studentRegNo.isEmpty()){
            Toast.makeText(this, "Student RegNo Should not be Empty", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true

    }


}