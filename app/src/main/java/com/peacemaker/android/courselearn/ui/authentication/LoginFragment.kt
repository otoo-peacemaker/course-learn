package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentLoginBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class LoginFragment : BaseFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: AuthViewModel by viewModels()

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
            login()
            observeLiveDataResource(viewModel.signInLiveData, {
                navigateTo(R.id.action_global_home_graph)
            }, binding.loader)
        }

        binding.signUpBtn.setOnClickListener {
            navigateTo(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.forgotPassword.setOnClickListener {
            val email = binding.emailId.text.toString()
            printLogs("Email",email)
            val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment(email = email)
            it.findNavController().navigate(action)
          //  findNavController().navigate(action)
            //navigateTo(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }

    private fun login() {
        with(binding) {
            val email = emailId.text.toString()
            val password = passwordId.text.toString()
            if (validateString(email) and validateString(password)) viewModel.signIn(email, password)
            else showSnackBar(requireView(), "Field(s) can not be empty or must be greater than 3 characters")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}