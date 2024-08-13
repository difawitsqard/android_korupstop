package com.difawitsqard.korupstop.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.difawitsqard.korupstop.model.EducationModel
import com.difawitsqard.korupstop.repository.EducationRepository
import kotlinx.coroutines.launch

class EducationViewModel : ViewModel() {
    private val repository = EducationRepository()

    private val _submissionEdu = MutableLiveData<Boolean>()
    val submissionEdu: LiveData<Boolean> get() = _submissionEdu

    private val _educationContent = MutableLiveData<List<EducationModel>>()
    val educationContent: LiveData<List<EducationModel>> get() = _educationContent

    fun submitEducation(edu: EducationModel, imageUri: Uri) {
        viewModelScope.launch {
            repository.submitEducation(edu, imageUri).observeForever { result ->
                _submissionEdu.value = result
            }
        }
    }

    fun fetchEducationContent() {
        viewModelScope.launch {
            repository.getEducationContent().observeForever { fetchedEducation ->
                Log.d("EducationViewModel", "Fetched Education Content: $fetchedEducation")
                _educationContent.value = fetchedEducation
            }
        }
    }

    fun deleteEducation(id: String) {
        viewModelScope.launch {
            repository.deleteEducation(id).observeForever { result ->
                if (result) {
                    fetchEducationContent()
                }
                _submissionEdu.value = result
            }
        }
    }
}
