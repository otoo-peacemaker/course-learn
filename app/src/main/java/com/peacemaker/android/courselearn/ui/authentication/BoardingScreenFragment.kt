package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.FragmentBoardingScreenBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

class BoardingScreenFragment : BaseFragment() {

    private var _binding: FragmentBoardingScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var backPressedCallback: OnBackPressedCallback

    companion object {
        fun newInstance() = BoardingScreenFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardingScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppButton(binding.signUpBtn,"Sign Up"){
            navigateTo(R.id.action_boardingScreenFragment_to_signUpFragment)
        }

        setTextButton(binding.loginBtn,"Login"){
            findNavController().navigate(R.id.action_boardingScreenFragment_to_loginFragment)
        }

        //Handle onDevice back press
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmationDialog()
            }
        }
        // Register the callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    //A function to show a dialog on a back press
    fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to close the app?")
            .setPositiveButton("Yes") { _, _ ->
                // User confirmed, close the fragment
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(requireContext())
        updateUI(FirebaseHelper.UserDataCollection().getCurrentUser())
    }

    override fun onResume() {
        super.onResume()
        showActionBarOnFragment(this,true)
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
//            findNavController().navigate(R.id.action_global_home_graph)
            if (user.isEmailVerified) findNavController().navigate(R.id.action_global_home_graph)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the callback
        backPressedCallback.remove()
        _binding = null
    }

}