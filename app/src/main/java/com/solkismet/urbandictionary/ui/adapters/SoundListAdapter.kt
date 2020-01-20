package com.solkismet.urbandictionary.ui.adapters

import android.app.Application
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.ui.viewmodels.SoundViewModel
import kotlinx.android.synthetic.main.list_item_sound.view.*

class SoundListAdapter(
    application: Application,
    private val onSoundClickAction: OnSoundClickAction
) : ListAdapter<String, SoundListAdapter.ViewHolder>(SoundUriDiff()) {
    interface OnSoundClickAction {
        fun playSound(
            url: String,
            onPlaySoundStarted: () -> Unit,
            onPlaySoundStopped: () -> Unit
        )
        fun stopAll(updateList: (recyclerView: RecyclerView) -> Unit)
    }

    private val unselectedTextColor by lazy {
        ContextCompat.getColor(
            application.applicationContext.applicationContext,
            R.color.colorPrimary
        )
    }

    private val selectedTextColor by lazy {
        ContextCompat.getColor(
            application.applicationContext.applicationContext,
            R.color.colorAccent
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_sound,
            parent,
            false
        )

        val viewModel = SoundViewModel()
        return ViewHolder(view, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { _soundUrl ->
            holder.soundViewModel.soundUrl = _soundUrl
            holder.itemView.play_sound_text.text = holder.itemView.context.getString(R.string.audio_file, position+1)
            holder.itemView.play_sound_icon.setOnClickListener {
                playSound(holder)
            }
            holder.itemView.play_sound_text.setOnClickListener {
                playSound(holder)
            }
        }
    }

    private fun stopAll(recyclerView: RecyclerView) {
        for (i in 0..itemCount) {
            recyclerView.findViewHolderForAdapterPosition(i)?.let { _viewHolder ->
                unSelectSoundListItem(_viewHolder.itemView)
            }
        }
    }

    private fun playSound(holder: ViewHolder) {
        onSoundClickAction.stopAll { _recyclerView ->
            stopAll(_recyclerView)
        }
        holder.soundViewModel.soundUrl?.let { _soundUrl ->
            onSoundClickAction.playSound(
                _soundUrl,
                {
                    selectSoundListItem(holder.itemView)
                },
                {
                    unSelectSoundListItem(holder.itemView)
                }
            )
        }
    }

    private fun selectSoundListItem(view: View) {
        view.play_sound_icon.setColorFilter(selectedTextColor)
        view.play_sound_text.setTextColor(selectedTextColor)
        view.play_sound_text.typeface = Typeface.DEFAULT_BOLD
    }

    private fun unSelectSoundListItem(view: View) {
        view.play_sound_icon.setColorFilter(unselectedTextColor)
        view.play_sound_text.setTextColor(unselectedTextColor)
        view.play_sound_text.typeface = Typeface.DEFAULT
    }

    class ViewHolder(
        itemView: View,
        val soundViewModel: SoundViewModel
    ) : RecyclerView.ViewHolder(itemView)

    class SoundUriDiff : DiffUtil.ItemCallback<String>() {
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.equals(newItem, ignoreCase = true)
        }

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.equals(newItem, ignoreCase = true)
        }
    }
}
