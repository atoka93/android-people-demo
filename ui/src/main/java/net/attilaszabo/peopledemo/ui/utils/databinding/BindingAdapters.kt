package net.attilaszabo.peopledemo.ui.utils.databinding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import net.attilaszabo.peopledemo.ui.utils.GlideRequestFinishedListener

@BindingAdapter("visible")
fun setVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter(value = ["bind:imageUrl", "bind:loadedListener", "bind:requestOptions"], requireAll = false)
fun loadImage(
    view: ImageView,
    imageUrl: String,
    loadedListener: GlideRequestFinishedListener?,
    glideRequestOptions: RequestOptions?
) {
    Glide.with(view)
        .load(imageUrl)
        .apply(glideRequestOptions ?: RequestOptions())
        .listener(loadedListener)
        .into(view)
}
