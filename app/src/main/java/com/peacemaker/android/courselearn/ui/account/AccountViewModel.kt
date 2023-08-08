package com.peacemaker.android.courselearn.ui.account

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.peacemaker.android.courselearn.model.AppUser

class AccountViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val _profilePictureUri = MutableLiveData<Uri>()
    val profilePictureUri: LiveData<Uri>
        get() = _profilePictureUri

    // LiveData to hold the user's name
    private val _name = MutableLiveData<String?>()
    val name: MutableLiveData<String?>
        get() = _name

    init {
        getUserProfile()
    }

    // Function to retrieve the user's profile information
    private fun getUserProfile() {
        getCurrentUser()?.let { user ->
            getUserProfileByUserId(user.uid)
                .addOnSuccessListener { documentSnapshot ->
                    val spareUserProfile = documentSnapshot.toObject(AppUser::class.java)
                    spareUserProfile?.let {
                        _profilePictureUri.postValue(Uri.parse(spareUserProfile.profileImage))
                        _name.postValue(spareUserProfile.name)
                    }
                }.addOnFailureListener {
                    // Failed to retrieve user profile
                }
        }
    }

    private fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    private fun getUserProfileByUserId(userId: String): Task<DocumentSnapshot> {
        return fireStore.collection("users").document(userId).get()
    }
}