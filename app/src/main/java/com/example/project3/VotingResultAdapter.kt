package com.example.project3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.databinding.ItemCandidateResultsBinding
import com.example.project3.databinding.ItemCandidateVoteBinding

class VotingResultAdapter(): RecyclerView.Adapter<VotingResultAdapter.CandidateListViewHolder>() {
    var candidateList= mutableListOf<Candidate>()
    //    val firestore= FirebaseFirestore.getInstance()
    fun populateArray(mutableCandidateList: MutableList<Candidate>,){
        candidateList=mutableCandidateList
    }

    class CandidateListViewHolder(val binding: ItemCandidateResultsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(name:String,regNo:String,totalVotes:Int){
            binding.candidateNameTv3.text = name
            binding.candidateRegNoTv3.text = regNo
            binding.totalVotesTv.text=totalVotes.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateResultsBinding.inflate(inflater, parent, false)
        return CandidateListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return candidateList.size
    }

    override fun onBindViewHolder(holder: CandidateListViewHolder, position: Int) {
        val element=candidateList[position]
        holder.bind(element.name,element.regNo,element.totalVotes)


    }
}