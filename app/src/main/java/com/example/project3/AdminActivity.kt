package com.example.project3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project3.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addAdminBtn.setOnClickListener {
            val intent= Intent(this,AddAdminActivity::class.java)
            startActivity(intent)
        }
        binding.studentValBtn.setOnClickListener {
            val intent= Intent(this,StudentValidationListActivity::class.java)
            startActivity(intent)
        }
        binding.manageVotingSessBtn.setOnClickListener {
            val intent= Intent(this,VotingSessionActivity::class.java)
            startActivity(intent)
        }
    }
}