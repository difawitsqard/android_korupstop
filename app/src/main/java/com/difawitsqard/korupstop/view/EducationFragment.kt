package com.difawitsqard.korupstop.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.difawitsqard.korupstop.R
import com.difawitsqard.korupstop.model.EducationModel
import com.difawitsqard.korupstop.utils.FirebaseAuthInstance
import com.difawitsqard.korupstop.view.home.HomeFragment
import com.difawitsqard.korupstop.viewmodel.EducationViewModel

class EducationFragment : Fragment() {

    private lateinit var viewModel: EducationViewModel
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonUploadImage: Button
    private lateinit var imagePreview: ImageView
    private lateinit var buttonSubmitEducation: Button
    private lateinit var progressBar: ProgressBar

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_education, container, false)

        viewModel = ViewModelProvider(this).get(EducationViewModel::class.java)
        editTextTitle = view.findViewById(R.id.edit_title)
        editTextDescription = view.findViewById(R.id.edit_description)
        buttonUploadImage = view.findViewById(R.id.button_upload_image)
        imagePreview = view.findViewById(R.id.image_preview)
        buttonSubmitEducation = view.findViewById(R.id.button_submit_education)
        progressBar = view.findViewById(R.id.progressBar) // Add this line

        buttonUploadImage.setOnClickListener {
            openImagePicker()
        }

        buttonSubmitEducation.setOnClickListener {
            submitEducation()
        }

        viewModel.submissionEdu.observe(viewLifecycleOwner) { result ->
            result?.let {
                progressBar.visibility = View.GONE
                if (it) {
                    Toast.makeText(context, "Education submitted successfully!", Toast.LENGTH_SHORT)
                        .show()

                    (activity as? AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, HomeFragment())
                        ?.addToBackStack(null)
                        ?.commit()
                } else {
                    Toast.makeText(context, "Failed to submit education.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return view
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

    private fun submitEducation() {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val currentUser = FirebaseAuthInstance.getCurrentUser()

        if (title.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(context, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        val edu = EducationModel(title = title, userId= currentUser?.uid,  description = description)
        selectedImageUri?.let { imageUri ->
            viewModel.submitEducation(edu, imageUri)
        }
    }
}
