package com.peacemaker.android.courselearn.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseContentListItemBinding
import com.peacemaker.android.courselearn.databinding.FragmentClassroomBinding
import com.peacemaker.android.courselearn.model.AppUser
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.model.Lesson
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import com.peacemaker.android.courselearn.ui.util.VideoPlayerManager

class ClassroomFragment : BaseFragment() {

    private var _binding: FragmentClassroomBinding? = null
    private val binding get() = _binding!!
    private var coursesItem: CoursesItem? = null
    private val mAdapter by lazy { RecyclerBaseAdapter<Lesson>() }
    private lateinit var videoPlayerManager: VideoPlayerManager
    private lateinit var playerView: PlayerView
    var message = ""
    val time = System.currentTimeMillis()
    private val currentUser = FirebaseHelper.UserDataCollection().getCurrentUser()
    private val firebaseHelper = FirebaseHelper.UserDataCollection()
    var userDetails: AppUser?=null
    companion object {
        fun newInstance() = ClassroomFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentClassroomBinding.inflate(layoutInflater)
        playerView = binding.playerView
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            coursesItem = getParcelable("course")!!
        }
        videoPlayerManager = VideoPlayerManager(requireContext(), lifecycle)
        setUIItems()
        startLearningCourseButton()
        firebaseHelper.getUserByUID(currentUser?.uid){
            userDetails = it
        }
    }
    private fun setUIItems() {
        binding.apply {
            //courseName.text = coursesItem?.courseName
            courseName2.text = coursesItem?.courseName
            coursePrice.text = coursesItem?.price
            desc.text = coursesItem?.courseAbout
            minute.text = coursesItem?.duration?.hours?.toString()?.plus(":").plus(coursesItem?.duration?.minutes).plus(" hrs")
            lesson.text = coursesItem?.lessons?.size.toString().plus(" Lessons")
            message = "You have started learning ${coursesItem?.courseName}"
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
            view.min.text = eachItem.duration?.hours.toString().plus(":").plus(eachItem.duration?.minutes)
            view.apply {
                playBtn.setOnClickListener {
                    binding.exoControllerPlaceholder.visibility = View.GONE
//                    view.playBtn.background = changeResDrawable(R.drawable.ico_play)
                    videoPlayerManager.setPlayUrlAndPlay(playerView,eachItem.content, it as TextView){isLoading->
                        if (isLoading) {
                            binding.exoControllerLoader.visibility = View.VISIBLE
                        }
                        else {
                            binding.exoControllerLoader.visibility = View.GONE
                        }
                    }
                }
            }
        }
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mAdapter
    }
    private fun startLearningCourseButton() {
        val result = coursesItem?.lessons!!
        val videoUrlsStr = mutableListOf<String?>()
        result.forEach { videoUrlsStr.add(it.content) }
        setAppButton(binding.buy, "Start course") {
            addMessage(userName = userDetails?.name, status = userDetails?.status, message = message,time,userDetails?.profileImage)
            binding.exoControllerPlaceholder.visibility = View.GONE
            videoPlayerManager.setPlaylist(playerView,videoUrlsStr){isLoading->
                if (isLoading) binding.exoControllerLoader.visibility = View.VISIBLE
                else{
                    binding.exoControllerLoader.visibility = View.GONE
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoPlayerManager.pause()
    }
    override fun onResume() {
        super.onResume()
        videoPlayerManager.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}