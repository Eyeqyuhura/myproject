package com.example.project3

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.databinding.ItemStudentListBinding
import com.google.firebase.firestore.FirebaseFirestore

class StudentValidationAdapter(private val context: Context): RecyclerView.Adapter<StudentValidationAdapter.StudentValidationViewHolder>() {
    var studentList= mutableListOf<Student>()
    val firestore=FirebaseFirestore.getInstance()
    fun populateArray(mutableList: MutableList<Student>){
        studentList=mutableList
    }
    class StudentValidationViewHolder(private val binding: ItemStudentListBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(name:String,regNo:String,position: Int){
                binding.studentNameTv.text=name
                binding.studentRegNoTv.text=regNo
            }

        val acceptButton: Button
            get() {
                return binding.acceptBtn
            }

        val rejectButton:Button
                get(){
            return binding.rejectBtn
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentValidationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStudentListBinding.inflate(inflater, parent, false)
        return StudentValidationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentValidationViewHolder, position: Int) {
        val element=studentList[position]
        holder.bind(element.name,element.regNo,position)
        holder.acceptButton.setOnClickListener {
                firestore.collection("USERS").document(element.regNo).update("validated","accepted")
                firestore.collection("VALUSER").document(element.regNo).delete().addOnSuccessListener {
                    Toast.makeText(context, element.name+" ACCEPTED", Toast.LENGTH_SHORT)
                        .show()
                    studentList.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)

            }
        }
        holder.rejectButton.setOnClickListener {
            firestore.collection("USERS").document(element.regNo).update("validated","rejected")
            firestore.collection("VALUSER").document(element.regNo).delete().addOnSuccessListener {
                Toast.makeText(context, element.name+" REJECTED", Toast.LENGTH_SHORT)
                    .show()

                studentList.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }
        }
    }
}