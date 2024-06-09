package com.example.project3

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityVotingBinding
import com.example.project3.databinding.ActivityVotingSessionResultBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VotingResultActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVotingSessionResultBinding
    val firestore = FirebaseFirestore.getInstance()
    private lateinit var session: VotingSession
    private var candidateList= mutableListOf<Candidate>()
    private var pieEntryList= arrayListOf<PieEntry>()
    private lateinit var viewModel: VotingResultAdapter
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVotingSessionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pieChart=binding.pieChat

        val candidateListAdapter=VotingResultAdapter()
        binding.candidateResultListRv.adapter=candidateListAdapter
        binding.candidateResultListRv.layoutManager= LinearLayoutManager(this)

        val mySession=intent.getSerializableExtra("votingSession",VotingSession::class.java)
        if(mySession!=null) {
            firestore.collection("VOTINGSESSIONS").document(mySession.id)
                .collection("CANDIDATES").get().addOnSuccessListener {documents ->
                    for(doc in documents){
                        val id=doc.getString("id") as String
                        val name=doc.getString("name") as String
                        val regNo=doc.getString("regNo") as String
                        val totalVotes=doc.getDouble("totalVotes") as Double
                        val candidate=Candidate(name, regNo, id,totalVotes.toInt())
                        candidateList.add(candidate)
                        pieEntryList.add(PieEntry(totalVotes.toFloat(),name))
                    }
                    candidateListAdapter.populateArray(candidateList)
                    candidateListAdapter.notifyDataSetChanged()
                    val pieDataSet=PieDataSet(pieEntryList,"Candidates")
                    pieDataSet.colors=ColorTemplate.MATERIAL_COLORS.toList()
                    val formatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()} %"
                        }
                    }
                    pieDataSet.valueFormatter=formatter
                    pieDataSet.valueTextSize=16f


                    val pieData=PieData(pieDataSet)
                    pieChart.setUsePercentValues(true);
                    pieChart.setHoleRadius(0f);
                    pieChart.setTransparentCircleRadius(10f);
                    pieChart.data=pieData
                    pieChart.description.isEnabled=false
                    pieChart.animateY(1000)
                    pieChart.setEntryLabelTextSize(16f)
                    pieChart.legend.isEnabled = false
                    pieChart.invalidate()

                }

        }

        binding.reportButton.setOnClickListener {
            val intent= Intent(this,ReportActivity::class.java)
            intent.putExtra("votingSession",mySession)
            startActivity(intent)
        }



    }
}