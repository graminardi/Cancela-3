package com.rafael.cancela

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/** Widget da tela inicial: 1 toque liga o modo criança; desligar exige biometria. */
class ModeWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { manager.updateAppWidget(it, build(context)) }
    }

    companion object {
        fun build(c: Context): RemoteViews {
            val on = Prefs.kidMode(c)
            val v = RemoteViews(c.packageName, R.layout.widget_mode)
            v.setTextViewText(R.id.txt, if (on) "🔒 Modo criança: LIGADO" else "▶ Ligar modo criança")
            v.setInt(R.id.root, "setBackgroundResource", if (on) R.drawable.bg_on else R.drawable.bg_off)
            val pi = PendingIntent.getActivity(
                c, 0, Intent(c, ToggleActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            v.setOnClickPendingIntent(R.id.root, pi)
            return v
        }

        fun refresh(c: Context) {
            val m = AppWidgetManager.getInstance(c)
            val ids = m.getAppWidgetIds(ComponentName(c, ModeWidget::class.java))
            ids.forEach { m.updateAppWidget(it, build(c)) }
        }
    }
}
