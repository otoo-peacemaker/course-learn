package com.peacemaker.android.courselearn.ui.util

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
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
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.databinding.AppButtonBinding
import com.peacemaker.android.courselearn.databinding.OutlineTextButtonBinding
import com.peacemaker.android.courselearn.databinding.ProgressBarLayoutBinding
import org.json.JSONArray
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

open class BaseFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FirebaseApp.initializeApp(requireContext())
        log("BaseFragment", "::::::::::::::::::::::::::::FirebaseApp.initializeApp")
    }

    fun setTextViewPartialColor(
        textView: TextView,
        fullText: String,
        partialText: String,
        color: Int) {
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
        message: (String) -> Unit
    ): Boolean {
        // Check if email is valid
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        if (!email.matches(emailRegex)) {
            message.invoke("Email is not valid")
            return false
        }
        // Check if password meets criteria
        //val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")
        val regex =
            Regex("^(?=.*\\d)(?=.*[!@#\$%^&*(),.?\":{}|<>])[a-zA-Z0-9!@#\$%^&*(),.?\":{}|<>]{8}$")
        if (!password.matches(regex)) {
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

    /**
     * This function takes in a view, a message, and an action to be performed when the "Retry" button is clicked.
     * @param view view that the SnackBar should be displayed on (for example, a CoordinatorLayout or a ConstraintLayout).
     * @param message to be displayed in the SnackBar.
     * @param action A lambda expression that defines the action to be performed when the "Retry" button is clicked.
     * */
    fun showRetrySnackBar(view: View, message: String, action: () -> Unit) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Retry") { action.invoke() }
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

    fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        suggestions: List<String>,
        onItemClick: ((String) -> Unit)? = null) {

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
        hintTextResId: Int?,
        autoCompleteTextView: AutoCompleteTextView?,
        dropDownImgId: ImageView?,
        listItem: List<String>?,
        markRequired: Boolean? = false,
        selectedDropItem: ((data: String) -> Unit)? = null
    ) {
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
        width: Int? = null,
        height: Int? = null,
        crossinline block: (AlertDialog.Builder).(T) -> Unit = {}
    ): AlertDialog {
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
        string: String,
        callback: (() -> Any)? = null) {
        btnLayoutBinding?.containedButton?.apply {
            text = string
          //  backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.transparent, null))
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
        string: String,
        callback: (() -> Any)? = null) {
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

    fun navigateTo(@IdRes navigationIdRes: Int){
        findNavController().navigate(navigationIdRes)
    }
    fun showLoadingScreen(loader: ProgressBarLayoutBinding ? = ProgressBarLayoutBinding.inflate(layoutInflater), visibility:Boolean){
        if (visibility) loader?.root?.visibility = View.VISIBLE else loader?.root?.visibility = View.INVISIBLE
    }

    fun underImplementation(){
        showToast(requireContext(),"Under implementation")
    }
}