package com.example.project3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityVotingBinding
import com.example.project3.databinding.ActivityVotingSessionResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VotingResultActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVotingSessionResultBinding
    val firestore = FirebaseFirestore.getInstance()
    private lateinit var session: VotingSession
    private var candidateList= mutableListOf<Candidate>()
    private lateinit var viewModel: VotingResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVotingSessionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val candidateListAdapter=VotingResultAdapter()
        binding.candidateResultListRv.adapter=candidateListAdapter
        binding.candidateResultListRv.layoutManager= LinearLayoutManager(this)

        val mySession=intent.getSerializableExtra("votingSession",VotingSession::class.java)
        if(mySession!=null) {
            session=mySession
            firestore.collection("VOTINGSESSIONS").document(mySession.id)
                .collection("CANDIDATES").get().addOnSuccessListener {documents ->
                    for(doc in documents){
                        val id=doc.getString("id") as String
                        val name=doc.getString("name") as String
                        val regNo=doc.getString("regNo") as String
                        val totalVotes=doc.getDouble("totalVotes") as Double
                        val candidate=Candidate(name, regNo, id,totalVotes.toInt())
                        candidateList.add(candidate)
                    }
                    candidateListAdapter.populateArray(candidateList)
                    candidateListAdapter.notifyDataSetChanged()

                }


        }



    }
}