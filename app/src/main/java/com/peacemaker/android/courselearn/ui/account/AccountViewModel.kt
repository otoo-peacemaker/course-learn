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
import com.google.firebase.firestore.QuerySnapshot
import com.peacemaker.android.courselearn.model.AppUser
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.util.Resource

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

    private val _userRelatedData = MutableLiveData<Resource<List<CoursesItem>?>>()
    val userRelatedData: LiveData<Resource<List<CoursesItem>?>> = _userRelatedData

    init {
        getUserProfile()
        loadUserRelatedData()
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

    private fun loadUserRelatedData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userCollectionRef = fireStore.collection("users").document(userId).collection("my_courses")

            userCollectionRef.get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                    val results = querySnapshot.toObjects(CoursesItem::class.java)
                    _userRelatedData.value = Resource.success(results)
                }
                .addOnFailureListener { e ->
                    _userRelatedData.value = e.localizedMessage?.let { Resource.error(null, it) }
                }
        } else {
            // User is not authenticated
            // Handle accordingly
        }
    }
}