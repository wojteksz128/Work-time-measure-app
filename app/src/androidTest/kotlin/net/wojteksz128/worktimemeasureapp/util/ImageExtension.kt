package net.wojteksz128.worktimemeasureapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import net.wojteksz128.worktimemeasureapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.io.File
import java.io.FileOutputStream

fun createTestImageUri(context: Context): Uri {
    val imageFile = File(context.cacheDir, "test_image.png")
    if (imageFile.exists()) {
        imageFile.delete()
    }
    val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    bitmap.setPixel(0, 0, Color.RED)

    FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    val authority = "${context.packageName}.provider"
    return FileProvider.getUriForFile(context, authority, imageFile)
}

fun isDefaultImage(context: Context): Matcher<View> = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("with default profile image")
    }

    override fun matchesSafely(item: View): Boolean {
        if (item !is ImageView) return false
        val currentDrawable = item.drawable as? BitmapDrawable ?: return false
        val currentBitmap = currentDrawable.bitmap

        val defaultBitmap =
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round)

        return currentBitmap.sameAs(defaultBitmap)
    }
}

fun getInternalImageUri(context: Context, originalUri: Uri): String =
    File(context.filesDir, originalUri.path!!.takeLastWhile { it != '/' }).path