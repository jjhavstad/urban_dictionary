package com.solkismet.urbandictionary.ui.activities

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.databinding.ActivityWordDetailBinding
import com.solkismet.urbandictionary.ui.adapters.SoundListAdapter
import com.solkismet.urbandictionary.ui.extensions.withColor
import com.solkismet.urbandictionary.ui.utils.NetworkListenerHelper
import com.solkismet.urbandictionary.viewmodels.WordDetailViewModel
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class WordDetailActivity : AppCompatActivity(), SoundListAdapter.OnSoundClickAction {

    companion object {
        private const val DATA_KEY = "data_key"

        @JvmStatic
        fun getIntent(context: Context, wordDetail: WordDetail): Intent {
            return Intent(context, WordDetailActivity::class.java)
                .putExtra(DATA_KEY, wordDetail.defId)
        }
    }

    private var binding: ActivityWordDetailBinding? = null
    private var viewModel: WordDetailViewModel? = null
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initDataBinding()
        registerNetworkConnectivityListener()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkConnectivityListener()
    }

    override fun playSound(
        url: String,
        onPlaySoundStarted: (recyclerView: RecyclerView) -> Unit,
        onPlaySoundStopped: (recyclerView: RecyclerView) -> Unit
    ) {
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(url)
                setOnPreparedListener {
                    mp -> mp.start()
                    binding?.detailItemSoundSampleList?.let { _recyclerView ->
                        onPlaySoundStarted(_recyclerView)
                    }
                }
                setOnCompletionListener {
                    binding?.detailItemSoundSampleList?.let { _recyclerView ->
                        onPlaySoundStopped(_recyclerView)
                    }
                }
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

    override fun stopAll(updateList: (recyclerView: RecyclerView) -> Unit) {
        mediaPlayer.stop()
        binding?.detailItemSoundSampleList?.let { _recyclerView ->
            updateList(_recyclerView)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(WordDetailViewModel::class.java)
        intent?.let {
            viewModel?.getWordDetailById(intent.getLongExtra(DATA_KEY, -1L))?.let {
                disposables.add(
                    it.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { _wordDetail ->
                                viewModel?.setResultItem(_wordDetail)
                                supportActionBar?.title = viewModel?.getResultItem()?.value?.word
                                (binding?.detailItemSoundSampleList?.adapter as SoundListAdapter).apply {
                                    submitList(viewModel?.getResultItem()?.value?.soundUrls)
                                }
                            },
                            {
                                binding?.root?.let { _rootView ->
                                    Snackbar.make(
                                        _rootView,
                                        R.string.word_detail_retrieval_error,
                                        Snackbar.LENGTH_SHORT
                                    ).apply {
                                        setAction(R.string.search_api_close_error, null)
                                        withColor(ContextCompat.getColor(this@WordDetailActivity, android.R.color.holo_red_dark))
                                        show()
                                    }
                                }
                            }
                        )
                )
            }
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_word_detail)
        binding?.viewModel = viewModel
        val adapter = SoundListAdapter(application, activity = this, onSoundClickAction = this)
        binding?.detailItemSoundSampleList?.adapter = adapter
    }

    private fun registerNetworkConnectivityListener() {
        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = NetworkListenerHelper.registerNetworkStateChangeListener(
            connectivityManager,
            { viewModel?.online = true },
            { viewModel?.online = false }
        )
    }

    private fun unregisterNetworkConnectivityListener() {
        networkCallback?.let { _networkCallback ->
            val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            NetworkListenerHelper.unregisterNetworkStateChangeListener(connectivityManager, _networkCallback)
        }
    }
}
