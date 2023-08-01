package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentLoginBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class LoginFragment : BaseFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LoginFragment()
    }

//    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
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
            // navigateTo(R.id.action_global_home_graph)
            login()
        }

        binding.signUpBtn.setOnClickListener {
            navigateTo(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.forgotPassword.setOnClickListener {
            navigateTo(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }

    private fun login() {
        showLoadingScreen(binding.loader, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}