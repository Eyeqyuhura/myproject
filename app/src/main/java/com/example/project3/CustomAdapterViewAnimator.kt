package com.example.project3

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView


class CustomAdapterViewAnimator : DefaultItemAnimator() {
    override fun animateMove(
        holder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        holder.itemView.translationX = (fromX - toX).toFloat()
        holder.itemView.translationY = (fromY - toY).toFloat()
        holder.itemView.animate()
            .translationX(0f)
            .translationY(0f)
            .setDuration(2000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchMoveFinished(holder)
                }
            })
            .start()
        return true
    }

    override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
        super.onAnimationFinished(viewHolder)
        viewHolder.itemView.translationX = 0f
        viewHolder.itemView.translationY = 0f
    }
}