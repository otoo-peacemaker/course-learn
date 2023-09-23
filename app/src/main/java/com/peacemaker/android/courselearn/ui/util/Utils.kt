package com.peacemaker.android.courselearn.ui.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.PERMISSION_CODE
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.PERMISSION_REQUEST_CODE

object Utils {

    /**
     * A function to navigate to a fragment from anywhere in the navigation graph.
     * This function can be called from any Fragment.
     *
     * @param destinationId The ID of the destination fragment.
     * @param navController The NavController.
     * @param args Optional arguments to pass to the destination fragment.
     * @param navOptions Optional NavOptions for the navigation action.
     */
    fun startFragmentFromAnywhere(
        destinationId: Int,
        navController: NavController,
        args: Bundle? = null,
        navOptions: NavOptions? = null
    ) {
        navController.navigate(destinationId, args, navOptions)
    }

    fun showToast(context: Context, string: String){
        Toast.makeText(context,string, Toast.LENGTH_SHORT).show()
    }



    object Constants {
        const val RC_SIGN_IN = 9001
        const val CREDENTIAL_PICKER_REQUEST = 1  // Set to an unused request code
        const val CHANNEL_ID = "course_added_id"
        const val CHANNEL_NAME = "course_added"
        const val CHANNEL_DESCRIPTION = "course_notification_channel"
        const val NOTIFICATION_ID = 111
        const val USER_PREFERENCES_NAME = "user_preferences"
        const val PERMISSION_REQUEST_CODE = 123 // You can choose any integer value
        const val PERMISSION_CODE = 1001
        const val IMAGE_PICK_CODE = 1000



    }

    object CheckPermissions{
        const val readPhonePermission = Manifest.permission.READ_PHONE_STATE
        const val readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val granted = PackageManager.PERMISSION_GRANTED


        fun checkAndRequestPermission(activity: Activity, permission: String): Boolean {
            // Check if permission is already granted
            if (ContextCompat.checkSelfPermission(activity, permission) != granted) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(activity, arrayOf(permission), PERMISSION_CODE)
                return false
            }
            return true
        }

}

    object Dummy {
         val videoUrls = mutableListOf<String?>(
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
        )
    }
}