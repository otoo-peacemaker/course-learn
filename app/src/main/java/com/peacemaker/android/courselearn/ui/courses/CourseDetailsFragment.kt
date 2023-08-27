package com.peacemaker.android.courselearn.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseContentListItemBinding
import com.peacemaker.android.courselearn.databinding.FragmentCourseDetailsBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.model.Lesson
import com.peacemaker.android.courselearn.model.Notification
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.message.MessageViewModel
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import com.peacemaker.android.courselearn.ui.util.NetworkConnectivity
import com.peacemaker.android.courselearn.ui.util.ApplicationNotificationManager

class CourseDetailsFragment : BaseFragment() {
    private var _binding: FragmentCourseDetailsBinding? = null
    private val binding get() = _binding!!

    private var coursesItem: CoursesItem? = null
    private val mAdapter by lazy { RecyclerBaseAdapter<Lesson>() }
    private lateinit var networkConnectivityCallback: NetworkConnectivity

    private val viewModel: MessageViewModel by viewModels()


    companion object {
        fun newInstance() = CourseDetailsFragment()
    }

    // private val viewModel: CourseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkConnectivityCallback = NetworkConnectivity(requireContext())
        networkConnectivityCallback.startListening()

        arguments?.apply {
            coursesItem = getParcelable("course")!!
        }

        setUIItems()
        addToCourseButton()
    }

    private fun setUIItems() {
        binding.apply {
            courseName.text = coursesItem?.courseName
            courseName2.text = coursesItem?.courseName
            coursePrice.text = coursesItem?.price
            desc.text = coursesItem?.courseAbout
            minute.text = coursesItem?.duration?.hours?.toString()?.plus(":")
                .plus(coursesItem?.duration?.minutes).plus(" hrs")
            lesson.text = coursesItem?.lessons?.size.toString().plus(" Lessons")
            setConstraintLayoutBackground(
                requireContext(),
                binding.courseImg,
                coursesItem?.courseBgImg.toString(),
                placeholderResId = R.drawable.course_bg_img
            )
        }

        setUpRecyclerview()
    }

    private fun setUpRecyclerview() {
        val result = coursesItem?.lessons!!
        mAdapter.submitList(result)
        mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
            CourseContentListItemBinding.inflate(inflater, viewGroup, false)
        }
        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as CourseContentListItemBinding
            view.courseNo.text = eachItem.id?.toString()
            view.title.text = eachItem.title
            view.min.text =
                eachItem.duration?.hours.toString().plus(":").plus(eachItem.duration?.minutes)

            view.playBtn.setOnClickListener {
                when (it.background) {
                    ResourcesCompat.getDrawable(resources, R.drawable.ico_play, null) -> {
                        it.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ico_course_pause,
                            null
                        )
                    }
                    ResourcesCompat.getDrawable(resources, R.drawable.ico_course_pause, null) -> {
                        it.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.ico_play, null)
                    }
                }
            }

        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mAdapter
    }

    private fun addToCourseButton() {
        setAppButton(binding.buy, "Add course") {
            //TODO : BUY COURSE, but I am just adding it to user freely for now
            /* viewModel.addToCourse(coursesItem)
             printLogs("$CourseDetailsFragment","$coursesItem")*/
            binding.loader.root.visibility = View.VISIBLE
            binding.buy.containedButton.isEnabled = false

            if (networkConnectivityCallback.isConnected()){
                performRequest()
            }else{
                binding.loader.root.visibility = View.GONE
                showRetrySnackBar(requireView(),"No Internet access","Retry"){
                    performRequest()
                }
            }

        }
    }


    private fun performRequest(){
        val firebaseHelper = FirebaseHelper.UserDataCollection()
        val notification = Notification(
            title = "You have successfully added new course" ,
            time = System.currentTimeMillis(),
            content = coursesItem
        )

        firebaseHelper.addUserData(coursesItem as Any, "users", "my_courses") { success, message ->
            if (success) {
                binding.loader.root.visibility = View.GONE
                binding.buy.containedButton.isEnabled = true
                //TODO: add notification to fb
                  firebaseHelper.addUserData(notification as Any,"users","notification"){success,message->
                      // TODO: show notification
                      ApplicationNotificationManager.sendNotification(requireActivity(),"Course","You have successfully added new course")
                      //NotificationManager.updateBadgeCount(requireActivity())
                      (activity as MainActivity).updateBadgeCount(1)
                  }
                showSnackBar(requireView(), "Course $message")
                navigateTo(R.id.action_courseDetailsFragment_to_myCoursesFragment)

            } else {
                binding.loader.root.visibility = View.GONE
                binding.buy.containedButton.isEnabled = true
                showSnackBar(requireView(), message)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkConnectivityCallback.stopListening()
    }

}