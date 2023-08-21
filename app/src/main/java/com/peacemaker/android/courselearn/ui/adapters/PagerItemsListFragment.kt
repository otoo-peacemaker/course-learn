package com.peacemaker.android.courselearn.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.peacemaker.android.courselearn.databinding.FragmentPagerItemsListBinding
import com.peacemaker.android.courselearn.ui.courses.CourseViewModel
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class PagerItemsListFragment : BaseFragment() {
    private var _binding: FragmentPagerItemsListBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = PagerItemsListFragment()
    }

    private lateinit var viewModel: CourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerItemsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
    }

}