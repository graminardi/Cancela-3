package com.rafael.cancela

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/** Atalho nas configurações rápidas: toque liga o modo criança; desligar exige biometria. */
class ModeTile : TileService() {

    override fun onStartListening() = refresh()

    override fun onClick() {
        val i = Intent(this, ToggleActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Com o celular bloqueado, exige desbloquear antes
        unlockAndRun {
            if (Build.VERSION.SDK_INT >= 34) {
                startActivityAndCollapse(
                    PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                )
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(i)
            }
        }
    }

    private fun refresh() {
        val on = Prefs.kidMode(this)
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = "Modo criança"
            if (Build.VERSION.SDK_INT >= 29) subtitle = if (on) "Ligado" else "Desligado"
            updateTile()
        }
    }
}
