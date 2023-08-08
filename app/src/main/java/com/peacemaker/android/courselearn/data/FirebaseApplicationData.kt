package com.peacemaker.android.courselearn.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.peacemaker.android.courselearn.model.AppUser
import kotlinx.coroutines.tasks.await


object FirebaseApplicationData{
    object UserDataCollection{
        fun getCurrentUser(): FirebaseUser? {
            return FirebaseAuth.getInstance().currentUser
        }
        fun findUserById(users: List<AppUser>, id: String): AppUser? {
            return users.find { it.id == id }
        }

        fun findUserByName(users: List<AppUser>, name: String): AppUser? {
            return users.find { it.name.equals(name, ignoreCase = true) }
        }

        fun getUserByUID(uid: String, onResult: (AppUser?) -> Unit) {
            val db = Firebase.firestore
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(AppUser::class.java)
                        onResult(user)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting user by UID", exception)
                    onResult(null)
                }
        }

        fun getUsersCollection(): CollectionReference {
            return Firebase.firestore.collection("users")
        }

        //Save user data when user sign in with google
        fun saveUserProfileWithGoogle(user: AppUser): Task<Void> {
            val db = FirebaseFirestore.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            return db
                .collection("users")
                .document(uid)
                .set(user, SetOptions.merge())
        }

        fun getAccountBalanceByUID(uid: String, callback: (Double?) -> Unit) {
            val firestore = Firebase.firestore
            val docRef = firestore.collection("users").document(uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(AppUser::class.java)
                    if (user != null) {
                        // val accountBalance = user.accBalance?:0.0
                        //   callback.invoke(accountBalance)
                    } else {
                        Log.d(TAG, "User is null")
                    }
                } else {
                    Log.d(TAG, "Document does not exist")
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting user account balance: ", exception)
            }
        }
    }
    object DocumentCollection{
        suspend fun <T> getCollection(collectionName: String, clazz: Class<T>): List<T> {
            val fireStore = FirebaseFirestore.getInstance()
            val collection = fireStore.collection(collectionName).get().await()
            return collection.toObjects(clazz)
        }


        fun <T> updateDocument(collectionRef: CollectionReference, documentId: String, fieldName: String, fieldValue: T, onComplete: () -> Unit) {
            val documentRef = collectionRef.document(documentId)
            documentRef.update(fieldName, fieldValue)
                .addOnSuccessListener {
                    onComplete()
                    Log.d(TAG, "::::::::::::::::Document $documentId updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                }
        }
    }
}






