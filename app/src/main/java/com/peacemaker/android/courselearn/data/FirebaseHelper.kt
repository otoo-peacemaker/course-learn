package com.peacemaker.android.courselearn.data

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.peacemaker.android.courselearn.model.AppUser
import kotlinx.coroutines.tasks.await
import java.net.UnknownHostException

object FirebaseHelper {
    class UserDataCollection {
        private val fireStore = FirebaseFirestore.getInstance()
        private var db = FirebaseDatabase.getInstance()
        private var usersListRef: DatabaseReference = db.getReference("users")
        var onlineStatus: DatabaseReference? = null
        private var connectedRef: DatabaseReference? = null
        private var userListValueEventListener: ValueEventListener? = null
        var userListItems = mutableListOf <String>()
        var adapter: ArrayAdapter<String>? = null

        fun getCurrentUser(): FirebaseUser? {
            return FirebaseAuth.getInstance().currentUser
        }
        fun findUserById(users: List<AppUser>, id: String): AppUser? {
            return users.find { it.id == id }
        }
        fun findUserByName(users: List<AppUser>, name: String): AppUser? {
            return users.find { it.name.equals(name, ignoreCase = true) }
        }
        fun getUserByUID(uid: String?, onResult: (AppUser?) -> Unit) {
            val db = Firebase.firestore
            db.collection("users").document(uid!!).get()
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
            val docRef = fireStore.collection("users").document(uid)
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

            try {
                val currentUser = getCurrentUser()
                currentUser?.let {
                    val userSubCollectionRef = fireStore.collection(collectionName)
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
            }catch (e:UnknownHostException){
                e.localizedMessage?.let { callback.invoke(false, it) }
            }catch (e:FirebaseException){
                e.cause?.let { it.localizedMessage?.let { it1 -> callback.invoke(false, it1) } }
            }

        }
        fun addOnlineUsers(currentUser: FirebaseUser?) {
            if (currentUser != null) {
                usersListRef.child(currentUser.uid).setValue(AppUser(name= currentUser.displayName, status = "Online"))
                onlineStatus = db.getReference("users/" + currentUser.uid + "/onlineStatus")
                connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
                connectedRef?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val connected = snapshot.getValue(Boolean::class.java)!!
                        if (connected) {
                            onlineStatus?.onDisconnect()?.setValue("offline")
                            onlineStatus?.setValue("Online")
                        } else {
                            onlineStatus?.setValue("offline")
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }


        }
        fun showOnlineUsers(onlineUsers :(MutableList<String>?)->Unit) {
            userListValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userListItems.clear()
                    //first check datasnap shot exist
                    //then add all users except current/self user
                    if (dataSnapshot.exists()) {
                        for (ds in dataSnapshot.children) {
                            if (ds.exists() && ds.key != getCurrentUser()!!.uid) {
                                val name = ds.child("name").getValue(String::class.java)!!
                                val onlineStatus = ds.child("onlineStatus").getValue(String::class.java)!!
                                userListItems.add("$name status : $onlineStatus")
                            }
                        }
                    }
                    onlineUsers.invoke(userListItems)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
            usersListRef.addValueEventListener(userListValueEventListener!!)
        }
        fun removeUserEventListener(){
            usersListRef.removeEventListener(userListValueEventListener!!)
        }
        fun checkUserOnlineStatus(callback: (Boolean, String?) -> Unit) {
            val userId = getCurrentUser()?.uid
            if (userId != null) {
                val onlineStatusRef = db.getReference("online_status")
                val userStatusRef = onlineStatusRef.child(userId)
                val userDocumentRef = fireStore.collection("users").document(userId)
                userStatusRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val isOnline = snapshot.getValue(Boolean::class.java) ?: false
                        // Retrieve the user's name from Fire store
                        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
                            val userName = documentSnapshot.getString("name")
                            callback(isOnline, userName)
                        }.addOnFailureListener {
                            callback(isOnline, null) // Handle the error here
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error if necessary
                        callback(false, null) // Set the default value to false and null username in case of an error
                    }
                })
            } else {
                // User is not authenticated or user ID is null
                callback(false, null)
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

        /**
               *  @param collectionRef the collection path eg users
                * @param documentId user id
                * @param fieldName the name of the to update
                * @param fieldValue generic value for update
                * @param onComplete callback to determine successful trx
                *
                * */
        fun <T> updateDocument(
            collectionRef: CollectionReference,
            documentId: String?, fieldName: String?,
            fieldValue: T, onComplete: (Boolean, String) -> Unit) {
            val documentRef = collectionRef.document(documentId!!)
            documentRef.update(fieldName!!, fieldValue)
                .addOnSuccessListener {
                    onComplete(true, "Document $documentId updated successfully")
                    Log.d(TAG, "::::::::::::::::Document $documentId updated successfully")}
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                    onComplete(false, e.localizedMessage!!)
                }
        }
    }
    object CloudMessagingServices{
        fun getFCMToken(callback: (Boolean, String) -> Unit){
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    callback.invoke(false,"Fetching FCM registration token failed")
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                callback.invoke(true,token)
            })
        }

    }

}

