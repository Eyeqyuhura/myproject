package com.example.project3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.databinding.ItemVotingSessionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManageVotingSessionAdapter (private val context:Context):RecyclerView.Adapter<ManageVotingSessionAdapter.VotingSessionViewHolder>(){
    var votingSessions= mutableListOf<VotingSession>()
    fun populateArray(mutableList: MutableList<VotingSession>){
        votingSessions=mutableList
    }
    class VotingSessionViewHolder(val binding:ItemVotingSessionBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(title:String, level:String, selectedLevel:String, start: Date,end:Date){
            binding.sessionTitleTv.text=title
            if (level != "high level") {
                binding.sessionLevelTv.text=selectedLevel
            } else {
                binding.sessionLevelTv.text="institutional"
            }
            val dateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val startDate=dateFormat.format(start)
            val endDate=dateFormat.format(end)
            binding.sessionSchedualTv.text="from $startDate to $endDate"

        }
        val card: ConstraintLayout
            get(){
            return binding.sessionCard
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingSessionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVotingSessionBinding.inflate(inflater, parent, false)
        return VotingSessionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return votingSessions.size
    }

    override fun onBindViewHolder(holder: VotingSessionViewHolder, position: Int) {
        val element=votingSessions[position]
        holder.bind(element.title,element.level,element.selectedLevel,element.startTime,element.endTime)
        holder.card.setOnClickListener {
            val intent = Intent(context, ManageVotingSesionsDetailsActivity::class.java)
            intent.putExtra("votingSession",element)
            context.startActivity(intent)
        }
    }
}