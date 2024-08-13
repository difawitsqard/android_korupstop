package com.difawitsqard.korupstop.view.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.difawitsqard.korupstop.R
import com.difawitsqard.korupstop.ResizeTransformation
import com.difawitsqard.korupstop.databinding.ItemEduBinding
import com.difawitsqard.korupstop.model.EducationModel
import com.difawitsqard.korupstop.utils.FirebaseAuthInstance

class EducationAdapter(
    private var educationList: List<EducationModel>,
    private val onDeleteClick: (EducationModel) -> Unit,
    private val onUpdateClick: (EducationModel) -> Unit,
) : RecyclerView.Adapter<EducationAdapter.EducationViewHolder>() {

    val currentUser = FirebaseAuthInstance.getCurrentUser()

    inner class EducationViewHolder(private val binding: ItemEduBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(education: EducationModel) {
            binding.imageTitle.text = education.title
            binding.imageDescription.text = education.description

            Glide.with(binding.image.context)
                .load(education.imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.sample_image)
                        .error(R.drawable.sample_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(ResizeTransformation(500, 300))
                )
                .into(binding.image)

            if (education.userId == currentUser?.uid) {
                binding.deleteIcon.visibility = View.VISIBLE
                binding.updateIcon.visibility = View.VISIBLE

                binding.deleteIcon.setOnClickListener {
                    onDeleteClick(education)
                }

                binding.updateIcon.setOnClickListener {
                    onUpdateClick(education)
                }
            } else {
                binding.deleteIcon.visibility = View.GONE
                binding.updateIcon.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EducationViewHolder {
        val binding = ItemEduBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EducationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EducationViewHolder, position: Int) {
        val education = educationList[position]
        holder.bind(education)
    }

    override fun getItemCount(): Int = educationList.size

    fun submitList(newList: List<EducationModel>) {
        educationList = newList
        notifyDataSetChanged()
    }
}

