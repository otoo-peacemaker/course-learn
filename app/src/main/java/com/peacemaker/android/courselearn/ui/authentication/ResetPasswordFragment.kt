package com.peacemaker.android.courselearn.ui.authentication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentLoginBinding
import com.peacemaker.android.courselearn.databinding.FragmentResetPasswordBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class ResetPasswordFragment : BaseFragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ResetPasswordFragment()
    }

    private val viewModel: AuthViewModel by viewModels()
    private val args: ResetPasswordFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        binding.emailId.setText(args.email).toString()
        printLogs("emailArgs",args.email)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.continueBtn, "Continue") {
            val email = binding.emailId.text.toString()
            resetPassword(email)
        }
    }

    private fun resetPassword(email: String) {
        viewModel.resetPassword(email)
        observeLiveDataResource(viewModel.resetPasswordLiveData, {
            val action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToSuccessFragment(it,email)
            findNavController().navigate(action)
           // navigateTo(R.id.action_resetPasswordFragment_to_successFragment)
        }, binding.loader)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}