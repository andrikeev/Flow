package flow.data.impl.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import flow.data.api.service.StoreService
import flow.models.Store
import javax.inject.Inject

internal class StoreServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : StoreService {
    private val ratingStore: Store by lazy { Store(getResolvableLink()) }

    override fun getStore() = ratingStore

    @SuppressLint("QueryPermissionsNeeded")
    private fun getResolvableLink(): String {
        val packageName = context.packageName
        return if (isRuStoreInstalled()) {
            "https://apps.rustore.ru/app/$packageName"
        } else {
            val marketLink = "market://details?id=$packageName"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(marketLink))
            if (intent.resolveActivity(context.packageManager) != null) {
                marketLink
            } else {
                "http://play.google.com/store/apps/details?id=$packageName"
            }
        }
    }

    private fun isRuStoreInstalled() = try {
        @Suppress("DEPRECATION")
        context.packageManager.getApplicationInfo("ru.vk.store", 0).enabled
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
