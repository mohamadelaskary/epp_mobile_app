package net.gbs.epp_project.Ui.SplashAndSignIn.Update

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Tools.ApkInstaller

class UpdateApkViewModel(private val application: Application,val activity: Activity) : BaseViewModel(application = application,activity) {
    private val repository = DownloadRepository(application)
    private val _downloadProgress = MutableLiveData<Int>()
    val downloadProgress: LiveData<Int> = _downloadProgress

    private val _downloadStatus = MutableLiveData<DownloadStatus>()
    val downloadStatus: LiveData<DownloadStatus> = _downloadStatus

    fun downloadApk(url: String) {
        viewModelScope.launch {
            _downloadStatus.value = DownloadStatus.DOWNLOADING

            val file = repository.downloadApk(url, onProgress =  { progress ->
            _downloadProgress.postValue(progress)
        }, onSuccess = { fileName->

        })

            if (file != null) {
                _downloadStatus.value = DownloadStatus.SUCCESS
                ApkInstaller.installApk(application, file.absolutePath)
            } else
                _downloadStatus.value = DownloadStatus.FAILED
        }
    }

    fun reset() {
        _downloadStatus.value = DownloadStatus.IDLE
        _downloadProgress.value = 0
    }
    enum class DownloadStatus {
        IDLE,
        DOWNLOADING,
        SUCCESS,
        FAILED
    }

}