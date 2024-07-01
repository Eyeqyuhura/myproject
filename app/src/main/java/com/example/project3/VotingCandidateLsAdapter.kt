package com.example.project3

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.example.project3.databinding.ItemCandidateVoteBinding
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class VotingCandidateLsAdapter(val viewModel: VotingActivityViewModel,val context: Context):RecyclerView.Adapter<VotingCandidateLsAdapter.CandidateListViewHolder>() {
    private var candidateList= listOf<Candidate>()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    fun populateArray(mutableCandidateList: List<Candidate>,){
        candidateList=mutableCandidateList
    }

    class CandidateListViewHolder(val binding: ItemCandidateVoteBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(name:String,regNo:String,check:Boolean){
            binding.candidateNameTv2.setText(name)
            binding.candidateRegNoTv2.setText(regNo)
            binding.checkBox.isChecked=check

        }
        val checkbox get() = binding.checkBox

        val candidateImage get() = binding.candidateImage

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateVoteBinding.inflate(inflater, parent, false)
        return CandidateListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return candidateList.size
    }

    override fun onBindViewHolder(holder: CandidateListViewHolder, position: Int) {
        val element=candidateList[position]
        val checked= viewModel.checkBoxValueList.value?.get(position)?:false
        holder.bind(element.name,element.regNo,checked)

        holder.checkbox.setOnClickListener {
            if(holder.checkbox.isChecked){
                viewModel.checkBoxValueList.value?.set(position, true)
                for(i in 0..(getItemCount()-1)){
                    if(i==position)continue
                    viewModel.checkBoxValueList.value?.set(i,false)
                }
                notifyDataSetChanged()
            }
        }

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

    private fun showImage(url: String?, imgView: CircleImageView) {
        if (url != null && url.isEmpty() == false) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Glide.with(context).load(url).override(width * 1 / 2, width * 2 / 3)
                .centerCrop().transform(CircleCrop()).into(imgView)
        }
    }
}