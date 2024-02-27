package com.example.project3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityStudentValidationListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class StudentValidationListActivity : AppCompatActivity() {
    private lateinit var studentValidationAdapter: StudentValidationAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val studentList= mutableListOf<Student>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityStudentValidationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val studentValidationAdapter=StudentValidationAdapter(this)
        binding.studListRv.adapter=studentValidationAdapter
        binding.studListRv.layoutManager= LinearLayoutManager(this)
        firestore.collection("VALUSER").get().addOnSuccessListener{documents ->
            for(doc in documents){
                val id=doc.getString("id") as String
                val name=doc.getString("name") as String
                val phoneNo=doc.getString("phoneNo") as String
                val gender=doc.getString("gender") as String
                val email=doc.getString("email") as String
                val password=doc.getString("password") as String
                val regNo=doc.getString("regNo") as String
                val admin= doc.getBoolean("admin") as Boolean
                val validated=doc.getString("validated") as String
                val courseId=doc.getString("courseId") as String
                Log.d("TAG", "onCreate: student")
                val student=Student(id,name,phoneNo,gender,email,password,regNo,admin,validated,courseId)
                studentList.add(student)
            }
            studentValidationAdapter.populateArray(studentList)
            studentValidationAdapter.notifyDataSetChanged()

        }



    }
}