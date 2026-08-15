package com.app.gastosimple.features.dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.gastosimple.R
import com.app.gastosimple.features.dashboard.DashboardViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla principal del Dashboard.
 * Integra el panel de progreso del presupuesto (HU-06).
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val budgetProgress by viewModel.budgetProgress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.nav_dashboard),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        BudgetProgressCard(state = budgetProgress)
    }
}
