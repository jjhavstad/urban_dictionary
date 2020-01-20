package com.solkismet.urbandictionary.ui.adapters

import android.app.Application
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.viewmodels.SoundViewModel
import com.solkismet.urbandictionary.viewmodels.WordDetailViewModel
import kotlinx.android.synthetic.main.list_item_sound.view.*

class SoundListAdapter(
    application: Application,
    activity: FragmentActivity,
    private val onSoundClickAction: OnSoundClickAction
) : ListAdapter<String, SoundListAdapter.ViewHolder>(SoundUriDiff()) {
    interface OnSoundClickAction {
        fun playSound(
            url: String,
            onPlaySoundStarted: (recyclerView: RecyclerView) -> Unit,
            onPlaySoundStopped: (recyclerView: RecyclerView) -> Unit
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

    private val viewModel: WordDetailViewModel = ViewModelProviders.of(activity).get(WordDetailViewModel::class.java)

    init {
        viewModel.onSoundAction = object: WordDetailViewModel.OnSoundAction {
            override fun playSound(soundUrl: String, position: Int) {
                onSoundClickAction.playSound(
                    soundUrl,
                    { recyclerView ->
                        recyclerView.findViewHolderForAdapterPosition(position)?.let { _viewHolder ->
                            selectSoundListItem(_viewHolder.itemView)
                        }
                    },
                    { recyclerView ->
                        recyclerView.findViewHolderForAdapterPosition(position)?.let { _viewHolder ->
                            unSelectSoundListItem(_viewHolder.itemView)
                        }
                    }
                )
            }

            override fun stopAll() {
                onSoundClickAction.stopAll { _recyclerView ->
                    stopAll(_recyclerView)
                }
            }
        }
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
                playSound(holder, position)
            }
            holder.itemView.play_sound_text.setOnClickListener {
                playSound(holder, position)
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

    private fun playSound(holder: ViewHolder, position: Int) {
        holder.soundViewModel.soundUrl?.let { _soundUrl ->
            viewModel.playSound(_soundUrl, position)
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
