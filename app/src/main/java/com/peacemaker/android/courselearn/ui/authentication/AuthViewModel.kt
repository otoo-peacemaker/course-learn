package com.peacemaker.android.courselearn.ui.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.model.AppUser
import com.peacemaker.android.courselearn.ui.util.Resource
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.RC_SIGN_IN
import com.peacemaker.android.courselearn.ui.util.VerificationStatus

class AuthViewModel : ViewModel() {

    private var auth = FirebaseAuth.getInstance()
    private val _createUserLiveData = MutableLiveData<Resource<FirebaseUser>>()
    val createUserLiveData: LiveData<Resource<FirebaseUser>> = _createUserLiveData

    private val _signInLiveData = MutableLiveData<Resource<FirebaseUser>>()
    val signInLiveData: LiveData<Resource<FirebaseUser>> = _signInLiveData

    private val _verificationStatus = MutableLiveData<VerificationStatus<Any>>()
    val verificationStatus: LiveData<VerificationStatus<Any>>
        get() = _verificationStatus

    private val _resetPasswordLiveData = MutableLiveData<Resource<String>>()
    val resetPasswordLiveData: LiveData<Resource<String>> = _resetPasswordLiveData


    fun createUser(username: String, email: String,
        phone: String, password: String) {
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
                               // sendEmailVerification(firebaseUser,context)
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
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {fetch->
                if (fetch.isSuccessful){//fetch email from firebase
                       auth.signInWithEmailAndPassword(email, password)
                           .addOnCompleteListener { task ->
                               if (task.isSuccessful) {
                                   // User signed in successfully
                                   if (auth.currentUser?.isEmailVerified == true) {
                                       // Email is verified, proceed to app
                                       _signInLiveData.value = auth.currentUser.let { Resource.success(it) }
                                   } else {
                                       _signInLiveData.value =  Resource.error(null, "user with $email is not verified")
                                   }
                               }
                           }.addOnFailureListener {
                               _signInLiveData.value = it.message?.let { it1 -> Resource.error(null, it1) }
                           }
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
    fun verifyPhoneNumber(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(MainActivity()) // Replace with your activity reference if you need the context
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                     _verificationStatus.value = VerificationStatus.Error(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken) {
                    _verificationStatus.value = VerificationStatus.CodeSent(verificationId, token)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _verificationStatus.value = VerificationStatus.Success(task.result?.user)
                } else {
                    _verificationStatus.value = task.exception?.let { VerificationStatus.Error(it) }
                }
            }
    }
    // Function to reset the user's password
    fun resetPassword(email: String) {
        _resetPasswordLiveData.value= Resource.loading(null)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _resetPasswordLiveData.value = Resource.success("Password reset link successfully sent to $email")
                } else {
                    _resetPasswordLiveData.value =
                        task.exception?.localizedMessage?.let { Resource.error(null, it) }
                }
            }
    }
     fun sendEmailVerification(user: FirebaseUser?, context: Context) {
        _resetPasswordLiveData.value= Resource.loading(null)
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _resetPasswordLiveData.value = Resource.success(" ${context.getString(com.peacemaker.android.courselearn.R.string.verification_message)} ")
                } else {
                    _resetPasswordLiveData.value =
                        task.exception?.localizedMessage?.let { Resource.error(null, it) }
                }
            }
    }
    fun signOut(route :()->Unit) {
        if (auth.currentUser !=null ){
            auth.signOut()
           // _createUserLiveData.postValue(null)
            route.invoke()
        }
    }
}
