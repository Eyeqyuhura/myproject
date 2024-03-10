package com.example.project3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.databinding.ItemCandidateBinding

class ManageCandidateListAdapter():RecyclerView.Adapter<ManageCandidateListAdapter.CandidateListViewHolder>() {
    var candidateList= mutableListOf<Candidate>()
//    val firestore= FirebaseFirestore.getInstance()
    fun populateArray(mutableList: MutableList<Candidate>){
    candidateList=mutableList
    }

    class CandidateListViewHolder(val binding:ItemCandidateBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(name:String,regNo:String){
            binding.candidateNameTv.setText(name)
            binding.candidateRegNoTv.setText(regNo)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateBinding.inflate(inflater, parent, false)
        return CandidateListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return candidateList.size
    }

    override fun onBindViewHolder(holder: CandidateListViewHolder, position: Int) {
        val element=candidateList[position]
        holder.bind(element.name,element.regNo)
    }
}