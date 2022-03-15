package com.example.mgplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mgplayer.model.Song
import com.example.mgplayer.model.repository.SongRepository
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SongViewModel(val repository: SongRepository) : ViewModel() {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val errorsMu: MutableLiveData<String> = MutableLiveData()
    val deleteAllMusicsMu: MutableLiveData<Boolean> = MutableLiveData()
    val setMusicsListMu: MutableLiveData<Boolean> = MutableLiveData()
    val getMusicsListMu: MutableLiveData<List<Song>> = MutableLiveData()

    fun setMusicsList(list: ArrayList<Song>) {
        repository.setMusicsList(list)
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onComplete() {
                    setMusicsListMu.postValue(true)
                }

                override fun onError(e: Throwable) {
                    errorsMu.postValue(e.toString())
                }

            })
    }

    fun deleteAllMusicsList(){
        repository.deleteAllMusics()
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver{
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onComplete() {
                    deleteAllMusicsMu.postValue(true)
                }

                override fun onError(e: Throwable) {
                    errorsMu.postValue(e.toString())
                }

            })
    }

    fun getAllMusics(){
        repository.getAllMusics()
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<List<Song>>{
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: List<Song>) {
                    getMusicsListMu.postValue(t)
                }

                override fun onError(e: Throwable) {
                    errorsMu.postValue(e.toString())
                }

            })
    }

    //return mutable
    fun getMusics(): LiveData<List<Song>>{
        return getMusicsListMu
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onCleared()
    }
}