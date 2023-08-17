package com.peacemaker.android.courselearn.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentSuccessBinding
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SuccessFragment : BaseFragment() {
    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SuccessFragment()
    }

    private val args: SuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentSuccessBinding.inflate(layoutInflater)
        binding.message.setText(args.message).toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        setAppButton(binding.doneBtn, "Ok") {
            openGmailApp()
            runBlocking {
                delay(2000)
                navigateTo(R.id.action_successFragment_to_loginFragment)
            }
        }
    }

}