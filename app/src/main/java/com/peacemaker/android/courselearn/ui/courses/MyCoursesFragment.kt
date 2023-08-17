package com.peacemaker.android.courselearn.ui.courses

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.CourseListItemsBinding
import com.peacemaker.android.courselearn.databinding.FragmentCourseBinding
import com.peacemaker.android.courselearn.databinding.FragmentMyCoursesBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class MyCoursesFragment : BaseFragment() {
    private var _binding: FragmentMyCoursesBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy { RecyclerBaseAdapter<CoursesItem>() }

    companion object {
        fun newInstance() = MyCoursesFragment()
    }

    private lateinit var viewModel: CourseViewModel
    private  var coursesItem: MutableList<CoursesItem>?=null

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
        arguments?.apply {
            coursesItem = getParcelable("course")!!
        }

        setUpRecyclerView()

    }

    private fun setUpRecyclerView(){
        mAdapter.submitList(coursesItem)
        mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
            CourseListItemsBinding.inflate(inflater, viewGroup, false)
        }

        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as CourseListItemsBinding
            view.apply {
                courseName.text = eachItem.courseName
                coursePrice.text = eachItem.price
                authorName.text = eachItem.authorName
                courseDuration.text = eachItem.duration?.hours.toString().plus(":").plus(eachItem.duration?.minutes).plus("hrs")

                val bundle = Bundle().apply {
                    putParcelable("course",eachItem)
                }
                view.root.setOnClickListener {
                    navigateTo(R.id.action_searchFragment_to_courseDetailsFragment, bundle = bundle)
                }
            }
        }
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = CarouselLayoutManager()
//        GridLayoutManager(requireContext(),2)
        recyclerView.adapter = mAdapter

    }

}