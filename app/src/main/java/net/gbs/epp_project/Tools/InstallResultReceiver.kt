package net.gbs.epp_project.Tools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.widget.Toast

class InstallResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)
        val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)

        when (status) {
            PackageInstaller.STATUS_SUCCESS ->
                Toast.makeText(context, "App updated successfully", Toast.LENGTH_LONG).show()

            else ->
                Toast.makeText(context, "Install failed: $msg", Toast.LENGTH_LONG).show()
        }
    }
}