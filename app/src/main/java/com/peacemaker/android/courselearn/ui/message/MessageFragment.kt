package com.peacemaker.android.courselearn.ui.message

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.tabs.TabLayoutMediator
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentMessagesBinding
import com.peacemaker.android.courselearn.ui.adapters.PagerAdapter
import com.peacemaker.android.courselearn.ui.adapters.PagerItemsListFragment
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class MessageFragment : BaseFragment() {
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPagerItems()
    }

    @SuppressLint("InflateParams")
    private fun setPagerItems() {
        val tabLayout = binding.tabL
        val viewPager = binding.viewPager2
        val tabTitles = listOf("Messages", "Notifications") // Replace with your tab titles
        val adapter = PagerAdapter(fragmentActivity = requireActivity())
        for (title in tabTitles) {
            adapter.addFragment(PagerItemsListFragment(), title)
        }

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTab = layoutInflater.inflate(R.layout.custom_tab_item, null)
            val tabTextView = customTab.findViewById<TextView>(android.R.id.text1)
            tabTextView.text = tabTitles[position]
            tab.text = adapter.getPageTitle(position)
            tab.customView = customTab
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}