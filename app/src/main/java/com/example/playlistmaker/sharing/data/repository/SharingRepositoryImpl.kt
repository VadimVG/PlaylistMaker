package com.example.playlistmaker.sharing.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingRepositoryImpl(private val context: Context): SharingRepository {
    override fun getShareAppLink(): String {
        return  context.getString(R.string.share_message)
    }
    override fun getTermsLink(): String {
        return context.getString(R.string.docs_message)
    }
    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.user_email),
            subject = context.getString(R.string.support_theme_message),
            body = context.getString(R.string.support_text_message)
        )
    }

//    override fun shareApp() {
//        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "text/plain"
//            putExtra(Intent.EXTRA_TEXT, getShareAppLink())
//        }
//        context.startActivity(Intent.createChooser(shareIntent, getShareAppLink()))
//    }
//    @SuppressLint("QueryPermissionsNeeded")
//    override fun openTerms() {
//        val linkIntent = Intent(Intent.ACTION_VIEW).apply {
//            data = Uri.parse(getTermsLink())
//        }
//        if (linkIntent.resolveActivity(context.packageManager) != null) {
//            context.startActivity(linkIntent)
//        }
//    }
//    @SuppressLint("QueryPermissionsNeeded")
//    override fun openSupport() {
//        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
//            val emailData: EmailData = getSupportEmailData()
//            data = Uri.parse("mailto:")
//            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
//            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
//            putExtra(Intent.EXTRA_TEXT, emailData.body)
//        }
//        if (emailIntent.resolveActivity(context.packageManager) != null) {
//            context.startActivity(emailIntent)
//        }
//    }
}