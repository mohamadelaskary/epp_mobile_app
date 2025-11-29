package net.gbs.epp_project.Tools

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ApkInstaller {
    fun installApk(context: Context, apkPath: String) {
        val apkFile = File(apkPath)
        val authority = "${context.packageName}.fileprovider"
        val apkUri = FileProvider.getUriForFile(context, authority, apkFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}