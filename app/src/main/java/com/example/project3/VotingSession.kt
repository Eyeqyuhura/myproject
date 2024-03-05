package com.example.project3

import java.io.Serializable
import java.util.Date


data class VotingSession(
    var title:String, var startTime: Date, var endTime: Date, var level:String, var selectedLevel:String,
    var id:String=""): Serializable
