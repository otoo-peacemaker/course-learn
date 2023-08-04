package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
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

    private val args: VerifyPhoneNumberFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyPhoneNumberBinding.inflate(layoutInflater)
        // Call the verifyPhoneNumber function to start the verification process.
        viewModel.verifyPhoneNumber(args.number)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.verifyAccount, "Verify and Create Account") {
            navigateTo(R.id.action_verifyAccountFragment_to_successFragment)
            verifyAccountNumber()
        }
    }

    private fun verifyAccountNumber() {
        viewModel.verificationStatus.observe(this) { status ->
            when (status) {
                is VerificationStatus.CodeSent -> {
                    //TODO: IMPLEMENT NUMBER VERIFICATION
                    // Save the verificationId and token to use later when the user enters the OTP.
                    // Display an OTP input field and prompt the user to enter the OTP.
                    // Once the OTP is entered, call signInWithPhoneAuthCredential function with the credential.
                    val verificationId = status.verificationId
                    val token = status.token
                    printLogs("verificationId:::",verificationId)
                    printLogs("token:::::::::::","$token")
                }
                is VerificationStatus.Success -> {
                    //TODO: Success
                    // User is signed in successfully.
                    // Proceed with your app's logic after successful phone number verification.
                    val user = status.user
                    navigateTo(R.id.action_verifyAccountFragment_to_successFragment)
                }

                is VerificationStatus.Error -> {
                    val exception = status.exception
                    exception.localizedMessage?.let { showSnackBar(requireView(), it) }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}