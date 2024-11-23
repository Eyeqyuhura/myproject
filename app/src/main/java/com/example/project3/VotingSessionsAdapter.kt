package com.example.project3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.databinding.ItemVotingSession2Binding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

class VotingSessionsAdapter(
    private val context: Context,
    val userRegNo: String,
    val voterDepartment: String,
    val isDelegate:Boolean

): RecyclerView.Adapter<VotingSessionsAdapter.VotingSessionViewHolder>() {
    var votingSessions= mutableListOf<VotingSession>()

    fun populateArray(mutableList: MutableList<VotingSession>){
        votingSessions=mutableList
    }

    class VotingSessionViewHolder(val binding: ItemVotingSession2Binding):RecyclerView.ViewHolder(binding.root){
        private val firestore= FirebaseFirestore.getInstance()
        fun bind(title:String, level:String, selectedLevel:String, start: Date, end: Date,voterDepartment: String,regNo:String,isDelegate:Boolean){
            binding.sessionTitleTv2.text=title

            if (isDelegate && selectedLevel==""){
                binding.toVoteBtn.visibility= View.VISIBLE
            }

            if(selectedLevel==voterDepartment&&selectedLevel!=""){
                val temp=title
                binding.toVoteBtn.visibility= View.VISIBLE
            }
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
        holder.bind(element.title,element.level,element.selectedLevel,element.startTime,element.endTime,voterDepartment,userRegNo,isDelegate)
        holder.toVote.setOnClickListener {
            val currentDate = Date()
            if(element.endTime<currentDate){
                Toast.makeText(context, "Voting time has come to an end", Toast.LENGTH_SHORT).show()
            }else if (element.startTime>currentDate){
                Toast.makeText(context, "Voting has not started", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(context, VotingActivity::class.java)
                intent.putExtra("votingSession",element)
                intent.putExtra("userId",userRegNo)
                context.startActivity(intent)
            }
        }
        holder.toResult.setOnClickListener {
            val currentDate = Date()
            if(element.endTime>currentDate){
                Toast.makeText(context, "Can't view results before the elections end", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(context, VotingResultActivity::class.java)
                intent.putExtra("votingSession",element)
                context.startActivity(intent)
            }

        }
    }


}