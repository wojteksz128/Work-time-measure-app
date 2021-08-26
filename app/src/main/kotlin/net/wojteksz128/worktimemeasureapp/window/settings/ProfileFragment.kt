package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.window.settings.property.ImageViewPreference

class ProfileFragment : PreferenceFragmentCompat() {

    lateinit var imageViewPreference: ImageViewPreference
    private lateinit var mailPreference: EditTextPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.profile_preferences, rootKey)

        prepareImageViewPreference()
        prepareMailPreference()
    }

    private fun prepareImageViewPreference() {
        imageViewPreference = findPreference("settings_profile_image")!!
        imageViewPreference.attachImageSelector(this)
    }

    private fun prepareMailPreference() {
        mailPreference = findPreference("settings_profile_mail")!!
        mailPreference.setOnBindEditTextListener { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // empty
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // empty
                }

                override fun afterTextChanged(s: Editable?) {
                    val validationError: String? =
                        if (s?.let { isEmailValid(s.toString()) } == true) null else getString(R.string.settings_profile_mail_error)


                    editText.error = validationError
                    editText.rootView.findViewById<View>(android.R.id.button1).isEnabled =
                        validationError == null
                }
            })
// TODO: 27.08.2021 Clean up code
            /*editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dismissDialog()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }*/
        }
        mailPreference.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) isEmailValid(
                newValue
            ) else false
        }
    }

    /*private fun dismissDialog() {
        for (fragment in requireActivity().supportFragmentManager.fragments) {
            if (fragment is EditTextPreferenceDialogFragmentCompat) {
                fragment.dismiss()
                return
            }
        }
    }*/

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}