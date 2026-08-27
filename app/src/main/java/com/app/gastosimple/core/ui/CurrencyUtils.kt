package com.app.gastosimple.core.ui

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Utilidades centralizadas para el formateo de montos monetarios en USD ($)
 * respetando el Locale activo para separadores numéricos.
 */
object CurrencyUtils {

    private const val CURRENCY_CODE_USD = "USD"
    private const val DEFAULT_CURRENCY_SYMBOL = "$"

    /**
     * Obtiene una instancia configurada de [NumberFormat] para moneda USD con el símbolo '$'.
     *
     * @param locale Locale regional a aplicar (por defecto [Locale.getDefault]).
     * @return Formateador monetario configurado.
     */
    fun getCurrencyFormatter(locale: Locale = Locale.getDefault()): NumberFormat {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        try {
            formatter.currency = Currency.getInstance(CURRENCY_CODE_USD)
            if (formatter is DecimalFormat) {
                val symbols = formatter.decimalFormatSymbols
                symbols.currencySymbol = DEFAULT_CURRENCY_SYMBOL
                formatter.decimalFormatSymbols = symbols
            }
        } catch (_: Exception) {
            // Manejo defensivo en entornos sin soporte completo de Currency
        }
        return formatter
    }

    /**
     * Formatea un valor numérico [Double] como moneda USD.
     *
     * @param amount Monto a formatear.
     * @param locale Locale regional a aplicar.
     * @return Cadena formateada con el símbolo '$'.
     */
    fun format(amount: Double, locale: Locale = Locale.getDefault()): String {
        return try {
            getCurrencyFormatter(locale).format(amount)
        } catch (_: Exception) {
            String.format(locale, "$%.2f", amount)
        }
    }

    /**
     * Formatea un valor numérico [BigDecimal] como moneda USD.
     *
     * @param amount Monto a formatear.
     * @param locale Locale regional a aplicar.
     * @return Cadena formateada con el símbolo '$'.
     */
    fun format(amount: BigDecimal, locale: Locale = Locale.getDefault()): String {
        return try {
            getCurrencyFormatter(locale).format(amount)
        } catch (_: Exception) {
            String.format(locale, "$%.2f", amount.toDouble())
        }
    }

    /**
     * Formatea una cadena numérica como moneda USD.
     *
     * @param amountStr Cadena con el monto numérico.
     * @param locale Locale regional a aplicar.
     * @return Cadena formateada con el símbolo '$'.
     */
    fun format(amountStr: String?, locale: Locale = Locale.getDefault()): String {
        if (amountStr.isNullOrBlank()) return format(0.0, locale)
        val parsed = amountStr.toDoubleOrNull() ?: return "$$amountStr"
        return format(parsed, locale)
    }
}

/**
 * Función de extensión para formatear un [Double] como moneda USD.
 */
fun Double.formatAsCurrency(locale: Locale = Locale.getDefault()): String =
    CurrencyUtils.format(this, locale)

/**
 * Función de extensión para formatear un [BigDecimal] como moneda USD.
 */
fun BigDecimal.formatAsCurrency(locale: Locale = Locale.getDefault()): String =
    CurrencyUtils.format(this, locale)

/**
 * Función de extensión para formatear un [String] numérico como moneda USD.
 */
fun String?.formatAsCurrency(locale: Locale = Locale.getDefault()): String =
    CurrencyUtils.format(this, locale)
