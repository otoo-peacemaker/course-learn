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


    }

    object CheckPermissions{
        private const val readPhonePermission = Manifest.permission.READ_PHONE_STATE
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val readPostNotificationPermission = Manifest.permission.POST_NOTIFICATIONS
        private const val granted = PackageManager.PERMISSION_GRANTED

        fun readPhoneStatePermission(context: Context):Boolean?{
            if (ContextCompat.checkSelfPermission(context, readPhonePermission) == granted) return true
            else requestPhonePermission(context)
            return null
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun readPostNotificationPermission(context: Context):Boolean?{
            if (ActivityCompat.checkSelfPermission(context, readPostNotificationPermission)== granted) return true
            else requestPostNotificationPermission(context)
            return null
        }

         private fun requestPhonePermission(context: Context) {
            ActivityCompat.requestPermissions(context as Activity,
                arrayOf(readPhonePermission), PERMISSION_REQUEST_CODE
            )
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun requestPostNotificationPermission(context: Context) {
            ActivityCompat.requestPermissions(context as Activity,
                arrayOf(readPostNotificationPermission), PERMISSION_REQUEST_CODE
            )
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