package com.peacemaker.android.courselearn.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentMyCoursesBinding
import com.peacemaker.android.courselearn.databinding.MyCourseListItemBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.account.AccountViewModel
import com.peacemaker.android.courselearn.ui.adapters.GridSpacingItemDecoration
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.adapters.RecyclerViewItemDecoration
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class MyCoursesFragment : BaseFragment() {
    private var _binding: FragmentMyCoursesBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy { RecyclerBaseAdapter<CoursesItem>() }
    private var coursesItem = mutableListOf<CoursesItem>()

    companion object {
        fun newInstance() = MyCoursesFragment()
    }

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCoursesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setUpRecyclerView() {
        viewModel.loadUserRelatedData("my_courses",CoursesItem::class.java)
        observeLiveDataResource(viewModel.userRelatedData, { items ->
            printLogs("MyCoursesFragment", "$items")
            coursesItem= items as MutableList<CoursesItem>
            mAdapter.submitList(items)
        })

        mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
            MyCourseListItemBinding.inflate(inflater, viewGroup, false)
        }
        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as MyCourseListItemBinding
            view.apply {
                courseName.text = eachItem.courseName
                val bundle = Bundle().apply {
                    putParcelable("course", eachItem)
                }
                view.root.setOnClickListener {
                    navigateTo(R.id.action_myCoursesFragment_to_classroomFragment, bundle = bundle)
                }
            }
        }
        val spanCount = 2
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing) // Define your desired spacing in pixels
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = StaggeredGridLayoutManager(spanCount, 1)
        val itemDecoration = RecyclerViewItemDecoration(spanCount, spacingInPixels)
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.adapter = mAdapter

    }

}