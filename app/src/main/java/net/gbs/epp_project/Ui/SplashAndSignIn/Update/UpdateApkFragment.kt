package net.gbs.epp_project.Ui.SplashAndSignIn.Update

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ApkInstaller
import net.gbs.epp_project.Tools.Tools.changeTitle
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import net.gbs.epp_project.databinding.FragmentUpdateApkBinding

class UpdateApkFragment : BaseFragmentWithViewModel<UpdateApkViewModel, FragmentUpdateApkBinding>() {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUpdateApkBinding
        get() = FragmentUpdateApkBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.downloadApk(USER?.apkUrl!!)
        observeDownloadProgress()
    }

    private fun observeDownloadProgress() {
        viewModel.downloadProgress.observe(viewLifecycleOwner){
            binding.loadingBar.progress = it
            binding.progress.text = "$it%"
        }
        viewModel.downloadStatus.observe(viewLifecycleOwner){
            when(it){
                UpdateApkViewModel.DownloadStatus.IDLE -> {
                    binding.refresh.visibility = GONE
                    binding.progress.visibility = GONE
                    binding.loadingBar.visibility = GONE
                    binding.animationView.setAnimation(R.raw.update)
                }
                UpdateApkViewModel.DownloadStatus.DOWNLOADING -> {
                    binding.refresh.visibility = GONE
                    binding.progress.visibility = VISIBLE
                    binding.loadingBar.visibility = VISIBLE
                    binding.animationView.setAnimation(R.raw.update)

                }
                UpdateApkViewModel.DownloadStatus.SUCCESS -> {
                    binding.refresh.visibility = GONE
                    binding.progress.visibility = VISIBLE
                    binding.loadingBar.visibility = VISIBLE
                    binding.animationView.setAnimation(R.raw.success)

                }
                UpdateApkViewModel.DownloadStatus.FAILED -> {
                    binding.refresh.visibility = VISIBLE
                    binding.progress.visibility = VISIBLE
                    binding.loadingBar.visibility = VISIBLE
                    binding.animationView.setAnimation(R.raw.network_fail_anim)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.update),requireActivity() as MainActivity)
    }
}