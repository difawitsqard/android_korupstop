package com.difawitsqard.korupstop.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.difawitsqard.korupstop.model.EducationModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

import android.util.Log

class EducationRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun submitEducation(edu: EducationModel, imageUri: Uri): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val storageReference = storage.reference.child("educations/${UUID.randomUUID()}.jpg")
        val uploadTask = storageReference.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val updatedEducation = edu.copy(imageUrl = uri.toString())

                firestore.collection("educations")
                    .add(updatedEducation)
                    .addOnSuccessListener { documentReference ->
                        val documentId = documentReference.id
                        firestore.collection("educations").document(documentId)
                            .update("id", documentId)
                            .addOnSuccessListener { result.value = true }
                            .addOnFailureListener {
                                Log.e(
                                    "EducationRepository",
                                    "Failed to update education ID in Firestore",
                                    it
                                )
                                result.value = false
                            }
                    }
                    .addOnFailureListener {
                        Log.e("EducationRepository", "Failed to add education to Firestore", it)
                        result.value = false
                    }
            }.addOnFailureListener {
                Log.e("EducationRepository", "Failed to get download URL", it)
                result.value = false
            }
        }.addOnFailureListener {
            Log.e("EducationRepository", "Failed to upload image", it)
            result.value = false
        }

        return result
    }

    fun getEducationContent(): LiveData<List<EducationModel>> {
        val educationList = MutableLiveData<List<EducationModel>>()

        firestore.collection("educations")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("EducationRepository", "Failed to fetch education content", e)
                    educationList.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val educations = snapshot.documents.map { document ->
                        val data = document.toObject(EducationModel::class.java)
                        data?.copy(id = document.id) ?: EducationModel(id = document.id)
                    }
                    educationList.value = educations
                } else {
                    educationList.value = emptyList()
                }
            }

        return educationList
    }


    fun deleteEducation(id: String, imageUrl: String? = null): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val firestoreTask = firestore.collection("educations").document(id).delete()

        if (imageUrl != null) {
            val storageTask = deleteImage(imageUrl)
            storageTask.observeForever { success ->
                if (success) {
                    firestoreTask.addOnSuccessListener {
                        result.value = true
                    }.addOnFailureListener {
                        Log.e(
                            "EducationRepository",
                            "Failed to delete education from Firestore",
                            it
                        )
                        result.value = false
                    }
                } else {
                    result.value = false
                }
            }
        } else {
            firestoreTask.addOnSuccessListener {
                result.value = true
            }.addOnFailureListener {
                Log.e("EducationRepository", "Failed to delete education from Firestore", it)
                result.value = false
            }
        }

        return result
    }


    private fun deleteImage(imageUrl: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val storageReference = storage.getReferenceFromUrl(imageUrl)

        storageReference.delete()
            .addOnSuccessListener {
                result.value = true
            }
            .addOnFailureListener {
                Log.e("EducationRepository", "Failed to delete image from Storage", it)
                result.value = false
            }

        return result
    }

    fun updateEducation(
        id: String,
        updatedEducation: EducationModel,
        imageUri: Uri? = null
    ): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        if (imageUri != null) {
            val storageReference = storage.reference.child("educations/${UUID.randomUUID()}.jpg")
            val uploadTask = storageReference.putFile(imageUri)

            uploadTask.addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    deleteImage(updatedEducation.imageUrl)
                    val updatedEdu = updatedEducation.copy(imageUrl = uri.toString())
                    firestore.collection("educations").document(id)
                        .set(updatedEdu)
                        .addOnSuccessListener { result.value = true }
                        .addOnFailureListener {
                            Log.e(
                                "EducationRepository",
                                "Failed to update education in Firestore",
                                it
                            )
                            result.value = false
                        }
                }.addOnFailureListener {
                    Log.e("EducationRepository", "Failed to get download URL", it)
                    result.value = false
                }
            }.addOnFailureListener {
                Log.e("EducationRepository", "Failed to upload image", it)
                result.value = false
            }
        } else {
            // No image update
            firestore.collection("educations").document(id)
                .set(updatedEducation)
                .addOnSuccessListener { result.value = true }
                .addOnFailureListener {
                    Log.e("EducationRepository", "Failed to update education in Firestore", it)
                    result.value = false
                }
        }

        return result
    }

}
