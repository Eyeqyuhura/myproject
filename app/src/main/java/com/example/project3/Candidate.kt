package com.example.project3

import java.util.Objects

data class Candidate(val name:String, val regNo :String, var id:String="", val totalVotes:Int=0, val imageName:String=""){
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val item: Candidate = o as Candidate
        return totalVotes == item.totalVotes &&
        Objects.equals(name, item.name) && Objects.equals(regNo, item.regNo) && Objects.equals(imageName, item.imageName);
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + regNo.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + totalVotes
        result = 31 * result + imageName.hashCode()
        return result
    }
}
