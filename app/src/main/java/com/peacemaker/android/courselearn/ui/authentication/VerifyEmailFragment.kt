package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.FragmentVerifyEmailBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class VerifyEmailFragment : BaseFragment() {
    private var _binding: FragmentVerifyEmailBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = VerifyEmailFragment()
    }

    private val viewModel: AuthViewModel by viewModels()
    private val args: VerifyEmailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyEmailBinding.inflate(layoutInflater)
        binding.emailId.setText(args.email).toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.continueBtn, "Continue") {
            val email = binding.emailId.text.toString()
            printLogs("$VerifyEmailFragment::::::::::", email)
            verifyEmail()
        }
    }

    private fun verifyEmail() {
       // val user = FirebaseAuth.getInstance().currentUser
        viewModel.sendEmailVerification(FirebaseHelper.UserDataCollection().getCurrentUser(),requireContext())
        val email = binding.emailId.text.toString()
        printLogs("$VerifyEmailFragment::::::::::", email)
        observeLiveDataResource(viewModel.resetPasswordLiveData, {
            val action = VerifyEmailFragmentDirections.actionVerifyEmailFragmentToSuccessFragment(it,email)
            navigateTo(action)
        }, binding.loader)
    }

}