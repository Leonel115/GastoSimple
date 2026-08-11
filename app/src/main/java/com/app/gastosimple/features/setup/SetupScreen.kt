package com.app.gastosimple.features.setup

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gastosimple.R
import com.app.gastosimple.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(viewModel: SetupViewModel, onFinished: () -> Unit) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LightCoolBlue, Color.White)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header con progreso
            SetupProgressHeader(
                currentStep = state.currentStep,
                onBack = { viewModel.previousStep() },
                showBack = state.currentStep > 0
            )

            // Contenido Animado
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn()).togetherWith(
                                slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut())
                        } else {
                            (slideInHorizontally(animationSpec = tween(300)) { -it } + fadeIn()).togetherWith(
                                slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut())
                        }
                    },
                    label = "SetupStepAnimation"
                ) { step ->
                    when (step) {
                        0 -> OnboardingPage(
                            title = stringResource(R.string.welcome_title),
                            description = stringResource(R.string.welcome_desc),
                            icon = Icons.Default.Favorite
                        )
                        1 -> OnboardingPage(
                            title = stringResource(R.string.explain_title),
                            description = stringResource(R.string.explain_desc),
                            icon = Icons.Default.Info
                        )
                        2 -> OnboardingPage(
                            title = stringResource(R.string.saving_title),
                            description = stringResource(R.string.saving_desc),
                            icon = Icons.Default.Star // Changed to Star for safety
                        )
                        3 -> BudgetStep(
                            budget = state.budget,
                            cycleType = state.cycleType,
                            onBudgetChange = viewModel::onBudgetChange,
                            onCycleChange = viewModel::onCycleTypeChange
                        )
                        4 -> UsersStep(
                            isMultiUser = state.isMultiUser,
                            users = state.users,
                            onMultiUserChange = viewModel::onMultiUserChange,
                            onUpdateName = viewModel::updateUserName,
                            onUpdatePercentage = viewModel::updatePercentage,
                            onAddUser = viewModel::addUser,
                            onRemoveUser = viewModel::removeUser
                        )
                    }
                }
            }

            // Footer con botones
            SetupFooter(
                currentStep = state.currentStep,
                error = state.error,
                onNext = {
                    if (state.currentStep < 4) viewModel.nextStep()
                    else viewModel.finishSetup()
                }
            )
        }
    }
}

@Composable
fun SetupProgressHeader(
    currentStep: Int,
    onBack: () -> Unit,
    showBack: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (showBack) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = CoolBlue)
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentStep) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (index == currentStep) CoolBlue else CoolBlue.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(Modifier.size(48.dp))
    }
}

@Composable
fun OnboardingPage(
    title: String,
    description: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = SoftCyan
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                tint = CoolBlue
            )
        }
        Spacer(Modifier.height(32.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CoolBlue,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MediumGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetStep(
    budget: String,
    cycleType: String,
    onBudgetChange: (String) -> Unit,
    onCycleChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Define tu Presupuesto",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = CoolBlue
        )
        
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = budget,
                    onValueChange = onBudgetChange,
                    label = { Text(stringResource(R.string.budget_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("$", color = CoolBlue) },
                    shape = MaterialTheme.shapes.medium
                )

                Text(stringResource(R.string.cycle_type_label), style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = cycleType == "MENSUAL",
                        onClick = { onCycleChange("MENSUAL") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(stringResource(R.string.mensual))
                    }
                    SegmentedButton(
                        selected = cycleType == "QUINCENAL",
                        onClick = { onCycleChange("QUINCENAL") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(stringResource(R.string.quincenal))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersStep(
    isMultiUser: Boolean,
    users: List<com.app.gastosimple.core.data.local.UserEntity>,
    onMultiUserChange: (Boolean) -> Unit,
    onUpdateName: (Int, String) -> Unit,
    onUpdatePercentage: (Int, Double) -> Unit,
    onAddUser: (String) -> Unit,
    onRemoveUser: (Int) -> Unit
) {
    var newUserName by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Participantes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CoolBlue
            )
        }

        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = !isMultiUser,
                    onClick = { onMultiUserChange(false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text(stringResource(R.string.single_user))
                }
                SegmentedButton(
                    selected = isMultiUser,
                    onClick = { onMultiUserChange(true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text(stringResource(R.string.multi_user))
                }
            }
        }

        if (!isMultiUser) {
            item {
                OutlinedTextField(
                    value = users[0].name,
                    onValueChange = { onUpdateName(0, it) },
                    label = { Text("Tu nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }
        } else {
            itemsIndexed(users) { index, user ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = user.name,
                            onValueChange = { onUpdateName(index, it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.weight(1.5f),
                            shape = MaterialTheme.shapes.small
                        )
                        OutlinedTextField(
                            value = if (user.contributionPercentage == 0.0) "" else user.contributionPercentage.toString(),
                            onValueChange = { val p = it.toDoubleOrNull() ?: 0.0; onUpdatePercentage(index, p) },
                            label = { Text("%") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = MaterialTheme.shapes.small
                        )
                        if (users.size > 1) {
                            IconButton(onClick = { onRemoveUser(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = { Text("Nuevo usuario") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    )
                    FilledIconButton(
                        onClick = {
                            if (newUserName.isNotBlank()) {
                                onAddUser(newUserName)
                                newUserName = ""
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = CoolBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun SetupFooter(
    currentStep: Int,
    error: String?,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        if (error != null) {
            Text(
                text = error,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = CoolBlue)
        ) {
            Text(
                text = if (currentStep < 4) "Siguiente" else stringResource(R.string.finish),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}
