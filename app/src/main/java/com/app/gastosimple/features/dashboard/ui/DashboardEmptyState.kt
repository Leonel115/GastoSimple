package com.app.gastosimple.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.core.ui.theme.GastoSimpleTheme

/**
 * Constantes de diseño para el estado vacío del Dashboard.
 * Previene el uso de magic numbers según continuerules.md.
 */
object DashboardEmptyStateDimens {
    val ContainerPaddingVertical: Dp = 28.dp
    val ContainerPaddingHorizontal: Dp = 20.dp
    val IconContainerSize: Dp = 68.dp
    val IconSize: Dp = 32.dp
    val IconContainerBorderWidth: Dp = 1.dp
    val SpacingAfterIcon: Dp = 16.dp
    val SpacingAfterTitle: Dp = 8.dp
}

/**
 * Componente Composable que representa el estado vacío en el Dashboard (HU-07).
 * Se visualiza cuando no existen consumos registrados en el período temporal consultado.
 */
@Composable
fun DashboardEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = DashboardEmptyStateDimens.ContainerPaddingVertical,
                horizontal = DashboardEmptyStateDimens.ContainerPaddingHorizontal
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Contenedor circular con icono representativo
        Box(
            modifier = Modifier
                .size(DashboardEmptyStateDimens.IconContainerSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .border(
                    width = DashboardEmptyStateDimens.IconContainerBorderWidth,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f),
                modifier = Modifier.size(DashboardEmptyStateDimens.IconSize)
            )
        }

        Spacer(modifier = Modifier.height(DashboardEmptyStateDimens.SpacingAfterIcon))

        // Título del estado vacío
        Text(
            text = stringResource(R.string.dashboard_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(DashboardEmptyStateDimens.SpacingAfterTitle))

        // Subtítulo descriptivo
        Text(
            text = stringResource(R.string.dashboard_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// -------------------------------------------------------------------------
// Previews de Compose
// -------------------------------------------------------------------------

@Preview(name = "Dashboard Empty State - Dark", showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun DashboardEmptyStateDarkPreview() {
    GastoSimpleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardEmptyState()
        }
    }
}

@Preview(name = "Dashboard Empty State - Light", showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun DashboardEmptyStateLightPreview() {
    GastoSimpleTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardEmptyState()
        }
    }
}
