package com.example.project3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.HomePageBinding

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
        binding.votingSessionBtn.setOnClickListener {
            val intent= Intent(this,VotingSessionsActivity::class.java)
            startActivity(intent)
        }

    }
}