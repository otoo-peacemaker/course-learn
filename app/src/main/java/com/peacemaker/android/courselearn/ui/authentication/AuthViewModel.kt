package com.peacemaker.android.courselearn.ui.authentication

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.peacemaker.android.courselearn.model.AppUser
import com.peacemaker.android.courselearn.ui.util.Constants.RC_SIGN_IN
import com.peacemaker.android.courselearn.ui.util.Resource

class AuthViewModel : ViewModel() {

    private var auth = FirebaseAuth.getInstance()

    private val _createUserLiveData = MutableLiveData<Resource<FirebaseUser>?>()
    val createUserLiveData: MutableLiveData<Resource<FirebaseUser>?> = _createUserLiveData

    private val _signInLiveData = MutableLiveData<Resource<FirebaseUser>>()
    val signInLiveData: LiveData<Resource<FirebaseUser>> = _signInLiveData

    fun createUser(username:String, email: String, phone: String, password: String) {
        _createUserLiveData.value = Resource.loading(null)
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User account created successfully
                        val firebaseUser = task.result?.user
                        val userId = firebaseUser?.uid
                        val db = Firebase.firestore

                        // Save additional user information in the Fire store database
                        val spareUser = AppUser(id = userId!!, name = username, email=email, phoneNumber =phone, password = password, profileImage = "")
                        db.collection("users").document(userId).set(spareUser)
                            .addOnSuccessListener {
                                _createUserLiveData.value = Resource.success(firebaseUser)
                            }
                            .addOnFailureListener { e ->
                                _createUserLiveData.value =
                                    e.localizedMessage?.let { Resource.error(firebaseUser, it) }
                            }
                    } else {
                        // Handle error
                        _createUserLiveData.value =
                            task.exception?.localizedMessage?.let { Resource.error(null, it) }
                    }
                }
        }catch (e: FirebaseAuthInvalidUserException){
            _createUserLiveData.postValue(e.message?.let { Resource.error(null, it) })
        }catch (e:RuntimeExecutionException){
            _createUserLiveData.postValue(e.localizedMessage?.let { Resource.error(null, it) })
        }catch (e:IllegalArgumentException){
            _createUserLiveData.postValue(e.localizedMessage?.let { Resource.error(null, it) })
        }

    }
    fun signIn(email: String, password: String) {
        _signInLiveData.value = Resource.loading(null)
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User signed in successfully
                        _signInLiveData.value = auth.currentUser.let { Resource.success(it) }
                    }
                }.addOnFailureListener {
                    _signInLiveData.value = it.message?.let { it1 -> Resource.error(null, it1) }
                }
        }
        catch (e: FirebaseAuthInvalidUserException){
            _signInLiveData.postValue(e.message?.let { Resource.error(null, it) })
        }catch (e:RuntimeExecutionException){
            _signInLiveData.postValue(e.localizedMessage?.let { Resource.error(null, it) })
        }catch (e:IllegalArgumentException){
            _signInLiveData.postValue(e.localizedMessage?.let { Resource.error(null, it) })
        }

    }

    fun signInWithGoogle(activity: Activity) {
        val googleSignInClient = GoogleSignIn.getClient(activity.applicationContext,
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    fun handleGoogleSignInResult(data: Intent?, onSuccess: (GoogleSignInAccount) -> Unit, onFailure: () -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { taskListener ->
                if (taskListener.isSuccessful) {
                    onSuccess.invoke(account)
                } else {
                    onFailure.invoke()
                }
            }
        } catch (e: ApiException) {
            onFailure.invoke()
        }
    }

    fun signOut(route :()->Unit) {
        if (auth.currentUser !=null ){
            auth.signOut()
            _createUserLiveData.postValue(null)
            route.invoke()
        }
    }
}
