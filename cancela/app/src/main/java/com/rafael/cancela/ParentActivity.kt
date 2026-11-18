package com.rafael.cancela

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/** Painel dos pais: só abre com biometria. */
class ParentActivity : AppCompatActivity() {

    private data class Item(val label: String, val pkg: String)
    private lateinit var content: LinearLayout
    private lateinit var kidSwitch: Switch
    private lateinit var accBtn: Button
    private lateinit var list: ListView
    private var items = listOf<Item>()
    private var authed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Painel dos pais"

        kidSwitch = Switch(this).apply {
            text = "Modo criança (só apps liberados)"
            setPadding(24, 24, 24, 24)
            setOnCheckedChangeListener { _, on ->
                if (on && !Prefs.serviceEnabled(this@ParentActivity)) {
                    isChecked = false
                    Toast.makeText(context, "Ative primeiro o serviço de acessibilidade.", Toast.LENGTH_LONG).show()
                } else Prefs.setKidMode(this@ParentActivity, on)
            }
        }
        accBtn = Button(this).apply {
            text = "1) Ativar serviço de acessibilidade do Cancela"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val homeBtn = Button(this).apply {
            text = "2) Definir Cancela como tela inicial"
            setOnClickListener { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
        }
        val tileBtn = Button(this).apply {
            text = "3) Adicionar atalho nas configurações rápidas"
            setOnClickListener {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    getSystemService(android.app.StatusBarManager::class.java).requestAddTileService(
                        android.content.ComponentName(this@ParentActivity, ModeTile::class.java),
                        "Modo criança",
                        android.graphics.drawable.Icon.createWithResource(this@ParentActivity, R.drawable.ic_tile),
                        this@ParentActivity.mainExecutor
                    ) { _ -> }
                } else {
                    Toast.makeText(context, "Puxe a barra de cima, toque no lápis e arraste 'Modo criança'.", Toast.LENGTH_LONG).show()
                }
            }
        }
        val hint = TextView(this).apply {
            text = "4) Marque os apps que a criança pode abrir:"
            setPadding(24, 16, 24, 8)
        }
        list = ListView(this).apply { choiceMode = ListView.CHOICE_MODE_MULTIPLE }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(kidSwitch); addView(accBtn); addView(homeBtn); addView(tileBtn); addView(hint)
            addView(list, LinearLayout.LayoutParams(-1, 0, 1f))
            visibility = View.INVISIBLE
        }
        setContentView(content)

        if (!Auth.available(this)) {
            Toast.makeText(this, "Cadastre uma biometria nas configurações do celular.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!authed) {
            Auth.prompt(this, "Painel dos pais", onOk = { authed = true; show() }, onCancel = { finish() })
        } else show()
    }

    override fun onStop() {
        super.onStop()
        authed = false
        content.visibility = View.INVISIBLE
    }

    private fun show() {
        kidSwitch.isChecked = Prefs.kidMode(this)
        accBtn.visibility = if (Prefs.serviceEnabled(this)) View.GONE else View.VISIBLE

        val pm = packageManager
        val q = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        items = pm.queryIntentActivities(q, 0)
            .map { Item(it.loadLabel(pm).toString(), it.activityInfo.packageName) }
            .filter { it.pkg != packageName }
            .distinctBy { it.pkg }
            .sortedBy { it.label.lowercase() }

        list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items.map { it.label })
        val allowed = Prefs.allowed(this)
        items.forEachIndexed { i, it -> list.setItemChecked(i, it.pkg in allowed) }
        list.setOnItemClickListener { _, _, pos, _ ->
            Prefs.setAllowed(this, items[pos].pkg, list.isItemChecked(pos))
        }
        content.visibility = View.VISIBLE
    }
}
