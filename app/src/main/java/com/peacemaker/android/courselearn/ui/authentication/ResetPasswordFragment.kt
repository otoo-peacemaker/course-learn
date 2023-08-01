package com.peacemaker.android.courselearn.ui.authentication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

//    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}