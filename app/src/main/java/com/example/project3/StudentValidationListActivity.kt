package com.example.project3

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.databinding.ActivityStudentValidationListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class StudentValidationListActivity : AppCompatActivity() {
    private lateinit var studentValidationAdapter: StudentValidationAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val studentList= mutableListOf<Student>()
    private lateinit var level:String
    private var levelId=""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityStudentValidationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        studentValidationAdapter=StudentValidationAdapter(this)
        binding.studListRv.adapter=studentValidationAdapter
        binding.studListRv.layoutManager= LinearLayoutManager(this)
        val regNo=intent.getSerializableExtra("userId",String::class.java)?:""
        firestore.collection("ADMIN")
            .document(regNo).get().addOnSuccessListener{document ->
                level=document.getString("level") as String
                if(level!="high manager"){
                    levelId=document.getString("selectedLevel") as String
                }
                fetchUsers()
            }
    }

    private fun fetchUsers(){
        firestore.collection("TOVALIDATEDUSER").get().addOnSuccessListener{documents ->
            for(doc in documents){
                val courseId=doc.getString("courseId") as String
                val schoolId=doc.getString("schoolId") as String
//                if(level=="course manager" && courseId!=levelId )continue
                val id=doc.getString("id") as String
                val name=doc.getString("name") as String
                val phoneNo=doc.getString("phoneNo") as String
                val gender=doc.getString("gender") as String
                val email=doc.getString("email") as String
                val password=doc.getString("password") as String
                val regNo=doc.getString("regNo") as String
                val admin= doc.getBoolean("admin") as Boolean
                val validated=doc.getString("validated") as String
                Log.d("TAG", "onCreate: student")
                val student=Student(id,name,phoneNo,gender,email,password,regNo,admin,validated,courseId,schoolId)
                studentList.add(student)
            }
            studentValidationAdapter.populateArray(studentList)
            studentValidationAdapter.notifyDataSetChanged()

        }
    }
}