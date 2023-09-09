package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.FragmentSignUpBinding
import com.peacemaker.android.courselearn.model.AppUser
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class SignUpFragment : BaseFragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    var message = "You have successfully created account, please check email for account confirmation"
    val time = System.currentTimeMillis()
    private val currentUser = FirebaseHelper.UserDataCollection().getCurrentUser()
    private val firebaseHelper = FirebaseHelper.UserDataCollection()

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextViewPartialColor(binding.loginBtn, getString(R.string.already_have_an_account_login),
            "Login", resources.getColor(R.color.primary, null))
        binding.loginBtn.setOnClickListener {
            navigateTo(R.id.action_signUpFragment_to_loginFragment)
        }

        signUpDataObserver()
    }

    private fun signUpDataObserver(){
        var userDetails: AppUser?=null
//        firebaseHelper.getUserByUID(currentUser?.uid){
//            userDetails = it
//        }
        setAppButton(binding.createAcc, "Create account") {
            if (binding.terms.isChecked) createAccount() else showSnackBar(requireView(),"Please accept our terms and conditions")
            observeLiveDataResource(viewModel.createUserLiveData, {
                val email = binding.emailId.text.toString()
                printLogs("$SignUpFragment::::::::::", email)
                val action = SignUpFragmentDirections.actionSignUpFragmentToVerifyEmailFragment(email)
              //  addMessage(userName = userDetails?.name, status = userDetails?.status, message = message,time,userDetails?.profileImage)
                navigateTo(action)
            }, binding.loader)
        }
    }

    private fun createAccount() {
        with(binding) {
            val firstname = firstNameId.text.toString()
            val lastName = lastNameId.text.toString()
            val email = emailId.text.toString()
            val phone = phoneNumId.text.toString()
            val password = passwordId.text.toString()
            if (validateEmailAndPassword(email, password) { showSnackBar(requireView(), it) }) {
                if (validateString(firstname) and validateString(lastName) and validateString(email) and validateString(password)) {
                    viewModel.createUser(
                        username = firstname.plus(" ").plus(lastName),
                        email = email,
                        phone = phone,
                        password = password,
                    )
                } else {
                    showSnackBar(requireView(), "Field can not be empty or string too short")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}