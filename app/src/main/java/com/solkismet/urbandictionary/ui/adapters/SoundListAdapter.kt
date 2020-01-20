package com.solkismet.urbandictionary.ui.adapters

import android.app.Application
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.ui.viewmodels.SoundViewModel
import kotlinx.android.synthetic.main.list_item_sound.view.*

class SoundListAdapter(
    application: Application,
    private val onSoundClickAction: OnSoundClickAction
) : RecyclerView.Adapter<SoundListAdapter.ViewHolder>() {
    interface OnSoundClickAction {
        fun playSound(
            url: String,
            onPlaySoundStarted: () -> Unit,
            onPlaySoundStopped: () -> Unit
        )
        fun stopAll(updateList: (recyclerView: RecyclerView) -> Unit)
    }

    var data: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
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

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data?.let { _dataList ->
            holder.soundViewModel.soundUrl = _dataList[position]
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
        data?.let { _dataList ->
            for (i in _dataList.indices) {
                recyclerView.findViewHolderForAdapterPosition(i)?.let { _viewHolder ->
                    unSelectSoundListItem(_viewHolder.itemView)
                }
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
}
