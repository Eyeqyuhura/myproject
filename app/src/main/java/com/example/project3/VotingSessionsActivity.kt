package com.example.project3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityVotingSession2Binding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class VotingSessionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVotingSession2Binding
    private val firestore= FirebaseFirestore.getInstance()
    private val sessionList= mutableListOf<VotingSession>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVotingSession2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val votingSessionsAdapter=VotingSessionsAdapter(this)
        binding.sessionListRv2.adapter=votingSessionsAdapter
        binding.sessionListRv2.layoutManager= LinearLayoutManager(this)
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
            votingSessionsAdapter.populateArray(sessionList)


            votingSessionsAdapter.notifyDataSetChanged()

        }
    }
}