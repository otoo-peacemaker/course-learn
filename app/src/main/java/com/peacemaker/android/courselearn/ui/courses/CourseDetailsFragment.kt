package com.peacemaker.android.courselearn.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseContentListItemBinding
import com.peacemaker.android.courselearn.databinding.FragmentCourseDetailsBinding
import com.peacemaker.android.courselearn.model.*
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.message.MessageViewModel
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import com.peacemaker.android.courselearn.ui.util.NetworkConnectivity
import com.peacemaker.android.courselearn.ui.util.ApplicationNotificationManager
import com.peacemaker.android.courselearn.ui.util.VideoPlayerManager

class CourseDetailsFragment : BaseFragment() {
    private var _binding: FragmentCourseDetailsBinding? = null
    private val binding get() = _binding!!

    private var coursesItem: CoursesItem? = null
    private val mAdapter by lazy { RecyclerBaseAdapter<Lesson>() }
    private lateinit var networkConnectivityCallback: NetworkConnectivity
    private val viewModel: MessageViewModel by viewModels()
    // private val viewModel: CourseViewModel by viewModels()
    private var isPlaying = false
    private val firebaseHelper = FirebaseHelper.UserDataCollection()
    private val currentUser = FirebaseHelper.UserDataCollection().getCurrentUser()
    val message = "You have successfully added new course"
    val time = System.currentTimeMillis()
    private lateinit var videoPlayerManager: VideoPlayerManager
    private lateinit var playerView: PlayerView
    private val vidUrls  = mutableListOf<String?>()

    companion object {
        fun newInstance() = CourseDetailsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentCourseDetailsBinding.inflate(layoutInflater)
        return binding.root
    }
    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkConnectivityCallback = NetworkConnectivity(requireContext())
        networkConnectivityCallback.startListening()
        videoPlayerManager = VideoPlayerManager(requireContext(), lifecycle)
        playerView = binding.playerView
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
            loadImageToImageView(requireContext(), coursesItem?.courseBgImg.toString(),
                binding.courseImg, placeholderResId =  R.drawable.loading_animation, errorResId = R.drawable.course_bg_img)
        }
        setUpRecyclerview()
    }
    private fun setUpRecyclerview() {
        val result = coursesItem?.lessons!!
        result.forEach {
           vidUrls.add(it.content)
        }
        videoPlayerManager.setPlaylist(playerView,vidUrls)
        mAdapter.submitList(result)
        mAdapter.expressionOnCreateViewHolder = { inflater, viewGroup ->
            CourseContentListItemBinding.inflate(inflater, viewGroup, false)
        }
        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as CourseContentListItemBinding
            view.courseNo.text = eachItem.id?.toString()
            view.title.text = eachItem.title
            view.min.text = eachItem.duration?.hours.toString().plus(":").plus(eachItem.duration?.minutes)
            view.playBtn.setOnClickListener {
                binding.courseName.visibility = View.GONE
                binding.courseImg.visibility = View.GONE
                binding.exoControllerLoader.visibility = View.GONE
                videoPlayerManager.setVideoUrlIdAndPlay(eachItem.id, view.playBtn){ isLoading ->
                    if (isLoading) {
                        binding.exoControllerLoader.visibility = View.VISIBLE
                    } else {
                        binding.exoControllerLoader.visibility = View.GONE
                    }
                }

               /* videoPlayerManager.setPlayUrlAndPlay(playerView, eachItem.content, it as  TextView) { isLoading ->
                    if (isLoading) {
                        binding.exoControllerLoader.visibility = View.VISIBLE
                    } else {
                        binding.exoControllerLoader.visibility = View.GONE
                    }
                }*/
             //   togglePlayPause(it)
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
        var userDetails:AppUser?=null
        firebaseHelper.getUserByUID(currentUser?.uid){
            userDetails = it
        }
        firebaseHelper.addUserData(coursesItem as Any, "users", "my_courses") { success, message ->
            if (success) {
                binding.loader.root.visibility = View.GONE
                binding.buy.containedButton.isEnabled = true
                showNotification()
                addMessage(userName = userDetails?.name, status = userDetails?.status, message = message,time,currentUser?.photoUrl.toString())
                navigateTo(R.id.action_courseDetailsFragment_to_myCoursesFragment)
            } else {
                binding.loader.root.visibility = View.GONE
                binding.buy.containedButton.isEnabled = true
                showSnackBar(requireView(), message)
            }
        }
    }
    private fun showNotification(){
        val notification = Notification(
            title = message,
            time = time,
            content = coursesItem
        )
        firebaseHelper.addUserData(notification as Any,"users","notification"){ success, _ ->
            if (success) ApplicationNotificationManager.sendNotification(requireActivity(),"Course","You have successfully added new course")
            //NotificationManager.updateBadgeCount(requireActivity())
            (activity as MainActivity).updateBadgeCount(1)
        }
    }
    private fun togglePlayPause(textView: TextView) {
        isPlaying = !isPlaying
        binding.courseImg.visibility = View.GONE
        if (isPlaying) {
            textView.setBackgroundResource(R.drawable.ico_course_pause)
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_course_pause, 0, 0, 0)
            videoPlayerManager.pause()

        } else {
            textView.setBackgroundResource(R.drawable.ico_play)
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ico_play, 0, 0, 0
            )
            // Implement logic to pause playback here
            videoPlayerManager.resume()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        networkConnectivityCallback.stopListening()
    }

}