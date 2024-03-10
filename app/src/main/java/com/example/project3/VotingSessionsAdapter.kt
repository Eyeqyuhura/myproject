package com.example.project3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.databinding.ItemVotingSession2Binding
import com.example.project3.databinding.ItemVotingSessionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VotingSessionsAdapter(private val context: Context): RecyclerView.Adapter<VotingSessionsAdapter.VotingSessionViewHolder>() {
    var votingSessions= mutableListOf<VotingSession>()
    fun populateArray(mutableList: MutableList<VotingSession>){
        votingSessions=mutableList
    }

    class VotingSessionViewHolder(val binding: ItemVotingSession2Binding):RecyclerView.ViewHolder(binding.root){
        fun bind(title:String, level:String, selectedLevel:String, start: Date, end: Date){
            binding.sessionTitleTv2.text=title
            if (level != "high level") {
                binding.sessionLevelTv2.text=selectedLevel
            } else {
                binding.sessionLevelTv2.text="institutional"
            }
            val dateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val startDate=dateFormat.format(start)
            val endDate=dateFormat.format(end)
            binding.sessionSchedualTv2.text="from $startDate to $endDate"

        }
        val toVote: Button
            get(){
                return binding.toVoteBtn
            }
        val toResult: Button
            get(){
                return binding.toResultBtn
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingSessionViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding = ItemVotingSession2Binding.inflate(inflater,parent,false)
        return VotingSessionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return votingSessions.size
    }

    override fun onBindViewHolder(holder: VotingSessionViewHolder, position: Int) {
        val element=votingSessions[position]
        holder.bind(element.title,element.level,element.selectedLevel,element.startTime,element.endTime)
        holder.toVote.setOnClickListener {
            val intent = Intent(context, VotingActivity::class.java)
            intent.putExtra("votingSession",element)
            context.startActivity(intent)
        }
        holder.toResult.setOnClickListener {
            val intent = Intent(context, VotingResultActivity::class.java)
            intent.putExtra("votingSession",element)
            context.startActivity(intent)
        }
    }


}