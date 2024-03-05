package com.example.project3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityVotingSessionBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class VotingSessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVotingSessionBinding
    private val firestore=FirebaseFirestore.getInstance()
    private val sessionList= mutableListOf<VotingSession>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVotingSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val votingSessionAdapter=VotingSessionAdapter(this)
        binding.sessionListRv.adapter=votingSessionAdapter
        binding.sessionListRv.layoutManager= LinearLayoutManager(this)
        firestore.collection("VOTINGSESSIONS").get().addOnSuccessListener{documents ->
            for(doc in documents){
                val id=doc.getString("id") as String
                val title=doc.getString("title") as String
                val startTime=doc.getDate("startTime") as Date
                val endTime=doc.getDate("endTime") as Date
                val level=doc.getString("level") as String
                val selectedLevel=doc.getString("selectedLevel") as String
                val votingSession=VotingSession(title, startTime,endTime,level,selectedLevel,id)
                sessionList.add(votingSession)
            }
            votingSessionAdapter.populateArray(sessionList)


            votingSessionAdapter.notifyDataSetChanged()

        }

        binding.createNewBtn.setOnClickListener {
            val intent= Intent(this,VotingSesionsDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}