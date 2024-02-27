package com.example.project3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.HomePageBinding
import com.example.project3.databinding.StudentLoginBinding
import com.example.project3.databinding.StudentRegistrationBinding
import com.example.project3.databinding.VoterResultsBinding

class HomeActivity: AppCompatActivity()  {
    private lateinit var binding: HomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonAdmin.setOnClickListener {
            val intent= Intent(this,AdminActivity::class.java)
            startActivity(intent)
        }

    }
}