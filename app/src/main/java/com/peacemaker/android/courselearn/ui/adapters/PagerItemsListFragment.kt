package com.peacemaker.android.courselearn.ui.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseListItemsBinding
import com.peacemaker.android.courselearn.databinding.FragmentPagerItemsListBinding
import com.peacemaker.android.courselearn.databinding.MessageItemListBinding
import com.peacemaker.android.courselearn.databinding.NotificationListItemsBinding
import com.peacemaker.android.courselearn.model.AppMessages
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.model.Notification
import com.peacemaker.android.courselearn.ui.account.AccountViewModel
import com.peacemaker.android.courselearn.ui.search.SearchFragment
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class PagerItemsListFragment : BaseFragment() {

    private var _binding: FragmentPagerItemsListBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy { RecyclerBaseAdapter<CoursesItem>() }
    private val notificationAdapter by lazy { RecyclerBaseAdapter<Notification>() }
    private val messageAdapter by lazy { RecyclerBaseAdapter<AppMessages>() }
    var title: String? = null
    private val viewModel: AccountViewModel by viewModels()
    private var notification = mutableListOf<Notification>()
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseHelper.UserDataCollection().getCurrentUser()

    private lateinit var coursesShimmer: ShimmerFrameLayout

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
        coursesShimmer = binding.coursesShimmer
        when (title) {
            "All" -> {
                setAllCoursesRecyclerViewItems()
                binding.title.visibility = View.GONE
            }

            "Popular" -> {
                binding.title.text = "No $title courses"
            }

            "New" -> {
                binding.title.text = "No $title courses"
            }

            "Messages" -> {
                binding.title.visibility = View.GONE
                setMessageRecyclerViewItems()
            }

            "Notifications" -> {
                binding.title.visibility = View.GONE
                binding.title.text = "No $title "
                setNotificationRecyclerViewItems()
            }
        }
    }
    private fun setAllCoursesRecyclerViewItems() {
        val collectionName = "courses"
        FirebaseHelper.DocumentCollection()
            .getDocumentsByType(firestore, collectionName, CoursesItem::class.java) { _, result ->
                printLogs("$SearchFragment", "$result")
                mAdapter.submitList(result)
                if (result ==null) startShimmerEffect(coursesShimmer)
                else {
                    stopShimmerEffect(coursesShimmer)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.coursesShimmer.visibility = View.GONE
                }
                mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
                    CourseListItemsBinding.inflate(inflater, viewGroup, false)
                }
                mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
                    val view = viewBinding as CourseListItemsBinding
                    view.courseName.text = eachItem.courseName
                    view.coursePrice.text = eachItem.price
                    view.authorName.text = eachItem.authorName
                    loadImageToImageView(requireContext(),eachItem.courseBgImg,view.courseImg,R.drawable.loading_animation,R.drawable.course_bg_img)
                    view.courseDuration.text = eachItem.duration?.hours.toString().plus(":")
                        .plus(eachItem.duration?.minutes).plus("hs")
                    view.root.setOnClickListener {
                        //navigateTo(R.id.action)
                        val bundle = Bundle().apply {
                            putParcelable("course", eachItem)
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
        viewModel.loadUserRelatedData("notification", Notification::class.java)
        observeLiveDataResource(viewModel.userRelatedData, { items ->
            printLogs("MyCoursesFragment", "$items")
            notification = items as MutableList<Notification>
            notificationAdapter.submitList(notification)
            if (items.isEmpty()) startShimmerEffect(coursesShimmer)
            else {
                stopShimmerEffect(coursesShimmer)
                binding.recyclerView.visibility = View.VISIBLE
                binding.coursesShimmer.visibility = View.GONE
            }

            notificationAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
                NotificationListItemsBinding.inflate(inflater, viewGroup, false)
            }
            notificationAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
                val view = viewBinding as NotificationListItemsBinding
                view.title.text = eachItem.title
                view.time.text = eachItem.time?.let { getTimeAgoString(it) }
                view.root.setOnClickListener {
                    //navigateTo(R.id.action)
                    val bundle = Bundle().apply {
                        putParcelable("course", eachItem)
                    }
                    navigateTo(R.id.action_global_messageDetailsFragment, bundle)
                }
            }
        })

        sortListByTime()
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = notificationAdapter

    }
    private fun setMessageRecyclerViewItems() {
        val collectionName = "messages"
        FirebaseHelper.DocumentCollection()
            .getDocumentsByType(firestore, collectionName, AppMessages::class.java) { _, result ->
                printLogs("$SearchFragment", "$result")
                messageAdapter.submitList(result)
                if (result==null) startShimmerEffect(coursesShimmer)
                else {
                    stopShimmerEffect(coursesShimmer)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.coursesShimmer.visibility = View.GONE
                }
                messageAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
                    MessageItemListBinding.inflate(inflater, viewGroup, false)
                }

                messageAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
                    val view = viewBinding as MessageItemListBinding
                    view.name.text = eachItem.name
                    view.desc.text = eachItem.content?.desc
                    view.status.text = eachItem.status
                    view.time.text = eachItem.time?.let { getTimeAgoString(it) }
                    loadImageToImageView(requireContext(), eachItem.content?.profileImg, view.courseImg)
                    view.root.setOnClickListener {
                        //navigateTo(R.id.action)
                        val bundle = Bundle().apply {
                            putParcelable("course", eachItem)
                        }
                        navigateTo(R.id.action_global_messageDetailsFragment, bundle)
                    }
                }

                val recyclerView = binding.recyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = messageAdapter
            }

    }
    private fun sortListByTime() {
        val timeComparator = Comparator<Notification> { item1, item2 ->
            item2.time?.let { item1.time?.compareTo(it) } ?: 0
        }
        notificationAdapter.setComparator(timeComparator)
    }

}