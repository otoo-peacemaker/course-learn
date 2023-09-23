package com.peacemaker.android.courselearn.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseListItemsBinding
import com.peacemaker.android.courselearn.databinding.FragmentSearchBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class SearchFragment : BaseFragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val mAdapter by lazy { RecyclerBaseAdapter<CoursesItem>() }
    private lateinit var viewModel: SearchViewModel

    companion object {
        fun newInstance() = SearchFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerViewItems()
    }

    private fun setRecyclerViewItems() {
        val store = FirebaseFirestore.getInstance()
        val collectionName = "courses"

        FirebaseHelper.DocumentCollection()
            .getDocumentsByType(store, collectionName, CoursesItem::class.java) {message, result->
                printLogs("$SearchFragment", "$result")
                mAdapter.submitList(result)
                if (result ==null) startShimmerEffect(binding.coursesShimmer.coursesShimmer)
                else {
                    stopShimmerEffect(binding.coursesShimmer.coursesShimmer)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.coursesShimmer.coursesShimmer.visibility = View.GONE
                }
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
                        loadImageToImageView(requireContext(),eachItem.courseBgImg,courseImg,R.drawable.loading_animation,R.drawable.avatar_person)
                        val bundle = Bundle().apply {
                            putParcelable("course",eachItem)
                        }
                        view.root.setOnClickListener {
                            navigateTo(R.id.action_searchFragment_to_courseDetailsFragment, bundle = bundle)
                        }
                    }
                }
                val recyclerView = binding.recyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = mAdapter
                val searchEditText = binding.searchBar.searchView
                searchEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                // TODO("Not yet implemented")
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        mAdapter.filter(s.toString())
                        //mAdapter.filter.filter(s)
                    }

                    override fun afterTextChanged(s: Editable?) {
                        mAdapter.filter(s.toString())
                    }
                })
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}