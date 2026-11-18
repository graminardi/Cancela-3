package com.rafael.cancela

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

object Auth {
    private const val AUTH = BIOMETRIC_STRONG or BIOMETRIC_WEAK

    fun available(a: AppCompatActivity) =
        BiometricManager.from(a).canAuthenticate(AUTH) == BiometricManager.BIOMETRIC_SUCCESS

    /** Somente biometria (sem PIN/senha como alternativa). */
    fun prompt(a: AppCompatActivity, title: String, onOk: () -> Unit, onCancel: () -> Unit) {
        val cb = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(r: BiometricPrompt.AuthenticationResult) = onOk()
            override fun onAuthenticationError(code: Int, msg: CharSequence) = onCancel()
        }
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setAllowedAuthenticators(AUTH)
            .setNegativeButtonText("Cancelar")
            .build()
        BiometricPrompt(a, ContextCompat.getMainExecutor(a), cb).authenticate(info)
    }
}
