package net.gbs.epp_project.Tools

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import kotlin.jvm.java

object ApkInstaller {
//    fun installApk(context: Context, apkPath: String) {
//        val apkFile = File(apkPath)
//        val authority = "${context.packageName}.fileprovider"
//        val apkUri = FileProvider.getUriForFile(context, authority, apkFile)
//
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(apkUri, "application/vnd.android.package-archive")
//            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//        context.startActivity(intent)
//    }
fun installApk(context: Context, apkPath: String) {
    val packageInstaller = context.packageManager.packageInstaller
    val apkFile = File(apkPath)
    val length = apkFile.length()

    val params = PackageInstaller.SessionParams(
        PackageInstaller.SessionParams.MODE_FULL_INSTALL
    )

    val sessionId = packageInstaller.createSession(params)
    val session = packageInstaller.openSession(sessionId)

    apkFile.inputStream().use { input ->
        session.openWrite("app_install", 0, length).use { output ->
            input.copyTo(output)
            session.fsync(output)
        }
    }

    // أهم نقطة: intent لنتيجة التثبيت
    val intent = Intent(context, InstallResultReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        sessionId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    session.commit(pendingIntent.intentSender)
    session.close()
}

}

