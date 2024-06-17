package com.example.project3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project3.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()

        binding.ResetPasswordButton.setOnClickListener {
            val email= binding.edtLogEmail.text.toString()
            if(email.isEmpty()){
                Toast.makeText(this, "Ensure you enter your email", Toast.LENGTH_SHORT).show()
            }else{
                firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(this, "reset password link sent to your registered email", Toast.LENGTH_SHORT).show()
                    val intent= Intent(this,LoginUserActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "Error -: "+it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backButton.setOnClickListener {
            val intent= Intent(this,LoginUserActivity::class.java)
            startActivity(intent)
        }



    }
}