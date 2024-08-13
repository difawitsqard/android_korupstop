package com.difawitsqard.korupstop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.difawitsqard.korupstop.model.EducationModel
import com.difawitsqard.korupstop.viewmodel.UpdateEducationViewModel
import com.google.android.material.textfield.TextInputEditText

class UpdateEducationActivity : AppCompatActivity() {

    private lateinit var editTitle: TextInputEditText
    private lateinit var editDescription: TextInputEditText
    private lateinit var imagePreview: ImageView
    private lateinit var buttonUploadImage: Button
    private lateinit var buttonSubmitEducation: Button
    private lateinit var progressBar: ProgressBar

    private val viewModel: UpdateEducationViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_education)

        editTitle = findViewById(R.id.edit_title)
        editDescription = findViewById(R.id.edit_description)
        imagePreview = findViewById(R.id.image_preview)
        buttonUploadImage = findViewById(R.id.button_upload_image)
        buttonSubmitEducation = findViewById(R.id.button_submit_education)
        progressBar = findViewById(R.id.progressBar)

        val eduId = intent.getStringExtra("EDUCATION_ID")

        if (eduId != null) {
            viewModel.fetchEducationDetails(eduId)
            Log.d("UpdateEducationActivity", "Called")
        }

        viewModel.education.observe(this) { education ->
            education?.let {
                populateUI(it)
            }
        }

        viewModel.updateResult.observe(this) { isSuccess ->
            progressBar.visibility = View.GONE
            if (isSuccess) {
                Toast.makeText(this, "Update successful!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Update failed, please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonUploadImage.setOnClickListener {
            openImagePicker()
        }

        buttonSubmitEducation.setOnClickListener {
            submitEducation(eduId)
        }
    }

    private fun populateUI(education: EducationModel) {
        editTitle.setText(education.title)
        editDescription.setText(education.description)

        education.imageUrl?.let { url ->
            imagePreview.visibility = View.VISIBLE

            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.sample_image)
                .error(R.drawable.sample_image)
                .into(imagePreview)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                imagePreview.setImageURI(selectedImageUri)
                imagePreview.visibility = View.VISIBLE
            }
        }

    private fun submitEducation(eduId: String?) {
        val title = editTitle.text.toString().trim()
        val description = editDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = intent.getStringExtra("USER_ID") ?: ""
        progressBar.visibility = View.VISIBLE
        
        val updatedEducation = EducationModel(
            id = eduId ?: "",
            userId = userId,
            title = title,
            description = description,
            imageUrl = viewModel.education.value?.imageUrl ?: ""
        )

        viewModel.updateEducation(updatedEducation, selectedImageUri)
    }
}
