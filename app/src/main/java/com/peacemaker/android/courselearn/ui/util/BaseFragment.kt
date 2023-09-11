package com.peacemaker.android.courselearn.ui.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.databinding.AppButtonBinding
import com.peacemaker.android.courselearn.databinding.OutlineTextButtonBinding
import com.peacemaker.android.courselearn.databinding.ProgressBarLayoutBinding
import com.peacemaker.android.courselearn.model.AppMessages
import com.peacemaker.android.courselearn.model.MessageBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.InputStreamReader
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

open class BaseFragment : Fragment() {
    private lateinit var backPressedCallback: OnBackPressedCallback

    fun setTextViewPartialColor(
        textView: TextView, fullText: String,
        partialText: String, color: Int) {
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(partialText)
        if (startIndex >= 0) {
            val endIndex = startIndex + partialText.length
            spannableString.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = spannableString
    }

    /**
     * This function takes in an email and password as strings and uses regular expressions to check if they are valid.
     * @param email it checks if the string matches the format of a typical email address
     * @param password  it checks if the string contains at least one lowercase letter,
     * one uppercase letter, one number, and is at least 8 characters long
     * */
    fun validateEmailAndPassword(
        email: String,
        password: String,
        message: (String) -> Unit): Boolean {
        // Check if email is valid
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        if (!email.matches(emailRegex)) {
            message.invoke("Email is not valid")
            return false
        }

        if (!isValidPasswordFormat(password)) {
            message.invoke("Please enter a strong password ")
            return false
        }
        // If both email and password are valid, return true
        return true
    }

    fun validateString(string: String): Boolean {
        return with(string) {
            this.isNotEmpty()
            this.length > 3
        }
    }

    private fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$")
        return passwordREGEX.matcher(password).matches()
    }

    /**
     * This function takes in a view, a message, and an action to be performed when the "Retry" button is clicked.
     * @param view view that the SnackBar should be displayed on (for example, a CoordinatorLayout or a ConstraintLayout).
     * @param message to be displayed in the SnackBar.
     * @param action A lambda expression that defines the action to be performed when the "Retry" button is clicked.
     * */
    fun showRetrySnackBar(view: View, message: String, actionText:String?="Retry",action: () -> Unit) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction(actionText) { action.invoke() }
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                view.context,
                R.color.primary
            )
        )
        snackBar.show()
    }
    fun showSnackBar(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        val textView = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
        snackBarView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.primary))
        snackBar.show()
    }
    fun showActionBarOnFragment(fragment: Fragment, show: Boolean) {
        val actionBar = (fragment.requireActivity() as AppCompatActivity).supportActionBar
        if (show) actionBar?.show() else actionBar?.hide()
    }
    fun getDrawable(resId: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), resId)
    }

    fun changeResDrawable(@DrawableRes resId: Int): Drawable? {
       return ResourcesCompat.getDrawable(resources, resId, null)
    }

    fun changeResColor(@ColorRes resId: Int): Int {
        return resources.getColor(resId, null)
    }

    fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        suggestions: List<String>, onItemClick: ((String) -> Unit)? = null) {
        val adapter = ArrayAdapter(
            autoCompleteTextView.context,
            android.R.layout.simple_dropdown_item_1line, suggestions)
        autoCompleteTextView.setAdapter(adapter)
        // autoCompleteTextView.threshold = 1
        autoCompleteTextView.setDropDownBackgroundDrawable(
            getDrawable(android.R.drawable.arrow_down_float)
        )
        onItemClick?.let { listener ->
            autoCompleteTextView.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val selectedItem = adapter.getItem(position) as String
                    listener.invoke(selectedItem)
                }
        }
    }

    /**
     * A function to show drop down list
     * @param hintTextResId auto complete hint text
     * @param autoCompleteTextView auto-complete textview
     * @param dropDownImgId drop down imageview
     * @param listItem list of items to the autoCompleteTextView
     * @param markRequired mark true or false
     * @param selectedDropItem callback to get selected item from the drop down list
     * */
    fun autoCompleteDropDownMenu(
        hintTextResId: Int?, autoCompleteTextView: AutoCompleteTextView?,
        dropDownImgId: ImageView?, listItem: List<String>?, markRequired: Boolean? = false,
        selectedDropItem: ((data: String) -> Unit)? = null) {
        var part = ""
        if (markRequired == true) part = "*"
        autoCompleteTextView?.hint = setStringPartColor(
            getString(hintTextResId!!), part, resources.getColor(android.R.color.holo_red_light, null)
        )
        if (listItem == null) return showToast(requireContext(),"No item in the list")
        if (autoCompleteTextView != null) {
            if (dropDownImgId != null) {
                if (listItem.isNotEmpty()) {
                    textSearchDrop(autoCompleteTextView, dropDownImgId, listItem)
                } else {
                    showSnackBar(requireView(),"No data found")
                }
            }
        }
        autoCompleteTextView?.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if (selectedItem.isNotEmpty()) {
                if (selectedDropItem != null) {
                    selectedDropItem(selectedItem)
                }
                printLogs("selectedItem", selectedItem)
            } else {
                showSnackBar(requireView(),"Please select item")
            }
        }
    }

    inline fun <reified T : ViewBinding> Fragment.inflateViewBindingDialog(
        crossinline bindingInflater: (LayoutInflater) -> T,
        width: Int? = null, height: Int? = null,
        crossinline block: (AlertDialog.Builder).(T) -> Unit = {}): AlertDialog {
        val binding = bindingInflater(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .apply { block(binding) }
            .create()
            .apply {
                setView(binding.root)
            }
        if (width != null && height != null) {
            dialog.window?.setLayout(width, height)
        }
        return dialog
    }
    fun handleOnBackPressed(onBackPressed: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })
    }
    fun setStringPartColor(org: String, part: String, partColor: Int): Spannable{
        val startPos = org.indexOf(part, 0, false)
        val endPos = startPos+part.length

        val spanString = Spannable.Factory.getInstance().newSpannable(org)
        spanString.setSpan(ForegroundColorSpan(partColor), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spanString
    }
    inline fun <reified T> readJsonData(context: Context, fileName: String): List<T> {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<T>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val gson = Gson()
            val item = gson.fromJson(jsonObject.toString(), T::class.java)
            list.add(item)
        }
        return list
    }

    inline fun <reified T> parseJsonFileToListOfDataClass(context: Context, fileName: String): List<T>? {
        val gson = Gson()
        val assetManager = context.assets

        val inputStream = assetManager.open(fileName)
        val reader = InputStreamReader(inputStream)

        return try {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            reader.close()
            inputStream.close()
        }
    }

    fun generateSalt(): String {
        val secureRandom = SecureRandom()
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.DEFAULT)
    }
    fun showToast(context: Context, strMsg: String){
        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show()
    }
    fun hashPassword(password: String, salt: String, pepper: String): String {
        val message = salt + password + pepper
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(messageBytes)
        return Base64.encodeToString(hashBytes, Base64.DEFAULT)
    }
    fun printLogs(strTag: String,  strValues: String){
        var printStr = strValues

        if(printStr.isEmpty()) printStr = "Empty values"

        Log.w(strTag,printStr)
    }
    fun verifyPasswordWithSalt(password: String, hashedPassword: String): Boolean {
        val parts = hashedPassword.split(":")
        if (parts.size != 2) {
            return false
        }
        val salt = parts[1]
        val passwordWithSalt = password + salt
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(passwordWithSalt.toByteArray())
        val hashedPasswordWithSalt = Base64.encodeToString(digest, Base64.DEFAULT) + ":" + salt
        return hashedPasswordWithSalt == hashedPassword
    }

    fun log(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun getCurrentMonthYear(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    fun <T> filterListBySubstring(list: List<T>, substring: String): List<T> {
        return list.filter { it.toString().contains(substring) }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { _, _ ->
                // User confirmed, close the app
                requireActivity().finish()

            }
            .setNegativeButton("No", null)
            .show()
    }
    fun Fragment.showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { _, _ ->
                // User confirmed, close the app
                requireActivity().finish()

            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun textSearchDrop(spSearch: AutoCompleteTextView, imgD: ImageView, list: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item, list
        )
    }

    fun backPressedCallback(destId: Int) {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController()
                if (navController.currentBackStackEntry?.destination?.id == destId) {
                    //requireActivity().finish()
                    showConfirmationDialog()
                } else {
                    // navController.popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, backPressedCallback
        )
    }

    fun setAppButton(
        btnLayoutBinding: AppButtonBinding? = AppButtonBinding.inflate(layoutInflater),
        string: String, callback: (() -> Any)? = null) {
        btnLayoutBinding?.containedButton?.apply {
            text = string
            setOnClickListener {
                try {
                    callback?.invoke()
                } catch (e: Exception) {
                    printLogs("FloatingButtonLayoutBinding", e.toString())
                }
            }
        }
    }

    fun setTextButton(
        btnLayoutBinding: OutlineTextButtonBinding? = OutlineTextButtonBinding.inflate(layoutInflater),
        string: String, callback: (() -> Any)? = null) {
        btnLayoutBinding?.outlinedButton?.apply {
            text = string
            //backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.transparent, null))
            setOnClickListener {
                try {
                    callback?.invoke()
                } catch (e: Exception) {
                    printLogs("FloatingButtonLayoutBinding", e.toString())
                }
            }
        }
    }

    fun navigateTo(@IdRes navigationIdRes: Int?){
        try {
            if (navigationIdRes != null) {
                findNavController().navigate(navigationIdRes)
            }
        }catch (e:Exception){
            showRetrySnackBar(requireView(),e.message.toString()){}
        }

    }

    fun navigateTo(navDirections: NavDirections?){
        try {
            if (navDirections != null) {
                findNavController().navigate(navDirections)
            }
        }catch (e:Exception){
            showRetrySnackBar(requireView(),e.message.toString()){}
        }
    }

    fun navigateTo(@IdRes navigationIdRes: Int?, bundle: Bundle){
        try {
            if (navigationIdRes != null) {
                findNavController().navigate(navigationIdRes, args = bundle)
            }
        }catch (e:Exception){
            showRetrySnackBar(requireView(),e.message.toString()){}
        }
    }

    fun showLoadingScreen(loader: ProgressBarLayoutBinding ? = ProgressBarLayoutBinding.inflate(layoutInflater), visibility:Boolean){
        if (visibility) loader?.root?.visibility = View.VISIBLE else loader?.root?.visibility = View.INVISIBLE
    }

    fun underImplementation(){
        showToast(requireContext(),"Under implementation")
    }

    fun <T> observeLiveDataResource(
        liveData: LiveData<Resource<T>>, onSuccess: (T) -> Unit,
        loader: ProgressBarLayoutBinding ? = ProgressBarLayoutBinding.inflate(layoutInflater),
        onError: ((String) -> Unit?)? =null,
        onException: ((String) -> Unit?)? =null,
        onLoading: (() -> Unit?)? =null) {
        liveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    showLoadingScreen(loader = loader, visibility = false)
                    resource.data?.let { data ->
                        onSuccess(data)
                    }
                }
                Status.ERROR -> {
                    showLoadingScreen(loader = loader,visibility = false)
                    resource.message?.let { message ->
                        if (onError != null) {
                            onError(message)
                          //  showSnackBar(requireView(),message)
                        }else{
                            showSnackBar(requireView(),message)
                        }
                    }
                }
                Status.LOADING -> {
                    onLoading?.invoke()
                    showLoadingScreen(loader = loader,visibility = true)
                }

                Status.EXCEPTION->{
                    showLoadingScreen(loader = loader,visibility = false)
                    resource.message?.let {
                        onException?.invoke(it)
                        showSnackBar(requireView(),it)
                    }
                }
            }
        }
    }
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = "^[+]?[0-9]{8,15}\$"
        return phoneNumber.matches(Regex(phoneRegex))
    }
     fun composeEmail(recipient: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        }
        // Set the package name of the Gmail app to ensure it opens directly if available
        intent.setPackage("com.google.android.gm")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            // Gmail app is not installed, open Play Store to download it
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.google.android.gm")
                setPackage("com.android.vending") // Set the package name of the Play Store app
            }

            if (playStoreIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(playStoreIntent)
            } else {
                // If the Play Store app is not available, open the Play Store in the browser
                val playStoreWebIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm")
                }
                startActivity(playStoreWebIntent)
            }
        }
    }
     fun openGmailApp() {
        val intent = requireActivity().packageManager.getLaunchIntentForPackage("com.google.android.gm")
        if (intent != null) {
            startActivity(intent)
        } else {
            // If the Play Store app is not available, open the Play Store in the browser
            val playStoreWebIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm")
            }
            startActivity(playStoreWebIntent)
        }
    }

    /**
     * placeholderResId: A resource ID of a placeholder image to be displayed while the actual image is loading.
     * errorResId: A resource ID of an error image to be displayed if the image loading fails.
     * diskCacheStrategy: A strategy for caching the image on disk.
     * transition: A transition animation to be applied when displaying the image.
     * */
    fun loadImageToImageView(context: Context, imageUrl: String?, imageView: ImageView,
        placeholderResId: Int? = null, errorResId: Int? = null, diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL,
        transition: DrawableTransitionOptions = DrawableTransitionOptions.withCrossFade()) {
        val glideRequest = Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(diskCacheStrategy)
            .transition(transition)
        placeholderResId?.let { glideRequest.placeholder(it) }
        errorResId?.let { glideRequest.error(it) }
        glideRequest.into(imageView)
    }

    fun setConstraintLayoutBackground(context: Context, layout: ConstraintLayout,
        imageUrl: String, placeholderResId: Int? = null, errorResId: Int? = null,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL,
        transition: DrawableTransitionOptions = DrawableTransitionOptions.withCrossFade()) {
        val imageView = ImageView(context)

        val glideRequest = Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(diskCacheStrategy)
            .transition(transition)

        placeholderResId?.let { glideRequest.placeholder(it) }
        errorResId?.let { glideRequest.error(it) }
        glideRequest.into(imageView)
        layout.background = imageView.drawable
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

    suspend fun loadImageFromUrl(url: Uri) = withContext(Dispatchers.IO) {
        return@withContext try {
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.loading_animation)  // Placeholder if image loading fails
                .error(R.drawable.ico_person)        // Error placeholder if loading fails
                .submit()
                .get()
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentDateTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(currentTime))
    }
    fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(currentTime))
    }
    fun getTimeAgoString(targetTime: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - targetTime

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just now"
            minutes < 2 -> "1 minute ago"
            minutes < 60 -> "$minutes minutes ago"
            hours < 2 -> "1 hour ago"
            hours < 24 -> "$hours hours ago"
            days < 2 -> "Yesterday"
            days < 7 -> "$days days ago"
            else -> {
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                dateFormat.format(Date(targetTime))
            }
        }
    }
     fun addMessage(userName: String?, status: String?, message: String, time:Long, profileImg: String?){
        val appMessages = AppMessages(
            name = userName,
            time = time,
            status = status,
            content = MessageBody(
                desc = message,
                profileImg = profileImg
            )
        )
        FirebaseHelper.DocumentCollection()
            .addDocumentsToCollection("messages", listOf(appMessages)){ _, _->
//                if (success) showSnackBar(requireView(),message) else showSnackBar(requireView(),message)
            }
    }


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


    fun startShimmerEffect(shimmerContainer: View, durationMillis: Long = 3000) {
        if (shimmerContainer is ShimmerFrameLayout) {
            val shimmer = Shimmer.AlphaHighlightBuilder()
                .setDuration(durationMillis)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(1f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build()

            shimmerContainer.setShimmer(shimmer)
        } else {
            throw IllegalArgumentException("The provided view is not a ShimmerFrameLayout")
        }
    }

    fun stopShimmerEffect(shimmerContainer: View) {
        if (shimmerContainer is ShimmerFrameLayout) {
            shimmerContainer.stopShimmer()
        } else {
            throw IllegalArgumentException("The provided view is not a ShimmerFrameLayout")
        }
    }

}