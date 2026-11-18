package com.rafael.cancela

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** Invisível: liga o modo criança direto; para desligar, pede biometria. */
class ToggleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Prefs.kidMode(this)) {
            if (!Prefs.serviceEnabled(this)) {
                Toast.makeText(this, "Ative o serviço de acessibilidade no painel dos pais.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ParentActivity::class.java))
            } else {
                Prefs.setKidMode(this, true)
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
            finish()
            return
        }

        if (!Auth.available(this)) { finish(); return }
        Auth.prompt(this, "Desligar modo criança",
            onOk = {
                Prefs.setKidMode(this, false)
                Toast.makeText(this, "Modo criança desligado", Toast.LENGTH_SHORT).show()
                finish()
            },
            onCancel = { finish() })
    }
}
