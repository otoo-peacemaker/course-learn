package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentSuccessBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class SuccessFragment : BaseFragment() {
    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SuccessFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuccessBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        setAppButton(binding.doneBtn, "Done") {
            navigateTo(R.id.action_successFragment_to_loginFragment)
        }
    }

}