package com.funetuneapps.bloombundy.classes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.funetuneapps.bloombundy.BuildConfig
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.notifications.SenderNotification
import java.text.SimpleDateFormat
import java.util.*

object Constants {


    var mainFragsSwitch: ((type: Int) -> Unit) = {}


    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun Fragment.mToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("SimpleDateFormat")
    fun getTimeFromStamp(stamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = stamp
        val format = SimpleDateFormat("hh:mm a")
        return format.format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateFromStamp(stamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = stamp
        val format = SimpleDateFormat("dd-MMMM-yyyy")
        return format.format(calendar.time)
    }

    private const val SECOND = 1
    private const val MINUTE = 60 * SECOND
    private const val HOUR = 60 * MINUTE
    private const val DAY = 24 * HOUR


    fun waterTimeDifference(days:Int,time: Long): Long {
        // convert back to second
        val diff =
            ((time + (days * AlarmManager.INTERVAL_DAY)) - System.currentTimeMillis()) / 1000

        return diff / 3600

    }

    fun sunlightTimeDifference(days:Int,time: Long): Long {
        // convert back to second
        val diff = ((time + (days * AlarmManager.INTERVAL_DAY)) - System.currentTimeMillis()) / 1000
        return diff / 3600

    }

    fun Activity.sendNotification(token: String, title: String, msg: String) {
        SenderNotification.sendNotifications(token, title, msg, this)
    }

    fun View.hideKeyboard() {
        try {
            clearFocus()
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(this.windowToken, 0)
        } catch (_: Exception) {
        } catch (_: java.lang.Exception) {
        }
    }

    fun View.showKeyboard() {

        requestFocus()
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)


    }

    fun Fragment.myToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    fun generateKeywords(name: String): List<String> {
        val keywords = mutableListOf<String>()
        try {
            keywords.add(name.substring(0, if (name.length > 3) 3 else name.length).lowercase())
            keywords.add(name.substring(0, if (name.length > 4) 4 else name.length).lowercase())
            keywords.add(name.substring(0, if (name.length > 5) 5 else name.length).lowercase())
            keywords.add(name.substring(0, if (name.length > 7) 7 else name.length).lowercase())
            keywords.add(name.takeLast(5).lowercase())
            keywords.add(name.takeLast(10).lowercase())
            keywords.add(name.takeLast(7).lowercase())
            keywords.add(name.lowercase())
        } catch (_: Exception) {
        } catch (_: java.lang.Exception) {
        }
//        for (i in name.indices) {
//            for (j in (i + 1)..name.length) {
//                keywords.add(name.slice(i until j).lowercase())
//            }
//        }
        return keywords
    }

    fun Context.shareApp() {
        try {

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            var shareMessage = buildString {
                append("\n")
                append("Check this awesome photo-shoot services application")
                append("\n\n")
                append("Application link:")
                append("\n")
            }
            shareMessage =
                "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    fun Context.launchMarket() {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            this.startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.unable_to_find_market_app), Toast.LENGTH_LONG)
                .show()
        }
    }

}