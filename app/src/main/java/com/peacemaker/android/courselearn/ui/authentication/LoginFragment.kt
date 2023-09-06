package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.FragmentLoginBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class LoginFragment : BaseFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val firebaseUser = FirebaseHelper.UserDataCollection()
    private val database = FirebaseDatabase.getInstance()
    val onlineStatusRef = database.getReference("online_status")

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
       // insertDummyData()
        val currentUser = firebaseUser.getCurrentUser()
        firebaseUser.addOnlineUsers(currentUser)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextViewPartialColor(
            binding.signUpBtn,
            getString(R.string.don_t_have_an_account_sign_up),
            "Sign up",
            resources.getColor(R.color.primary, null)
        )
        setAppButton(binding.loginBtn, "Log in") {
            login()
            observeLiveDataResource(viewModel.signInLiveData, {
                navigateTo(R.id.action_global_home_graph)
            },
                binding.loader, onError = {
                    val unVerifyEmail = "user with ${binding.emailId.text} is not verified"
                    printLogs("LoginFragment",unVerifyEmail)
                    if (it==unVerifyEmail) {
                        showRetrySnackBar(requireView(),it,"Verify"){
                            val email = binding.emailId.text.toString()
                            printLogs("Email",email)
                            val action = LoginFragmentDirections.actionLoginFragmentToVerifyEmailFragment(email = email)
                            navigateTo(action)
                        }
                    }
                })
        }

        binding.signUpBtn.setOnClickListener { navigateTo(R.id.action_loginFragment_to_signUpFragment) }
        binding.forgotPassword.setOnClickListener {
            val email = binding.emailId.text.toString()
            printLogs("Email",email)
            val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment(email = email)
            it.findNavController().navigate(action)
        }
    }
    private fun login() {
        with(binding) {
            val email = emailId.text.toString()
            val password = passwordId.text.toString()
            if (validateString(email) and validateString(password)) viewModel.signIn(email, password)
            else showSnackBar(requireView(), "Field(s) can not be empty or must be greater than 3 characters")
        }
    }
    private fun insertDummyData(){
        val course: List<CoursesItem>? = parseJsonFileToListOfDataClass(requireContext(), "courses.json")
        if (course != null) {
            // Use 'course' object
            printLogs("$LoginFragment","$course")
            FirebaseHelper.DocumentCollection()
                .addDocumentsToCollection("courses",course){success,message->
                    if (success) showSnackBar(requireView(),message) else showSnackBar(requireView(),message)
                }
        } else {
            // Handle parsing failure
            printLogs("$LoginFragment","parsing failure")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}