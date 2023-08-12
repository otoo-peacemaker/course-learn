package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentVerifyEmailBinding
import com.peacemaker.android.courselearn.ui.authentication.ResetPasswordFragmentDirections.Companion.actionResetPasswordFragmentToSuccessFragment
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.continueBtn, "Continue") {
            binding.emailId.setText(args.email)
            val email = binding.emailId.text.toString()
            verifyEmail(email)
        }
    }

    private fun verifyEmail(email:String) {
        val user = FirebaseAuth.getInstance().currentUser
        viewModel.sendEmailVerification(user,requireContext())
        ResetPasswordFragmentDirections
        observeLiveDataResource(viewModel.resetPasswordLiveData, {
            navigateTo(R.id.action_verifyEmailFragment_to_successFragment)
            val action = VerifyEmailFragmentDirections.actionVerifyEmailFragmentToSuccessFragment(it,email)
            navigateTo(action)
        }, binding.loader)
    }

}