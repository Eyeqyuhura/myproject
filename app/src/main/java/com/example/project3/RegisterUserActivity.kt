package com.example.project3

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
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
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=StudentRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        binding.registerButton.setOnClickListener {
            Toast.makeText(this, "check", Toast.LENGTH_SHORT).show()
            if(validation()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Successfully added user", Toast.LENGTH_SHORT).show()
                        val newStudent = Student(
                            firebaseAuth.uid.toString(),name,phoneNo,gender,email,password,regNo
                        )
                        firestore.collection("USERS")
                            .document(newStudent.id).set(newStudent)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this, "User added to database", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "User not added to database", Toast.LENGTH_SHORT).show()
                                Log.w(ContentValues.TAG, "Error adding document", it)
                            }

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
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