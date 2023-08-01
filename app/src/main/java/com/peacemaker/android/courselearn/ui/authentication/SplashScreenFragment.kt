package com.peacemaker.android.courselearn.ui.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentSplashScreenBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : BaseFragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SplashScreenFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(layoutInflater)

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showActionBarOnFragment(this, false)
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashScreenFragment_to_auth_graph)
        }, 3000)
    }

    override fun onDestroyView() {
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onDestroyView()
        _binding = null
    }
}