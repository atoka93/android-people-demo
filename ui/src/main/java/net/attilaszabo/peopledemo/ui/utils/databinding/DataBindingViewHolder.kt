package net.attilaszabo.peopledemo.ui.utils.databinding

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import net.attilaszabo.peopledemo.ui.BR

class DataBindingViewHolder<T>(
    val binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int, item: T) {
        binding.apply {
            setVariable(BR.binding, this)
            setVariable(BR.position, position)
            setVariable(BR.model, item)
            executePendingBindings()
        }
    }
}
