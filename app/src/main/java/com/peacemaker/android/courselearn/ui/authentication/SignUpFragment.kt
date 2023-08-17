package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentSignUpBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class SignUpFragment : BaseFragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextViewPartialColor(binding.loginBtn, getString(R.string.already_have_an_account_login),
            "Login", resources.getColor(R.color.primary, null))
        setAppButton(binding.createAcc, "Create account") {
            if (binding.terms.isChecked) signUp() else showSnackBar(requireView(),"Please accept our terms and conditions")
            observeLiveDataResource(viewModel.createUserLiveData, {
                val email = binding.emailId.text.toString()
                printLogs("$SignUpFragment::::::::::", email)
                val action = SignUpFragmentDirections.actionSignUpFragmentToVerifyEmailFragment(email)
                navigateTo(action)
            }, binding.loader)
        }

        binding.loginBtn.setOnClickListener {
            navigateTo(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun signUp() {
        with(binding) {
            val firstname = firstNameId.text.toString()
            val lastName = lastNameId.text.toString()
            val email = emailId.text.toString()
            val phone = phoneNumId.text.toString()
            val password = passwordId.text.toString()
            if (validateEmailAndPassword(email, password) { showSnackBar(requireView(), it) }) {
                if (validateString(firstname) and validateString(lastName) and validateString(email) and validateString(password)) {
                    viewModel.createUser(
                        username = firstname.plus(" ").plus(lastName),
                        email = email,
                        phone = phone,
                        password = password,requireContext()
                    )
                } else {
                    showSnackBar(requireView(), "Field can not be empty or string too short")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}