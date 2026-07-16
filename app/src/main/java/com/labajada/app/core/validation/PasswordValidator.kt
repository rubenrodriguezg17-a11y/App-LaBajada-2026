package com.labajada.app.core.validation

data class PasswordValidationResult(
    val hasMinLength: Boolean,
    val hasUppercase: Boolean
) {
    val isValid: Boolean
        get() = hasMinLength && hasUppercase
}

object PasswordValidator {

    private const val MIN_LENGTH = 8

    fun validate(password: String): PasswordValidationResult {
        return PasswordValidationResult(
            hasMinLength = password.length >= MIN_LENGTH,
            hasUppercase = password.any { it.isUpperCase() }
        )
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }
}

object PeruValidators {
    private val DNI_REGEX = Regex("^\\d{8}$")
    private val RUC_REGEX = Regex("^\\d{11}$")
    private val PHONE_REGEX = Regex("^9\\d{8}$")

    fun isValidDni(dni: String): Boolean = DNI_REGEX.matches(dni.trim())

    fun isValidRuc(ruc: String): Boolean = RUC_REGEX.matches(ruc.trim())

    fun isValidPhone(phone: String): Boolean = PHONE_REGEX.matches(phone.trim())

    fun isValidDocumento(documento: String): Boolean {
        val limpio = documento.trim()
        return when (limpio.length) {
            8 -> isValidDni(limpio)
            11 -> {
                val empiezaBien = limpio.startsWith("10") || limpio.startsWith("15") ||
                        limpio.startsWith("17") || limpio.startsWith("20")
                empiezaBien && isValidRuc(limpio)
            }
            else -> false
        }
    }
}