package com.app.gastosimple.core.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.util.Locale

class CurrencyUtilsTest {

    @Test
    fun `format with generic Spanish locale displays dollar sign and never generic currency sign`() {
        val genericEs = Locale("es")
        val formatted = CurrencyUtils.format(500.0, genericEs)
        
        assertTrue("Expected '$' in formatted output, got: $formatted", formatted.contains("$"))
        assertFalse("Did not expect generic '¤' in output, got: $formatted", formatted.contains("¤"))
    }

    @Test
    fun `format with generic English locale displays dollar sign and never generic currency sign`() {
        val genericEn = Locale("en")
        val formatted = CurrencyUtils.format(863.69, genericEn)

        assertTrue("Expected '$' in formatted output, got: $formatted", formatted.contains("$"))
        assertFalse("Did not expect generic '¤' in output, got: $formatted", formatted.contains("¤"))
    }

    @Test
    fun `format with BigDecimal works correctly and includes dollar sign`() {
        val amount = BigDecimal("1500.50")
        val formatted = amount.formatAsCurrency(Locale("es"))

        assertTrue("Expected '$' in formatted output, got: $formatted", formatted.contains("$"))
        assertFalse("Did not expect generic '¤' in output, got: $formatted", formatted.contains("¤"))
    }

    @Test
    fun `format with Double extension works correctly and includes dollar sign`() {
        val amount = 120.0
        val formatted = amount.formatAsCurrency(Locale.US)

        assertTrue("Expected '$' in formatted output, got: $formatted", formatted.contains("$"))
        assertFalse("Did not expect generic '¤' in output, got: $formatted", formatted.contains("¤"))
    }

    @Test
    fun `format with String extension handles null and blank safely`() {
        val nullStr: String? = null
        val formattedNull = nullStr.formatAsCurrency(Locale.US)
        assertTrue(formattedNull.contains("$"))

        val validStr = "450.75"
        val formattedValid = validStr.formatAsCurrency(Locale.US)
        assertTrue(formattedValid.contains("$"))
    }
}
