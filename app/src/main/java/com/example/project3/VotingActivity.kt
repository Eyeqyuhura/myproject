package com.example.project3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityVotingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VotingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVotingBinding
    val firestore = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var session: VotingSession
    private var candidateList= mutableListOf<Candidate>()
    private lateinit var viewModel: VotingActivityViewModel
    private var checkBoxValueList=ArrayList<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVotingBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(VotingActivityViewModel::class.java)
        setContentView(binding.root)

        val candidateListAdapter=VotingCandidateLsAdapter(viewModel)
        binding.candidateVoteListRv.adapter=candidateListAdapter
        binding.candidateVoteListRv.layoutManager=LinearLayoutManager(this)

        val mySession=intent.getSerializableExtra("votingSession",VotingSession::class.java)
        if(mySession!=null) {
            session=mySession
            firestore.collection("VOTINGSESSIONS").document(mySession.id)
                .collection("CANDIDATES").get().addOnSuccessListener {documents ->
                    for(doc in documents){
                        val id=doc.getString("id") as String
                        val name=doc.getString("name") as String
                        val regNo=doc.getString("regNo") as String
                        val candidate=Candidate(name, regNo, id)
                        candidateList.add(candidate)
                        checkBoxValueList.add(false)
                    }
                    if(viewModel.checkBoxValueList.value!!.size<1) viewModel.checkBoxValueList.value=checkBoxValueList
                    candidateListAdapter.populateArray(candidateList)
                    candidateListAdapter.notifyDataSetChanged()

                }

            binding.saveVoteBtn.setOnClickListener {
                val index=viewModel.checkBoxValueList.value?.indexOfFirst { it==true  }
                if(index!=null&&index!=-1){
                    val votedCandidate=candidateList[index]
                    firestore.collection("USERS").document(firebaseAuth.uid.toString()).get().addOnSuccessListener {
                        if(it.exists()){
                            val username=it.getString("name") as String
                            val regno=it.getString("regNo") as String
                            firestore.collection("VOTINGSESSIONS")
                                .document(mySession.id).collection("VOTERS")
                                .document(firebaseAuth.uid.toString()).set(mapOf("username" to username,"regNo" to regno))
                            firestore.collection("VOTINGSESSIONS").document(mySession.id)
                                .collection("UNCOUNTEDVOTES")
                                .add(mapOf("id" to votedCandidate.id,"name" to votedCandidate.name)).addOnSuccessListener {
                                    Toast.makeText(this, "vote saved successfully", Toast.LENGTH_SHORT).show()

                                }

                        }

                    }

                }

            }
        }




    }
}