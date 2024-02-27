package com.example.project3

import android.R
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.StudentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUserActivity : AppCompatActivity() {
    private lateinit var binding:StudentRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var name:String
    private lateinit var phoneNo:String
    private lateinit var gender:String
    private lateinit var regNo:String
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var confirmPassword:String
    private lateinit var selectedCourse:String
    private lateinit var selectedCourseId:String
    private var courseList= ArrayList<Course>()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var courseSpinner: Spinner
    private val adapterValues=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=StudentRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        setupSpinner()

        binding.registerButton.setOnClickListener {
            Toast.makeText(this, "check", Toast.LENGTH_SHORT).show()
            if(validation()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Successfully added user", Toast.LENGTH_SHORT).show()
                        val newStudent = Student(
                            firebaseAuth.uid.toString(),name,phoneNo,gender,email,password,regNo,false,"pending",selectedCourseId)
                        firestore.collection("USERS")
                            .document(newStudent.regNo).set(newStudent)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this, "User added to database", Toast.LENGTH_SHORT).show()
                                    val intent=Intent(this,HomeActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "User not added to database", Toast.LENGTH_SHORT).show()
                                Log.w(ContentValues.TAG, "Error adding document", it)
                            }
                        firestore.collection("VALUSER")
                            .document(newStudent.regNo).set(newStudent)

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.loginButton.setOnClickListener {
            val intent= Intent(this,LoginUserActivity::class.java)
            startActivity(intent)

        }

    }
    private fun setupSpinner(){
        courseSpinner=binding.courseSpinner
        val adapter= ArrayAdapter(this, R.layout.simple_spinner_item, adapterValues)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        firestore.collection("courses").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.data
                    val elem=Course(document.id,data["levelId"] as String,data["courseTitle"] as String)
                    adapterValues.add(data["courseTitle"] as String)
                    courseList.add(elem)
                }
                courseSpinner.adapter=adapter
            }
            .addOnFailureListener { exception ->
                val TAG="course get error"
                Log.w(TAG, "Error getting documents.", exception)
            }

        courseSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCourse= courseSpinner.selectedItem as String
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


    }
    private fun getInputValues(){
        name=binding.edtName.text.toString()
        phoneNo=binding.edtPhoneNo.text.toString()
        gender = if(binding.radioButtonFemale.isChecked){
            "Female"
        }else if(binding.radioButtonMale.isChecked){
            "Male"
        }else ""
        regNo=binding.edtRegNo.text.toString()
        email=binding.edtEmail.text.toString()
        password=binding.edtPassword.text.toString()
        confirmPassword=binding.edtConfirmPassword.text.toString()
        selectedCourseId=courseList.find { it.courseTitle==selectedCourse }?.id?:"7"
    }

    private fun validation():Boolean{
        getInputValues()
        var res=inputValidation(
            mapOf(
                name to "name",phoneNo to "phone number",
                gender to "gender",regNo to "RegNo",email to "email",
                password to "password",confirmPassword to "confirm password"))

        if(password!=confirmPassword){
            Toast.makeText(this, "check that both password and confirm Password are a match", Toast.LENGTH_SHORT)
                .show()
            res=false
        }
        if(password.length<6){
            Toast.makeText(this, "password should contain more than 6 characters", Toast.LENGTH_SHORT)
                .show()
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
}