package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentVerifyPhoneNumberBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import com.peacemaker.android.courselearn.ui.util.VerificationStatus

class VerifyPhoneNumberFragment : BaseFragment() {
    private var _binding: FragmentVerifyPhoneNumberBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    companion object {
        fun newInstance() = VerifyPhoneNumberFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyPhoneNumberBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.verifyAccount, "Verify and Create Account") {
            // navigateTo(R.id.action_global_home_graph
            //TODO: IMPLEMENT NUMBER VERIFICATION
            navigateTo(R.id.action_verifyAccountFragment_to_successFragment)
           verifyAccountNumber()
        }
    }

    private fun verifyAccountNumber(){
        val phoneNumber = "+233207043114"
        // Call the verifyPhoneNumber function to start the verification process.
        viewModel.verifyPhoneNumber(phoneNumber)
        viewModel.verificationStatus.observe(this) { status ->
            when (status) {
                is VerificationStatus.CodeSent -> {
                    // Save the verificationId and token to use later when the user enters the OTP.
                    val verificationId = status.verificationId
                    val token = status.token
                    // Display an OTP input field and prompt the user to enter the OTP.
                    // Once the OTP is entered, call signInWithPhoneAuthCredential function with the credential.
                }
                is VerificationStatus.Success -> {
                    val user = status.user
                    // User is signed in successfully.
                    // Proceed with your app's logic after successful phone number verification.
                    navigateTo(R.id.action_verifyAccountFragment_to_successFragment)
                }
                is VerificationStatus.Error -> {
                    val exception = status.exception
                    // Handle the error, show a message, or take appropriate action.
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}