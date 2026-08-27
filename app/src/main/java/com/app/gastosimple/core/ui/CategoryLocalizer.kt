package com.app.gastosimple.core.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.app.gastosimple.R
import java.util.Locale

/**
 * Utilidad para la localización dinámica de categorías de gastos (HU-11).
 * Convierte el identificador o nombre persistido de una categoría a su recurso de cadena internacionalizado.
 */
object CategoryLocalizer {

    /**
     * Retorna el identificador del recurso de cadena ([StringRes]) correspondiente a la categoría.
     *
     * @param categoryName Nombre de la categoría persistido en base de datos.
     * @return Identificador [R.string] correspondiente con fallback seguro a [R.string.cat_other].
     */
    @StringRes
    fun getCategoryStringRes(categoryName: String?): Int {
        if (categoryName.isNullOrBlank()) return R.string.cat_other
        return when (categoryName.trim().lowercase(Locale.ROOT)) {
            "alquiler", "rent" -> R.string.cat_rent
            "alimentación", "alimentacion", "food" -> R.string.cat_food
            "servicios", "services" -> R.string.cat_services
            "suscripciones", "subscriptions" -> R.string.cat_subscriptions
            "otros", "other" -> R.string.cat_other
            else -> R.string.cat_other
        }
    }

    /**
     * Retorna la etiqueta traducida de la categoría según el idioma activo en Compose.
     *
     * @param categoryName Nombre de la categoría.
     * @return Cadena traducida.
     */
    @Composable
    fun getCategoryLabel(categoryName: String?): String {
        return stringResource(getCategoryStringRes(categoryName))
    }
}

/**
 * Función de extensión para obtener el recurso de cadena de una categoría.
 */
@StringRes
fun String?.toCategoryStringRes(): Int = CategoryLocalizer.getCategoryStringRes(this)
