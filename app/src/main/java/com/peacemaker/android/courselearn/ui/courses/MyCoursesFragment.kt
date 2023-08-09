package com.peacemaker.android.courselearn.ui.courses

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentCourseBinding
import com.peacemaker.android.courselearn.databinding.FragmentMyCoursesBinding

class MyCoursesFragment : Fragment() {
    private var _binding: FragmentMyCoursesBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = MyCoursesFragment()
    }

    private lateinit var viewModel: CourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCoursesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
    }

}