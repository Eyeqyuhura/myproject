package com.example.project3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.databinding.ItemCandidateVoteBinding

class VotingCandidateLsAdapter(val viewModel: VotingActivityViewModel):RecyclerView.Adapter<VotingCandidateLsAdapter.CandidateListViewHolder>() {
    var candidateList= mutableListOf<Candidate>()
    //    val firestore= FirebaseFirestore.getInstance()
    fun populateArray(mutableCandidateList: MutableList<Candidate>,){
        candidateList=mutableCandidateList
    }

    class CandidateListViewHolder(val binding: ItemCandidateVoteBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(name:String,regNo:String,check:Boolean){
            binding.candidateNameTv2.setText(name)
            binding.candidateRegNoTv2.setText(regNo)
            binding.checkBox.isChecked=check

        }
        val checkbox get() = binding.checkBox

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateVoteBinding.inflate(inflater, parent, false)
        return CandidateListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return candidateList.size
    }

    override fun onBindViewHolder(holder: CandidateListViewHolder, position: Int) {
        val element=candidateList[position]
        val checked= viewModel.checkBoxValueList.value?.get(position)?:false
        holder.bind(element.name,element.regNo,checked)

        holder.checkbox.setOnClickListener {
            if(holder.checkbox.isChecked){
                viewModel.checkBoxValueList.value?.set(position, true)
                for(i in 0..(getItemCount()-1)){
                    if(i==position)continue
                    viewModel.checkBoxValueList.value?.set(i,false)
                }
                notifyDataSetChanged()
            }
        }
    }
}