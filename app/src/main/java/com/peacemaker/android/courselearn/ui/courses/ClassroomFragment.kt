package com.peacemaker.android.courselearn.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.CourseContentListItemBinding
import com.peacemaker.android.courselearn.databinding.FragmentClassroomBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.model.Lesson
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import com.peacemaker.android.courselearn.ui.util.VideoPlayerManager

class ClassroomFragment : BaseFragment() {

    companion object {
        fun newInstance() = ClassroomFragment()
    }

    private var _binding: FragmentClassroomBinding? = null
    private val binding get() = _binding!!

    private var coursesItem: CoursesItem? = null
    private val mAdapter by lazy { RecyclerBaseAdapter<Lesson>() }
    private lateinit var videoPlayerManager: VideoPlayerManager
    private lateinit var playerView: PlayerView

    private val videoUrls = listOf(
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        setUIItems()
        startLearningCourseButton()
        setupCoursePlayList()
    }

    private fun setupCoursePlayList() {
        videoPlayerManager = VideoPlayerManager(requireContext(), lifecycle)
        videoPlayerManager.setPlaylist(playerView, videoUrls)
    }


    private fun setUIItems() {
        binding.apply {
            //courseName.text = coursesItem?.courseName
            courseName2.text = coursesItem?.courseName
            coursePrice.text = coursesItem?.price
            desc.text = coursesItem?.courseAbout
            minute.text = coursesItem?.duration?.hours?.toString()?.plus(":").plus(coursesItem?.duration?.minutes).plus(" hrs")
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
            view.min.text = eachItem.duration?.hours.toString().plus(":").plus(eachItem.duration?.minutes)

            //TODO: GET THE CONTENT URLS
            // val videoUrls = listOf("urls")
            // videoPlayerManager = VideoPlayerManager(requireContext(), lifecycle)
            // videoPlayerManager.setPlaylist(playerView, videoUrls)
            // videoPlayerManager.play()

            view.apply {
                playBtn.setOnClickListener {
                    binding.exoControllerPlaceholder.visibility = View.GONE
                    videoPlayerManager.setVideoUrlIdAndPlay(eachItem.id){
                        if (it) binding.exoControllerLoader.visibility = View.VISIBLE
                        else binding.exoControllerLoader.visibility = View.GONE
                    }
                }
            }
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mAdapter
    }

    private fun startLearningCourseButton() {
        setAppButton(binding.buy, "Start course") {
            binding.exoControllerPlaceholder.visibility = View.GONE
            videoPlayerManager.setNewVideoUrlAndPlayById(
                binding.playerView,videoUrls,0){isLoading->
                if (isLoading) binding.exoControllerLoader.visibility = View.VISIBLE
                else{
                    binding.exoControllerLoader.visibility = View.GONE
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}