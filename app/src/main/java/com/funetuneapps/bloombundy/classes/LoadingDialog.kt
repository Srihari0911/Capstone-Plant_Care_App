package com.funetuneapps.bloombundy.classes

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import com.funetuneapps.bloombundy.R

object LoadingDialog {

    private var dialog: Dialog? = null

    fun show(context: Context,title:String?) {
            dialog = Dialog(context)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setCancelable(true)
            dialog?.setContentView(R.layout.loading_ad)
            val textView = dialog?.findViewById<TextView>(R.id.progress_text)
            if (title!=null) {
                textView?.text = title
            }
            dialog?.show()

    }

    fun hide() {
        try {
            if ( dialog != null) {
                dialog?.dismiss()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}