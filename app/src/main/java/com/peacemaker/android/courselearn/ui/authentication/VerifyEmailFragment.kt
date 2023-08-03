package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentVerifyEmailBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class VerifyEmailFragment : BaseFragment() {
    private var _binding: FragmentVerifyEmailBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = VerifyEmailFragment()
    }

    private val viewModel: AuthViewModel by viewModels()
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
            val email = binding.emailId.text.toString()
           resetPassword(email)
        }
    }

    private fun resetPassword(email: String) {

    }

}