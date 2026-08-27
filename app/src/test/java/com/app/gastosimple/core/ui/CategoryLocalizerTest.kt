package com.app.gastosimple.core.ui

import com.app.gastosimple.R
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryLocalizerTest {

    @Test
    fun `getCategoryStringRes maps standard Spanish category names correctly`() {
        assertEquals(R.string.cat_rent, CategoryLocalizer.getCategoryStringRes("Alquiler"))
        assertEquals(R.string.cat_food, CategoryLocalizer.getCategoryStringRes("Alimentación"))
        assertEquals(R.string.cat_food, CategoryLocalizer.getCategoryStringRes("Alimentacion"))
        assertEquals(R.string.cat_services, CategoryLocalizer.getCategoryStringRes("Servicios"))
        assertEquals(R.string.cat_subscriptions, CategoryLocalizer.getCategoryStringRes("Suscripciones"))
        assertEquals(R.string.cat_other, CategoryLocalizer.getCategoryStringRes("Otros"))
    }

    @Test
    fun `getCategoryStringRes maps English category names correctly`() {
        assertEquals(R.string.cat_rent, CategoryLocalizer.getCategoryStringRes("Rent"))
        assertEquals(R.string.cat_food, CategoryLocalizer.getCategoryStringRes("Food"))
        assertEquals(R.string.cat_services, CategoryLocalizer.getCategoryStringRes("Services"))
        assertEquals(R.string.cat_subscriptions, CategoryLocalizer.getCategoryStringRes("Subscriptions"))
        assertEquals(R.string.cat_other, CategoryLocalizer.getCategoryStringRes("Other"))
    }

    @Test
    fun `getCategoryStringRes falls back to cat_other on null or unknown categories`() {
        assertEquals(R.string.cat_other, CategoryLocalizer.getCategoryStringRes(null))
        assertEquals(R.string.cat_other, CategoryLocalizer.getCategoryStringRes("Categoría Inexistente"))
        assertEquals(R.string.cat_other, CategoryLocalizer.getCategoryStringRes(""))
    }

    @Test
    fun `toCategoryStringRes extension function matches getCategoryStringRes`() {
        assertEquals(R.string.cat_food, "Alimentación".toCategoryStringRes())
        assertEquals(R.string.cat_rent, "Alquiler".toCategoryStringRes())
        val nullCategory: String? = null
        assertEquals(R.string.cat_other, nullCategory.toCategoryStringRes())
    }
}
