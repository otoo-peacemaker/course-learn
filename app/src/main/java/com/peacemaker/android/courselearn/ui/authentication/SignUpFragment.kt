package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentSignUpBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class SignUpFragment : BaseFragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SignUpFragment()
    }

   // private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setTextViewPartialColor(binding.loginBtn,getString(R.string.already_have_an_account_login),"Login",resources.getColor(R.color.primary, null))
        setAppButton(binding.createAcc,"Create account"){
            signUp()

        }

        binding.loginBtn.setOnClickListener {
            navigateTo(R.id.action_signUpFragment_to_loginFragment)
        }
    }


    //TODO: CREATE ACCOUNT
    private fun signUp(){
        showLoadingScreen(binding.loader,true)
        navigateTo(R.id.action_signUpFragment_to_continueWithPhoneFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}