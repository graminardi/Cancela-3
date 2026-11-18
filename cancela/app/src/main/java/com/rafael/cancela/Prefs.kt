package com.rafael.cancela

import android.content.Context
import android.provider.Settings
import android.text.TextUtils

object Prefs {
    private fun sp(c: Context) = c.getSharedPreferences("cancela", Context.MODE_PRIVATE)

    fun allowed(c: Context): Set<String> = sp(c).getStringSet("allowed", emptySet()) ?: emptySet()

    fun setAllowed(c: Context, pkg: String, on: Boolean) {
        val s = allowed(c).toMutableSet()
        if (on) s.add(pkg) else s.remove(pkg)
        sp(c).edit().putStringSet("allowed", s).apply()
    }

    fun kidMode(c: Context) = sp(c).getBoolean("kid", false)
    fun setKidMode(c: Context, on: Boolean) {
        sp(c).edit().putBoolean("kid", on).apply()
        ModeWidget.refresh(c)
        try {
            android.service.quicksettings.TileService.requestListeningState(
                c, android.content.ComponentName(c, ModeTile::class.java))
        } catch (e: Exception) { /* ignora */ }
    }

    fun serviceEnabled(c: Context): Boolean {
        val enabled = Settings.Secure.getString(c.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        val me = "${c.packageName}/${LockService::class.java.name}"
        val sp = TextUtils.SimpleStringSplitter(':').apply { setString(enabled) }
        while (sp.hasNext()) if (sp.next().equals(me, true)) return true
        return false
    }
}
