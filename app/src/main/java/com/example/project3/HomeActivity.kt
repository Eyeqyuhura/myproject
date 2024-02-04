package com.example.project3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.StudentLoginBinding
import com.example.project3.databinding.StudentRegistrationBinding
import com.example.project3.databinding.VoterResultsBinding

class HomeActivity: AppCompatActivity()  {
    private lateinit var binding: VoterResultsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= VoterResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}