package com.example.project3

import androidx.recyclerview.widget.DiffUtil


class VotingResultAdapterDiffUtil(oldList: List<Candidate>, newList: List<Candidate>) :
    DiffUtil.Callback() {
    private val oldList: List<Candidate>
    private val newList: List<Candidate>

    init {
        this.oldList = oldList
        this.newList = newList
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id === newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition])
    }
}

