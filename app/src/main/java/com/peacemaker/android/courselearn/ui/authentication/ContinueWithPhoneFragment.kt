package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peacemaker.android.courselearn.databinding.FragmentContinueWithPhoneBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class ContinueWithPhoneFragment : BaseFragment() {
    private var _binding: FragmentContinueWithPhoneBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ContinueWithPhoneFragment() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContinueWithPhoneBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.continueBtn, "Continue") {
            verifyPhoneNumber()
        }
    }

    private fun verifyPhoneNumber() {
        val phoneNumber = binding.phoneNumId.text.toString()
        printLogs("phoneNumber", phoneNumber)
        if (isValidPhoneNumber(phoneNumber)) {
            val action =
                ContinueWithPhoneFragmentDirections.actionContinueWithPhoneFragmentToVerifyAccountFragment(
                    phoneNumber
                )
            navigateTo(action)
        } else {
            showSnackBar(requireView(), "Invalid phone number")
        }
    }

}