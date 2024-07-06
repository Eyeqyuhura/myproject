package com.example.project3

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.example.project3.databinding.ItemCandidateResultsBinding
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


class VotingResultAdapter(val context: Context): RecyclerView.Adapter<VotingResultAdapter.CandidateListViewHolder>() {
    var candidateList= mutableListOf<Candidate>()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    fun populateArray(mutableCandidateList: MutableList<Candidate>){
//        candidateList=mutableCandidateList
        val diffResult = DiffUtil.calculateDiff(VotingResultAdapterDiffUtil(candidateList, mutableCandidateList))
        candidateList.clear()
        candidateList.addAll(mutableCandidateList)
        diffResult.dispatchUpdatesTo(this)

    }

    fun candidateDataChanged(index:Int,candidate: Candidate){
        candidateList[index]=candidate
        notifyItemChanged(index)
    }

    class CandidateListViewHolder(val binding: ItemCandidateResultsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(name:String,regNo:String,totalVotes:Int){

            binding.candidateNameTv3.text = name
            binding.candidateRegNoTv3.text = regNo
            val totalVotesText= "$totalVotes "
            binding.totalVotesTv.text=totalVotesText
            if(totalVotes==1) binding.votesTv.text="vote"

        }

        val candidateImage get() = binding.candidateImage

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateResultsBinding.inflate(inflater, parent, false)
        return CandidateListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return candidateList.size
    }

    override fun onBindViewHolder(holder: CandidateListViewHolder, position: Int) {
        val element=candidateList[position]
        holder.bind(element.name,element.regNo,element.totalVotes)

        if(element.imageName!=""){
            val fileRef = firebaseStorage.child(element.imageName)
            fileRef.downloadUrl.addOnSuccessListener{
                val imageUri=it.toString()
                showImage(imageUri,holder.candidateImage)
            }
        }

        holder.candidateImage.setOnClickListener {
            popupImage(holder.candidateImage)
        }


    }

    private fun showImage(url: String?, imgView: CircleImageView) {
        if (url != null && url.isEmpty() == false) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Glide.with(context).load(url).override(width * 1 / 2, width * 2 / 3)
                .centerCrop().transform(CircleCrop()).into(imgView)
        }
    }

    private fun popupImage(imgView: CircleImageView) {
        val imagePopup = ImagePopup(context)
        imagePopup.windowHeight = 700 // Optional
        imagePopup.windowWidth = 700 // Optional
        imagePopup.backgroundColor = Color.TRANSPARENT // Optional
        imagePopup.isHideCloseIcon = true // Optional
        imagePopup.isImageOnClickClose = true // Optional
        imagePopup.initiatePopup(imgView.drawable) // Load Image from Drawable
        imagePopup.viewPopup()
    }
}