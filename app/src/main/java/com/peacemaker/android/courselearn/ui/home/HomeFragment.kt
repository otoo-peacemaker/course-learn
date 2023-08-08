package com.peacemaker.android.courselearn.ui.home

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentHomeBinding
import com.peacemaker.android.courselearn.ui.account.AccountViewModel
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            val cards = listOf(
                CourseCard(
                    bgColor = resources.getColor(R.color.default_card_bg_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
                CourseCard(
                    bgColor = resources.getColor(R.color.icon_color,null),
                    img = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null),
                    courseName = getString(R.string.course)
                ),
            )
            val cardAdapter = CourseCardAdapter(cards)
            viewPager.adapter = cardAdapter
            viewPager.offscreenPageLimit = 3
            dotsIndicator.attachTo(viewPager)
        }
        observeData()
        return root
    }

    fun setOnClickListeners(){
        binding.apply {

        }
    }
    private fun observeData(){
        val mainBinding = (activity as MainActivity).binding
        // Observe the LiveData objects
        viewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            // Update the profile picture ImageView
            Glide.with(this)
                .load(uri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.avatar_person)//TODO: change the placeholder img to loader
                        .error(R.drawable.avatar_person))
                .into(mainBinding.image)

            //// Update the profile picture of bottom icon

            runBlocking {
                val profileImageDrawable = loadImageFromUrl(uri)
                mainBinding.navView.menu.findItem(R.id.navigation_account).icon = profileImageDrawable
            }

        }
        viewModel.name.observe(viewLifecycleOwner) { name ->
            mainBinding.title.text=name
            mainBinding.subtitle.text = getString(R.string.start_learning)

        }

        mainBinding.image.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_account)
        }
    }
    fun changeBottomProfileIcon(uri: Uri){
        val mainBinding = (activity as MainActivity).binding
        // Use Glide or your preferred image loading library to load the image
        Glide.with(this)
            .load(uri)
            .apply(RequestOptions.circleCropTransform()) // Apply circular crop for a profile image
            .into(object : com.bumptech.glide.request.target.CustomTarget<Drawable>() {
                override fun onLoadStarted(placeholder: Drawable?) {
                    // You can show a placeholder image while loading
                    mainBinding.navView.menu.findItem(R.id.navigation_account).icon = ResourcesCompat.getDrawable(resources,R.drawable.avatar_person,null)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // Handle the case when image loading fails
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    // Update the icon of the profile item with the fetched image
                    mainBinding.navView.menu.findItem(R.id.navigation_account).icon = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // The image has been cleared
                }
            })
    }
    private suspend fun loadImageFromUrl(url: Uri) = withContext(Dispatchers.IO) {
        return@withContext try {
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ico_person)  // Placeholder if image loading fails
                .error(R.drawable.ico_person)        // Error placeholder if loading fails
                .submit()
                .get()
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}