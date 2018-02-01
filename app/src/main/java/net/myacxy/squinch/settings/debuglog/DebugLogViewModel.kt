package net.myacxy.squinch.settings.debuglog

import android.content.SharedPreferences
import android.databinding.ObservableArrayList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.myacxy.squinch.base.ViewModel
import net.myacxy.squinch.helpers.DataHelper
import timber.log.Timber

class DebugLogViewModel(
        private val dataHelper: DataHelper,
        debugLogSharedPreferences: SharedPreferences
) : ViewModel {
    val debugLogEntries = ObservableArrayList<DebugLogEntry>()
    private var disposable: Disposable? = null

    fun onAttach() {
        // TODO: 01.02.2018 live update
        disposable = dataHelper.debugLogEntries
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            debugLogEntries.clear()
                            debugLogEntries.addAll(it)
                        },
                        onError = { Timber.e(it) }
                )
    }

    fun onDetach() {
        disposable?.dispose()
    }
}
