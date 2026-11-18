package com.rafael.cancela

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

/** No modo criança, qualquer app fora da lista é jogado de volta para a tela do Cancela. */
class LockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (!Prefs.kidMode(this)) return
        val pkg = event.packageName?.toString() ?: return

        if (pkg == packageName || pkg == "com.android.systemui" || pkg == "android" ||
            pkg.contains("inputmethod", true) || pkg.contains("keyboard", true)) return
        if (pkg in Prefs.allowed(this)) return

        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            )
        )
    }

    override fun onInterrupt() {}
}
