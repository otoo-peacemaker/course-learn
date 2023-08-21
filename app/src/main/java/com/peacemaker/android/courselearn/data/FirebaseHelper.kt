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
import com.peacemaker.android.courselearn.model.CoursesItem
import kotlinx.coroutines.tasks.await


object FirebaseHelper {
    class UserDataCollection {
        private val firestore = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()

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

        /***
         * A function to store user-specific data under a particular user
         * @param userData Data object type
         * @param collectionName where data to be stored
         * @param subCollectionName, which represents the name of the sub collection where you want to store the user data.
         * @param callback to handle response from the API
         * */

        fun <T : Any> addUserData(
            userData: T,
            collectionName: String,
            subCollectionName: String,
            callback: (Boolean, String) -> Unit) {
            val currentUser = auth.currentUser
            currentUser?.let {
                val userSubCollectionRef = firestore.collection(collectionName)
                    .document(currentUser.uid)
                    .collection(subCollectionName)
                // Generate a unique document ID for the data entry
                val dataDocumentRef = userSubCollectionRef.document()
                // Set the user data in the document
                dataDocumentRef.set(userData)
                    .addOnSuccessListener {
                        // Data added successfully
                        callback.invoke(true, "added successfully")
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        callback.invoke(false, exception.localizedMessage!!)
                    }
            }
        }
    }

    class DocumentCollection {
        private val db = FirebaseFirestore.getInstance()
        private val firestore = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()
        fun <T : Any> addDocumentsToCollection(
            collectionName: String,
            documents: List<T>,
            callback: (Boolean, String) -> Unit) {
            val collection = db.collection(collectionName)
            for (document in documents) {
                collection.add(document)
                    .addOnSuccessListener { documentReference ->
                        // Document added successfully
                        callback(true, "Document added successfully")
                    }
                    .addOnFailureListener { e ->
                        // Handle error
                        callback(false, "Error adding collection: ${e.localizedMessage}")
                    }
            }
        }

        // Define a generic function to get documents of a particular data class type
        inline fun <reified T> getDocumentsByType(
            fireStore: FirebaseFirestore, collectionName: String, dataClass: Class<T>,
            crossinline callback: (Boolean, MutableList<T>?) -> Unit
        ) {
            val collectionReference = fireStore.collection(collectionName)
            val result = mutableListOf<T>()
            collectionReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val docData = document.toObject(dataClass)
                        result.add(docData)
                    }
                    callback.invoke(true, result)
                } else {
                    callback.invoke(false, result)
                }
            }
        }

        suspend fun <T> getCollection(collectionName: String, clazz: Class<T>): List<T> {
            val collection = db.collection(collectionName).get().await()
            return collection.toObjects(clazz)
        }

        fun <T> updateDocument(
            collectionRef: CollectionReference,
            documentId: String,
            fieldName: String,
            fieldValue: T,
            onComplete: (Boolean, String) -> Unit) {
            val documentRef = collectionRef.document(documentId)
            documentRef.update(fieldName, fieldValue)
                .addOnSuccessListener {
                    onComplete(true, "Document $documentId updated successfully")
                    Log.d(TAG, "::::::::::::::::Document $documentId updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                    onComplete(false, e.localizedMessage!!)
                }
        }
    }
}

