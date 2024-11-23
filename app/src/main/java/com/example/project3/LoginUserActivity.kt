package com.example.project3

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.StudentLoginBinding
import com.example.project3.databinding.StudentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginUserActivity : AppCompatActivity(){
    private lateinit var binding:StudentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var regNo:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= StudentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            email=binding.edtLogEmail.text.toString()
            password=binding.edtLogPassword.text.toString()
            regNo=binding.edtRegNoLg.text.toString()
            var res=inputValidation(
                mapOf(
                    email to "email",password to "password",
                    regNo to "regNo"))

            if(res){
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                firestore.collection("USERS")
                    .document(regNo).get().addOnSuccessListener {document->
                        val saved_email = document.getString("email") as String
                        if(saved_email==email){
                            val intent=Intent(this,HomeActivity::class.java)
                            intent.putExtra("userId",regNo)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, "user regNo is incorrect", Toast.LENGTH_SHORT).show()
                            Log.w(ContentValues.TAG, "Error wrong regNo input")
                        }

                    }.addOnFailureListener {
                        Toast.makeText(this, "user regNo is incorrect", Toast.LENGTH_SHORT).show()
                        Log.w(ContentValues.TAG, "Error getting document", it)
                    }

            }.addOnFailureListener{
                Toast.makeText(this, "signin unsuccessfull", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error adding document", it)
            }
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            val intent= Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)

        }
    }


    private fun inputValidation(input :Map<String,String>):Boolean{
        for((elem,label) in input) {
            if (elem.isEmpty()) {
                Toast.makeText(this, "$label Should not be Empty", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            if(label=="regNo"){
                val regex = Regex("""[A-Za-z]\d{3}-\d{2}-\d{4}/\d{4}""")
                val matchResult = regex.matchEntire(elem)
                if(matchResult==null){
                    Toast.makeText(this, "check that the registration number is entered correctly", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }else{
                    regNo=regNo.replaceFirstChar { it.uppercase() }.replace('/','_')
                }
            }
        }
        return true
    }
}