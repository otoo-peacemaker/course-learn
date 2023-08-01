package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peacemaker.android.courselearn.databinding.FragmentBoardingScreenBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class BoardingScreenFragment : BaseFragment() {

    private var _binding: FragmentBoardingScreenBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = BoardingScreenFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardingScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}