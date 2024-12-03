package com.example.project3

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.graphics.Color
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.HomePageBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity: AppCompatActivity()  {
    private lateinit var binding: HomePageBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var validated=""
    private var departmentName=""
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val regNo=intent.getSerializableExtra("userId",String::class.java)?:""
        firestore.collection("USERS")
            .document(regNo).get().addOnSuccessListener{document ->
                validated=document.getString("validated") as String
                val admin= document.getBoolean("admin") as Boolean
                val departmentId=document.getString("courseId") as String
                firestore.collection("courses").document(departmentId).get().addOnSuccessListener {
                    doc ->
                    departmentName=doc.getString("courseTitle") as String
                }
                if(admin){
                    binding.buttonAdmin.visibility= View.VISIBLE
                }
                when (validated) {
                    "pending" -> {
                        binding.pendingStatusTV.text="STATUS PENDING VERIFICATION"
                        binding.pendingStatusTV.visibility= View.VISIBLE
                        binding.pendingStatusTV.setTextColor(Color.BLUE)
                        binding.buttonAdmin.isClickable=false
                        binding.votingSessionBtn.isClickable =false
                    }
                    "rejected" -> {
                        binding.pendingStatusTV.text="STATUS REJECTED"
                        binding.pendingStatusTV.setTextColor(Color.RED)
                        binding.buttonAdmin.isClickable=false
                        binding.votingSessionBtn.isClickable =false
                        binding.pendingStatusTV.visibility= View.VISIBLE
                    }
                }
            }

        binding.buttonAdmin.setOnClickListener {
            when (validated) {
                "pending" -> {
                    Toast.makeText(this, "Await verification to continue", Toast.LENGTH_SHORT).show()
                }
                else->{
                    val intent= Intent(this,AdminActivity::class.java)
                    intent.putExtra("userId",regNo)
                    startActivity(intent)
                }
        }
        }

        binding.candidateRegistration.setOnClickListener{
            when (validated) {
                "pending" -> {
                    Toast.makeText(this, "Await verification to continue", Toast.LENGTH_SHORT)
                        .show()
                }else -> {
                val intent = Intent(this, CandidateRegistrationActivity::class.java)
                intent.putExtra("userId",regNo)
                intent.putExtra("departmentName",departmentName)
                startActivity(intent)
            }
            }
        }

        binding.votingSessionBtn.setOnClickListener {
            when (validated) {
                "pending" -> {
                    Toast.makeText(this, "Await verification to continue", Toast.LENGTH_SHORT)
                        .show()
                }else -> {
                    val intent = Intent(this, VotingSessionsActivity::class.java)
                    intent.putExtra("userId",regNo)
                    intent.putExtra("departmentName",departmentName)
                    startActivity(intent)
                }
            }
        }



    }
}
