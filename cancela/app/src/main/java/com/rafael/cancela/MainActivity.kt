package com.rafael.cancela

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/** Tela da criança: grade grande só com os apps liberados. Também serve como tela inicial. */
class MainActivity : AppCompatActivity() {

    private data class Item(val label: String, val pkg: String, val icon: Drawable)
    private lateinit var grid: GridView
    private lateinit var titleView: TextView

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        titleView = TextView(this).apply { textSize = 20f; setPadding(dp(16), dp(16), dp(16), dp(16)) }
        val parent = Button(this).apply {
            text = "🔒 Pais"
            setOnClickListener { startActivity(Intent(this@MainActivity, ParentActivity::class.java)) }
        }
        val bar = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            addView(titleView, LinearLayout.LayoutParams(0, -2, 1f))
            addView(parent)
        }
        grid = GridView(this).apply {
            numColumns = 2
            verticalSpacing = dp(16)
            horizontalSpacing = dp(16)
            setPadding(dp(16), dp(8), dp(16), dp(16))
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(bar)
            addView(grid, LinearLayout.LayoutParams(-1, 0, 1f))
        })
    }

    override fun onResume() {
        super.onResume()
        val kid = Prefs.kidMode(this)
        val allowed = Prefs.allowed(this)
        titleView.text = if (kid) "Modo criança" else "Cancela (modo criança desligado)"

        val pm = packageManager
        val q = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val items = pm.queryIntentActivities(q, 0)
            .map { Item(it.loadLabel(pm).toString(), it.activityInfo.packageName, it.loadIcon(pm)) }
            .filter { it.pkg != packageName && (!kid || it.pkg in allowed) }
            .distinctBy { it.pkg }
            .sortedBy { it.label.lowercase() }

        grid.adapter = object : BaseAdapter() {
            override fun getCount() = items.size
            override fun getItem(i: Int): Any = items[i]
            override fun getItemId(i: Int) = i.toLong()
            override fun getView(i: Int, v: View?, p: ViewGroup?): View {
                val it = items[i]
                return LinearLayout(this@MainActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setPadding(dp(8), dp(16), dp(8), dp(16))
                    addView(ImageView(context).apply { setImageDrawable(it.icon) },
                        LinearLayout.LayoutParams(dp(96), dp(96)))
                    addView(TextView(context).apply { text = it.label; gravity = Gravity.CENTER; textSize = 16f })
                }
            }
        }
        grid.setOnItemClickListener { _, _, pos, _ ->
            packageManager.getLaunchIntentForPackage(items[pos].pkg)?.let { startActivity(it) }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { /* é a tela inicial: não sai */ }
}
