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

class LoginUserActivity : AppCompatActivity(){
    private lateinit var binding:StudentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= StudentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email=binding.edtLogEmail.text.toString()
            val password=binding.edtLogPassword.text.toString()
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent=Intent(this,HomeActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener{
                Toast.makeText(this, "signin unsuccessfull", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error adding document", it)
            }
        }
    }
}