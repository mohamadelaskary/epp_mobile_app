package net.gbs.epp_project.Tools

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import net.gbs.epp_project.R

class LoadingDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun show() {
        Log.d(TAG, "show: $isShowing")
        if (!isShowing)
            super.show()
    }
}