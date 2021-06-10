package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import net.wojteksz128.worktimemeasureapp.R

class ImageViewPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    private lateinit var imageView: ImageView
    lateinit var imageClickListener: View.OnClickListener

    init {
        layoutResource = R.layout.image_view_preference
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        imageView = holder?.findViewById(R.id.image_view_preference_image) as ImageView
        imageView.setOnClickListener(imageClickListener)
    }

}