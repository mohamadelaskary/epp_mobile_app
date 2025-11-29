package net.gbs.epp_project.Ui.SplashAndSignIn.Update

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gbs.epp_project.Model.User
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class DownloadRepository(private val context: Context) {
    suspend fun downloadApk(
        apkUrl: String,
        onProgress: (Int) -> Unit,
        onSuccess : (String) -> Unit
    ): File? = withContext(Dispatchers.IO) {

        try {
            Log.d("DownloadRepository", "downloadStarted:")
            val client = OkHttpClient()
            val request = Request.Builder().url(apkUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return@withContext null
            val body = response.body ?: return@withContext null

            val contentLength = body.contentLength()
            val inputStream = body.byteStream()

            val apkFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "app_version_${USER?.apkVersion}.apk"
            )
            onSuccess(apkFile.absolutePath)
            Log.d("DownloadRepository", "download Response code: ${response.code.toString()}")

            Log.d("DownloadRepository", "downloadApk: $apkFile")

            apkFile.outputStream().use { fileOut ->
                val buffer = ByteArray(8_192)
                var bytesRead: Int
                var totalBytes = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    fileOut.write(buffer, 0, bytesRead)
                    totalBytes += bytesRead

                    if (contentLength > 0) {
                        val progress = (totalBytes * 100 / contentLength).toInt()
                        onProgress(progress)
                    }
                }
            }

            return@withContext apkFile
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("DownloadRepository", "downloadApk: ${e.message}")
            return@withContext null
        }
    }
}