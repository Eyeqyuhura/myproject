package com.example.project3

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.example.project3.databinding.ActivityAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminBinding
    private val firestore = FirebaseFirestore.getInstance()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val regNo=intent.getSerializableExtra("userId",String::class.java)?:""
        firestore.collection("ADMIN")
            .document(regNo).get().addOnSuccessListener{document ->
                if(document.exists()) {
                    val level = document.getString("level") as String
                    if (level == "high manager") {
                        binding.addAdminBtn.visibility = View.VISIBLE
                    }
                }
            }
        binding.addAdminBtn.setOnClickListener {
            val intent= Intent(this,AddAdminActivity::class.java)
            startActivity(intent)
        }
        binding.studentValBtn.setOnClickListener {
            val intent= Intent(this,StudentValidationListActivity::class.java)
            intent.putExtra("userId",regNo)
            startActivity(intent)
        }
        binding.manageVotingSessBtn.setOnClickListener {
            val intent= Intent(this,ManagaVotingSessionActivity::class.java)
            intent.putExtra("userId",regNo)
            startActivity(intent)
        }
    }
}