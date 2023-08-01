package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mukesh.OnOtpCompletionListener
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentVerifyAccountBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class VerifyAccountFragment : BaseFragment() {
    private var _binding: FragmentVerifyAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    companion object {
        fun newInstance() = VerifyAccountFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.verifyAccount, "Verify and Create Account") {
            // navigateTo(R.id.action_global_home_graph
            navigateTo(R.id.action_verifyAccountFragment_to_successFragment)
        }

        verifyAccount()
    }

    fun verifyAccount(){
       val otpView = binding.otpView
        otpView.setOtpCompletionListener(object : OnOtpCompletionListener {
            override fun onOtpCompleted(otp: String?) {
                printLogs("OnOtpCompletionListener","$otp")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}