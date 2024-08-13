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

class UpdateEducationViewModel : ViewModel() {
    private val repository = EducationRepository()

    private val _education = MutableLiveData<EducationModel>()
    val education: LiveData<EducationModel> get() = _education

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> get() = _updateResult

    fun fetchEducationDetails(id: String) {
        viewModelScope.launch {
            repository.getEducationContent().observeForever { fetchedEducation ->
                _education.value = fetchedEducation.find { it.id == id }
                Log.d("UpdateEducationViewModel", "Fetched Education Content: $fetchedEducation")
            }
        }
    }

    fun updateEducation(updatedEducation: EducationModel, imageUri: Uri?) {
        viewModelScope.launch {
            repository.updateEducation(updatedEducation.id, updatedEducation, imageUri)
                .observeForever { result ->
                    _updateResult.value = result
                }
        }
    }
}
