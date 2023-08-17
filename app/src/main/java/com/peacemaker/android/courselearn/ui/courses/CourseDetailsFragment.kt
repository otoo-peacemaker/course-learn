package com.peacemaker.android.courselearn.ui.courses

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.CourseContentListItemBinding
import com.peacemaker.android.courselearn.databinding.CourseListItemsBinding
import com.peacemaker.android.courselearn.databinding.FragmentCourseBinding
import com.peacemaker.android.courselearn.databinding.FragmentCourseDetailsBinding
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.model.Lesson
import com.peacemaker.android.courselearn.ui.adapters.RecyclerBaseAdapter
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class CourseDetailsFragment : BaseFragment() {
    private var _binding: FragmentCourseDetailsBinding? = null
    private val binding get() = _binding!!

    private  var coursesItem: CoursesItem?=null
    private val mAdapter by lazy { RecyclerBaseAdapter<Lesson>() }
    companion object {
        fun newInstance() = CourseDetailsFragment()
    }

    private lateinit var viewModel: CourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailsBinding.inflate(layoutInflater)
        arguments?.apply {
            coursesItem = getParcelable("course")!!
        }

        setUIItems()
        addToCourseButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
    }

    private fun setUIItems(){
        binding.apply {
            courseName.text = coursesItem?.courseName
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

    fun setUpRecyclerview(){
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
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mAdapter
    }

    private fun addToCourseButton(){
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users")
        val docId = FirebaseHelper.UserDataCollection().getCurrentUser()?.providerData?.get(0)?.uid
        setAppButton(binding.buy,"Add course"){//TODO : BUY COURSE, but I am just adding it to user freely for now
            FirebaseHelper.UserDataCollection().addUserData(coursesItem as Any,"users","my_courses"){success,message->
                if (success) showSnackBar(requireView(),"Course $message") else showSnackBar(requireView(),message)
            }

            //navigateTo(R.id.action_courseDetailsFragment_to_myCoursesFragment,bundle)
        }
    }

}