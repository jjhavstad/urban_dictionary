package com.solkismet.urbandictionary.ui.adapters

import android.app.Application
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.databinding.ListItemSoundBinding
import com.solkismet.urbandictionary.ui.contracts.SoundEventHandler
import com.solkismet.urbandictionary.ui.viewmodels.SoundViewModel

class SoundListAdapter(application: Application,
                       val onPlaySound: SoundEventHandler.OnPlaySound) :
    RecyclerView.Adapter<SoundListAdapter.ViewHolder>() {
    var data: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val unselectedTextColor =
        ContextCompat.getColor(application.applicationContext.applicationContext,
            R.color.colorPrimary)
    val selectedTextColor =
        ContextCompat.getColor(application.applicationContext.applicationContext,
            R.color.colorAccent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListItemSoundBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.list_item_sound,
                parent,
                false)
        binding.viewModel = SoundViewModel(onPlaySound,
            object: SoundEventHandler.OnUpdateSoundListItem {
                override fun selectSoundListItem() {
                    binding.playSoundIcon.setColorFilter(selectedTextColor)
                    binding.playSoundText.setTextColor(selectedTextColor)
                    binding.playSoundText.setTypeface(Typeface.DEFAULT_BOLD)
                }
                override fun unselectSoundListItem() {
                    binding.playSoundIcon.setColorFilter(unselectedTextColor)
                    binding.playSoundText.setTextColor(unselectedTextColor)
                    binding.playSoundText.setTypeface(Typeface.DEFAULT)
                }
            })
        return ViewHolder(binding.root, binding)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data?.let {
            val soundFileText =
                holder.binding.root.context.getString(R.string.audio_file, position+1)
            holder.binding.viewModel?.setSoundFileText(soundFileText)
            holder.binding.viewModel?.soundUrl = it.get(position)
        }
    }

    class ViewHolder(itemView: View,
                     val binding: ListItemSoundBinding) : RecyclerView.ViewHolder(itemView)
}
