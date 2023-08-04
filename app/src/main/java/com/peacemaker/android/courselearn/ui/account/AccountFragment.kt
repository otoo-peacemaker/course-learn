package com.peacemaker.android.courselearn.ui.account

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.navigation.fragment.findNavController
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = AccountFragment()
    }

    //private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(layoutInflater)
        setupUIElement()
        return binding.root
    }

    private fun setupUIElement(){
        binding.apply {
            favorite.apply {
                itemImg.setImageDrawable(getDrawable(resources,R.drawable.favorite, null))
                itemName.text = getString(R.string.favorite)
            }
            edtAcc.apply {
                itemImg.setImageDrawable(getDrawable(resources,R.drawable.baseline_mode_edit_24, null))
                itemName.text = getString(R.string.edt_acc)
            }
            settingsPrivacy.apply {
                itemImg.setImageDrawable(getDrawable(resources,R.drawable.outline_settings_24, null))
                itemName.text = getString(R.string.settings)
            }
            help.apply {
                itemImg.setImageDrawable(getDrawable(resources,R.drawable.baseline_help_outline_24, null))
                itemName.text = getString(R.string.help)
            }

            logout.apply {
                itemImg.setImageDrawable(getDrawable(resources,R.drawable.baseline_logout_24, null))
                itemName.text = getString(R.string.logout)
                logout.root.setOnClickListener {
                  /*  authViewModel.signOut{
                        findNavController().navigate(R.id.action_global_authentication_nav_graph)
                    }*/
                }
            }
        }
    }

}