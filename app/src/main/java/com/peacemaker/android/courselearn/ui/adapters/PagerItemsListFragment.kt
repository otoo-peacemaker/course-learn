package com.peacemaker.android.courselearn.ui.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseListItemsBinding
import com.peacemaker.android.courselearn.databinding.FragmentPagerItemsListBinding
import com.peacemaker.android.courselearn.databinding.NotificationListItemsBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.model.Notification
import com.peacemaker.android.courselearn.ui.account.AccountViewModel
import com.peacemaker.android.courselearn.ui.courses.CourseViewModel
import com.peacemaker.android.courselearn.ui.search.SearchFragment
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class PagerItemsListFragment : BaseFragment() {
    private var _binding: FragmentPagerItemsListBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy { RecyclerBaseAdapter<CoursesItem>() }
    private val messageAdapter by lazy { RecyclerBaseAdapter<Notification>() }
    var title:String? = null
    private val viewModel: AccountViewModel by viewModels()
    private var notification = mutableListOf<Notification>()
    companion object {
        fun newInstance(bundle: Bundle) = PagerItemsListFragment().apply {
            arguments = bundle.apply {
                title = getString("title")
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentPagerItemsListBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(title){
            "All"->{
                setAllCoursesRecyclerViewItems()
                binding.title.visibility = View.GONE
            }

            "Popular"->{
                binding.title.text = "No $title courses"
            }

            "New"->{
                binding.title.text = "No $title courses"
            }

            "Messages"->{
                binding.title.text = "No $title "
            }

            "Notifications"->{
                binding.title.visibility = View.GONE
                binding.title.text = "No $title "
                setNotificationRecyclerViewItems()
                main()
            }
        }
    }

    private fun setAllCoursesRecyclerViewItems() {
        val firestore = FirebaseFirestore.getInstance()
        val collectionName = "courses"
        FirebaseHelper.DocumentCollection()
            .getDocumentsByType(firestore, collectionName, CoursesItem::class.java) { message, result->
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
            }

    }
    private fun setNotificationRecyclerViewItems() {
        viewModel.loadUserRelatedData("notification",Notification::class.java)
        observeLiveDataResource(viewModel.userRelatedData, { items ->
            printLogs("MyCoursesFragment", "$items")
            notification = items as MutableList<Notification>
            messageAdapter.submitList(notification)

            messageAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
                NotificationListItemsBinding.inflate(inflater, viewGroup, false)
            }
            messageAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
                val view = viewBinding as NotificationListItemsBinding
                view.title.text = eachItem.title
                view.time.text = eachItem.time?.let { getTimeAgoString(it) }
                view.root.setOnClickListener {
                    //navigateTo(R.id.action)
                    val bundle = Bundle().apply {
                        putParcelable("course",eachItem)
                    }
                    navigateTo(R.id.action_global_messageDetailsFragment, bundle)
                }
            }
        })

        sortListByTime()

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = messageAdapter

    }

    private fun sortListByTime() {
        val timeComparator = Comparator<Notification> { item1, item2 ->
            item2.time?.let { item1.time?.compareTo(it) } ?: 0
        }
        messageAdapter.setComparator(timeComparator)
    }


}