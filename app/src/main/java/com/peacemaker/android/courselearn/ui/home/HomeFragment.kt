package com.peacemaker.android.courselearn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.CourseCardLayoutBinding
import com.peacemaker.android.courselearn.databinding.FragmentHomeBinding
import com.peacemaker.android.courselearn.databinding.LessonPlanItemsBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.account.AccountViewModel
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import kotlinx.coroutines.runBlocking

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()
    private val mAdapter by lazy { RecyclerBaseAdapter<CoursesItem>() }
    private var coursesItem = mutableListOf<CoursesItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveDataResource(viewModel.userRelatedData, { items ->
            printLogs("MyCoursesFragment", "$items")
            coursesItem= items as MutableList<CoursesItem>
            mAdapter.submitList(items)
        })

          setupUIItems()
       // setupCarousel()
        setupProfileData()
        setupLessonPlan()
    }

    fun setOnClickListeners() {
        binding.apply {}
    }

    private fun setupCarousel() {
        mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
            CourseCardLayoutBinding.inflate(inflater, viewGroup, false)
        }
        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as CourseCardLayoutBinding
            view.apply {
                courseName.text = eachItem.courseName
                val bundle = Bundle().apply {
                    putParcelable("course", eachItem)
                }
                view.root.setOnClickListener {
                    navigateTo(R.id.action_global_classroomFragment, bundle)
                }
            }
        }
        binding.apply {
            viewPager.adapter = mAdapter
            viewPager.offscreenPageLimit = 3
            dotsIndicator.attachTo(viewPager)
        }

    }

    private fun setupUIItems() {
        binding.apply {
            val cardItem =  arrayListOf<CourseCard>()
            coursesItem.forEach { coursesItem ->
                cardItem.add(CourseCard(
                    bgColor = changeResColor(R.color.default_card_bg_color),
                    img = changeResDrawable(R.drawable.avatar_person),
                    courseName = coursesItem.courseName
                ))
            }

            printLogs("Items","$cardItem")
            val cards = listOf(
                CourseCard(
                    bgColor = changeResColor(R.color.default_card_bg_color),
                    img = changeResDrawable(R.drawable.avatar_person),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = changeResColor(R.color.icon_color),
                    img = changeResDrawable(R.drawable.avatar_person),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = changeResColor(R.color.icon_color),
                    img = changeResDrawable(R.drawable.avatar_person),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = changeResColor(R.color.icon_color),
                    img = changeResDrawable(R.drawable.avatar_person),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = changeResColor(R.color.icon_color),
                    img = changeResDrawable(R.drawable.avatar_person),
                    courseName = getString(R.string.course)
                ),
            )
            val cardAdapter = CourseCardAdapter(cards)
            viewPager.adapter = cardAdapter
            viewPager.offscreenPageLimit = 3
            dotsIndicator.attachTo(viewPager)
        }
    }

    private fun setupProfileData() {
        val mainBinding = (activity as MainActivity).binding
        // Observe the LiveData objects
        viewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            // Update the profile picture ImageView
            Glide.with(this)
                .load(uri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.avatar_person)
                )
                .into(mainBinding.image)

            //// Update the profile picture of bottom icon

            runBlocking {
                val profileImageDrawable = loadImageFromUrl(uri)
                mainBinding.navView.menu.findItem(R.id.navigation_account).icon =
                    profileImageDrawable
            }

        }
        viewModel.name.observe(viewLifecycleOwner) { name ->
            mainBinding.title.text = name
            mainBinding.subtitle.text = getString(R.string.start_learning)

        }

        mainBinding.image.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_account)
        }
    }

    private fun setupLessonPlan() {
        mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
            LessonPlanItemsBinding.inflate(inflater, viewGroup, false)
        }
        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as LessonPlanItemsBinding
            view.apply {
                courseName.text = eachItem.courseName
                loadImageToImageView(
                    requireContext(),
                    eachItem.courseBgImg,
                    courseImg,
                    R.drawable.loading_animation,
                    R.drawable.course_bg_img
                )
                val bundle = Bundle().apply {
                    putParcelable("course", eachItem)
                }
                view.root.setOnClickListener {
                    navigateTo(R.id.action_global_classroomFragment, bundle)
                }
            }
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

