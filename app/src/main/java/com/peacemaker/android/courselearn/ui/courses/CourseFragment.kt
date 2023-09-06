package com.peacemaker.android.courselearn.ui.courses

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseListItemsBinding
import com.peacemaker.android.courselearn.databinding.FragmentCourseBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.adapters.PagerAdapter
import com.peacemaker.android.courselearn.ui.adapters.PagerItemsListFragment
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.model.CourseCard
import com.peacemaker.android.courselearn.ui.home.CourseCardAdapter
import com.peacemaker.android.courselearn.ui.search.SearchFragment
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class CourseFragment : BaseFragment() {
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy { RecyclerBaseAdapter<CoursesItem>() }

    companion object {
        fun newInstance() = CourseFragment()
    }

    private lateinit var viewModel: CourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCourseBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        setCarousel()
        setPagerItems()
        //setRecyclerViewItems()
    }

    private fun setCarousel(){
        binding.apply {
            val cards = listOf(
                CourseCard(
                    bgColor = resources.getColor(R.color.default_card_bg_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
            )
            val cardAdapter = CourseCardAdapter(cards)
            viewPager.adapter = cardAdapter
            viewPager.offscreenPageLimit = 3
            dotsIndicator.attachTo(viewPager)
        }

    }
    @SuppressLint("InflateParams")
    private fun setPagerItems(){
        val tabLayout = binding.tabL
        val viewPager = binding.viewPager2
        val tabTitles = listOf("All", "Popular", "New") // Replace with your tab titles
        val adapter = PagerAdapter(fragmentActivity = requireActivity())
        val bundle = Bundle()
        for (title in tabTitles) {
            when(title){
                "All"->{
                    bundle.apply { bundle.putString("title",title) }
                    adapter.addFragment(PagerItemsListFragment.newInstance(bundle), title)
                }

                "Popular"->{
                    bundle.apply { bundle.putString("title",title) }
                    adapter.addFragment(PagerItemsListFragment.newInstance(bundle), title)
                }

                "New"->{
                    bundle.apply { bundle.putString("title",title) }
                    adapter.addFragment(PagerItemsListFragment.newInstance(bundle), title)
                }
            }
        }

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTab = layoutInflater.inflate(R.layout.custom_tab_item, null)
            val tabTextView = customTab.findViewById<TextView>(android.R.id.text1)
            tabTextView.text = tabTitles[position]
            tab.text = adapter.getPageTitle(position)
            // Set tab text colors based on selection state
            if (tab.isSelected) {
                tabTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.selectedTabTextColor))
                customTab.setBackgroundResource(R.drawable.selected_tab_background)
                tab.view.setBackgroundColor(R.drawable.selected_tab_background)
            } else {
                tabTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.unselectedTabTextColor))
                customTab.setBackgroundResource(R.drawable.unselected_tab_background)
                tab.view.setBackgroundColor(R.drawable.unselected_tab_background)
            }
            tab.customView = customTab
        }.attach()
    }
    private fun setRecyclerViewItems() {
        val firestore = FirebaseFirestore.getInstance()
        val collectionName = "courses"
        FirebaseHelper.DocumentCollection()
            .getDocumentsByType(firestore, collectionName, CoursesItem::class.java) { _, result->
                printLogs("$SearchFragment", "$result")
                mAdapter.submitList(result)
                mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
                    CourseListItemsBinding.inflate(inflater, viewGroup, false)
                }
                mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
                    val view = viewBinding as CourseListItemsBinding
                    view.courseName.text = eachItem.courseName
                    view.coursePrice.text = eachItem.price
                    view.authorName.text = eachItem.authorName
                    view.courseDuration.text = eachItem.duration?.hours.toString().plus(":")
                        .plus(eachItem.duration?.minutes).plus("hs")
                    view.root.setOnClickListener {
                        //navigateTo(R.id.action)
                        val bundle = Bundle().apply {
                            putParcelable("course",eachItem)
                        }
                        navigateTo(R.id.action_global_courseDetailsFragment, bundle)
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
                        //mAdapter.filter(s.toString())
                        mAdapter.filter.filter(s)
                    }

                    override fun afterTextChanged(s: Editable?) {
                       // mAdapter.filter(s.toString())
                    }
                })
            }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}