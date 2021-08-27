package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import kotlinx.coroutines.*
import net.wojteksz128.worktimemeasureapp.R
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageViewPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    private val TAG = ImageViewPreference::class.java.simpleName

    private var job = Job()
    private var scopeForSaving = CoroutineScope(job + Dispatchers.Main)

    private lateinit var imageBitmap: Bitmap

    private lateinit var imageView: ImageView
    private lateinit var loadingIndicator: FrameLayout
    private lateinit var imageLoader: ActivityResultLauncher<String>

    init {
        layoutResource = R.layout.image_view_preference
    }

    fun attachImageSelector(activityResultCaller: ActivityResultCaller) {
        imageLoader =
            activityResultCaller.registerForActivityResult(ActivityResultContracts.GetContent())
            { uri ->
                uri?.let {
                    scopeForSaving.launch {
                        loadingIndicator.visibility = View.VISIBLE
                        changeImage(it)
                        loadingIndicator.visibility = View.INVISIBLE
                    }
                }
            }
    }

    private suspend fun changeImage(uri: Uri) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "changeImage: Selected image: $uri")
            replaceImage(uri)
        }
        loadImage()
    }

    private fun replaceImage(newImageUri: Uri) {
        val newImageFile = File(context.filesDir, newImageUri.path!!.takeLastWhile { it != '/' })

        Log.d(TAG, "replaceImage: Copy new image to internal storage start")
        copyImageToInternalStorage(newImageUri, newImageFile)
        Log.d(TAG, "replaceImage: Copy new image to internal storage end")

        val oldImageFilePath: String? = getPersistedString(null)
        val newImageFilePath: String? = newImageFile.absolutePath

        if (oldImageFilePath != newImageFilePath)
            oldImageFilePath?.let { File(it).delete() }
        persistString(newImageFilePath)
    }

    private fun copyImageToInternalStorage(newImageUri: Uri, newImageFile: File) {
        val inputStream = context.contentResolver.openInputStream(newImageUri)?.buffered()
        val outputStream = BufferedOutputStream(FileOutputStream(newImageFile))

        inputStream?.let {
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            do {
                outputStream.write(buffer)
            } while (inputStream.read(buffer) > 0)

            inputStream.close()
            outputStream.flush()
            outputStream.close()
        }

    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        loadingIndicator =
            holder?.findViewById(R.id.image_view_preference_loading_indicator) as FrameLayout

        imageView = holder.findViewById(R.id.image_view_preference_image_preview) as ImageView
        imageView.setOnClickListener {
            Log.i(TAG, "onClickListener: Invoke selecting image")
            imageLoader.launch("image/*")
        }
        scopeForSaving.launch {
            loadingIndicator.visibility = View.VISIBLE
            loadImage()
            loadingIndicator.visibility = View.INVISIBLE
        }
    }

    private suspend fun loadImage() {
        val imageBitmap: Bitmap?
        withContext(Dispatchers.IO) {
            imageBitmap = BitmapFactory.decodeFile(getPersistedString(null))
        }
        imageBitmap?.let { setImageBitmap(it) }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return if (this.isPersistent) {
            superState
        } else {
            val myState = SavedState(superState)
            myState.imageBitmap = imageBitmap
            myState
        }

    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            setImageBitmap(state.imageBitmap)
        }
        super.onRestoreInstanceState(state)
    }

    private fun setImageBitmap(imageBitmap: Bitmap) {
        this.imageBitmap = imageBitmap
        this.imageView.setImageBitmap(this.imageBitmap)
    }

    private class SavedState(superState: Parcelable) : BaseSavedState(superState) {

        lateinit var imageBitmap: Bitmap

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeValue(imageBitmap)
        }
    }
}