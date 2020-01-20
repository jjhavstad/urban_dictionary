package com.solkismet.urbandictionary.ui.activities

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.databinding.ActivityWordDetailBinding
import com.solkismet.urbandictionary.ui.adapters.SoundListAdapter
import com.solkismet.urbandictionary.ui.viewmodels.SoundViewModel
import com.solkismet.urbandictionary.ui.viewmodels.WordDetailViewModel
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class WordDetailActivity : AppCompatActivity(), SoundViewModel.OnPlaySound {

    companion object {
        private const val DATA_KEY = "data_key"

        @JvmStatic
        fun getIntent(context: Context, wordDetail: WordDetail): Intent {
            return Intent(context, WordDetailActivity::class.java)
                .putExtra(DATA_KEY, wordDetail)
        }
    }

    private var binding: ActivityWordDetailBinding? = null
    private var viewModel: WordDetailViewModel? = null
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initDataBinding()
        initToolbar()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

    override fun playSound(url: String,
                           onPlaySoundStarted: () -> Unit,
                           onPlaySoundStopped: () -> Unit) {
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(url)
                setOnPreparedListener { mp -> mp.start(); onPlaySoundStarted() }
                setOnCompletionListener { onPlaySoundStopped() }
                prepareAsync()
            }
        } catch (e: IllegalArgumentException) {
            if (!TextUtils.isEmpty(e.message)) {
                Log.e("MediaPlayer", "Error preparing media player: ${e.message}\n${e.stackTrace.joinToString("\n")}")
            } else {
                Log.e("MediaPlayer", "Caught IllegalArgumentException: \n${e.stackTrace.joinToString("\n")}")
            }
            mediaPlayer.release()
        } catch (e: IOException) {
            if (!TextUtils.isEmpty(e.message)) {
                Log.e("MediaPlayer", "Error preparing media player: ${e.message}\n${e.stackTrace.joinToString("\n")}")
            } else {
                Log.e("MediaPlayer", "Caught IOException: \n${e.stackTrace.joinToString("\n")}")
            }
            mediaPlayer.release()
        } catch (e: IllegalStateException) {
            if (!TextUtils.isEmpty(e.message)) {
                Log.e("MediaPlayer", "Error preparing media player: ${e.message}\n${e.stackTrace.joinToString("\n")}")
            } else {
                Log.e("MediaPlayer", "Caught IOException: \n${e.stackTrace.joinToString("\n")}")
            }
            mediaPlayer.release()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(WordDetailViewModel::class.java)
        intent?.let {
            viewModel?.searchResultItem?.value = intent.getParcelableExtra(DATA_KEY)
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_word_detail)
        binding?.viewModel = viewModel
        val adapter = SoundListAdapter(application, this)
        binding?.detailItemSoundSampleList?.adapter = adapter
        adapter.data = viewModel?.searchResultItem?.value?.soundUrls
    }

    private fun initToolbar() {
        supportActionBar?.title = viewModel?.searchResultItem?.value?.word
    }
}
