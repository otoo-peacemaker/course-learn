package com.peacemaker.android.courselearn.ui.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.FragmentAccountBinding
import com.peacemaker.android.courselearn.model.AppUser
import com.peacemaker.android.courselearn.ui.authentication.AuthViewModel
import com.peacemaker.android.courselearn.ui.util.BaseFragment
import com.peacemaker.android.courselearn.ui.util.Utils.CheckPermissions.checkAndRequestPermission
import com.peacemaker.android.courselearn.ui.util.Utils.CheckPermissions.readExternalStorage

class AccountFragment : BaseFragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    companion object {
        fun newInstance() = AccountFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(layoutInflater)
        setupUIElement()
        observeProfileImgAndNameData()
        selectImg()
        updateProfileImg()
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
    private fun observeProfileImgAndNameData() {
        // Observe the LiveData objects
        viewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            // Update the profile picture ImageView
            Glide.with(this)
                .load(uri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.avatar_person)//TODO: change the placeholder img to loader
                        .error(R.drawable.avatar_person)
                )
                .into(binding.profileImg)
        }
        viewModel.name.observe(viewLifecycleOwner) { name ->
            binding.username.text = name
        }
    }
    private fun selectImg() {
        binding.edtProfile.setOnClickListener {
            openImagePicker()
            if (checkAndRequestPermission(requireActivity(), readExternalStorage)) {
                // Permission is already granted, open the image picker
                //openImagePicker()
            }
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }
    private fun updateProfileImg() {
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users")
        val fieldName = "profileImage"
        var appUser: AppUser? = null
        FirebaseHelper.UserDataCollection()
            .getUserByUID(FirebaseHelper.UserDataCollection().getCurrentUser()?.uid) {
                appUser = it
            }
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val imageUri: Uri? = data.data
                        if (imageUri != null) {

                            // Upload the image to Firebase Storage and update the user's profile image
                            FirebaseHelper.DocumentCollection().updateDocument(
                                collectionRef = usersRef,
                                documentId = appUser?.id,
                                fieldName = fieldName,
                                fieldValue = imageUri.toString()) { success, message ->
                                if (success) showSnackBar(requireView(), message)
                                else showSnackBar(requireView(), message)
                            }
                        }
                    }
                }
            }
    }
}