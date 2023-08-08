package com.peacemaker.android.courselearn.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentCourseBinding
import com.peacemaker.android.courselearn.ui.adapters.PagerAdapter
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class CourseFragment : BaseFragment() {
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!

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

        val tabLayout = binding.tabL
        val viewPager = binding.viewPager2

        val tabTitles = listOf("All", "Popular", "New") // Replace with your tab titles
        val adapter = PagerAdapter(fragmentActivity = requireActivity())
        for (title in tabTitles) {
            adapter.addFragment(CourseCategoryFragment(), title) // Replace YourFragment with your actual fragment class
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}