package com.peacemaker.android.courselearn.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.FragmentAccountBinding
import com.peacemaker.android.courselearn.ui.authentication.AuthViewModel

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AccountFragment()
    }

    private val viewModel: AccountViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(layoutInflater)
        setupUIElement()
        observeProfileImgAndNameData()
        return binding.root
    }

    private fun setupUIElement() {
        binding.apply {
            favorite.apply {
                itemImg.setImageDrawable(getDrawable(resources, R.drawable.favorite, null))
                itemName.text = getString(R.string.favorite)
            }
            edtAcc.apply {
                itemImg.setImageDrawable(
                    getDrawable(
                        resources,
                        R.drawable.baseline_mode_edit_24,
                        null
                    )
                )
                itemName.text = getString(R.string.edt_acc)
            }
            settingsPrivacy.apply {
                itemImg.setImageDrawable(
                    getDrawable(
                        resources,
                        R.drawable.outline_settings_24,
                        null
                    )
                )
                itemName.text = getString(R.string.settings)
            }
            help.apply {
                itemImg.setImageDrawable(
                    getDrawable(
                        resources,
                        R.drawable.baseline_help_outline_24,
                        null
                    )
                )
                itemName.text = getString(R.string.help)
            }

            logout.apply {
                itemImg.setImageDrawable(
                    getDrawable(
                        resources,
                        R.drawable.baseline_logout_24,
                        null
                    )
                )
                itemName.text = getString(R.string.logout)
                logout.root.setOnClickListener {
                    authViewModel.signOut {
                        findNavController().navigate(R.id.auth_graph)
                    }
                }
            }
        }
    }

    private fun observeProfileImgAndNameData(){
        // Observe the LiveData objects
        viewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            // Update the profile picture ImageView
            Glide.with(this)
                .load(uri)
                .apply(
                    RequestOptions()
                    .placeholder(R.drawable.avatar_person)//TODO: change the placeholder img to loader
                    .error(R.drawable.avatar_person))
                .into(binding.profileImg)
        }
        viewModel.name.observe(viewLifecycleOwner) { name ->
            binding.username.text = name
        }
    }

}