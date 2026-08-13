package com.app.gastosimple.features.setup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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

    // Callbacks optimizados para evitar recomposición masiva
    val onBudgetChange = remember(viewModel) { { b: String -> viewModel.onBudgetChange(b) } }
    val onCycleChange = remember(viewModel) { { c: String -> viewModel.onCycleTypeChange(c) } }
    val onMultiUserChange = remember(viewModel) { { m: Boolean -> viewModel.onMultiUserChange(m) } }
    val onUpdateName = remember(viewModel) { { i: Int, n: String -> viewModel.updateUserName(i, n) } }
    val onUpdatePercentage = remember(viewModel) { { i: Int, p: Double -> viewModel.updatePercentage(i, p) } }
    val onAddUser = remember(viewModel) { { n: String -> viewModel.addUser(n) } }
    val onRemoveUser = remember(viewModel) { { i: Int -> viewModel.removeUser(i) } }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo Midnight estático
        SetupBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header con progreso
            SetupProgressHeader(
                currentStep = state.currentStep,
                onBack = { viewModel.previousStep() },
                showBack = state.currentStep > 0
            )

            // Contenido Animado con transiciones suaves
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        val duration = 500
                        val easing = FastOutSlowInEasing
                        if (targetState > initialState) {
                            (slideInHorizontally(animationSpec = tween(duration, easing = easing)) { it } + fadeIn(tween(duration))).togetherWith(
                                slideOutHorizontally(animationSpec = tween(duration, easing = easing)) { -it } + fadeOut(tween(duration)))
                        } else {
                            (slideInHorizontally(animationSpec = tween(duration, easing = easing)) { -it } + fadeIn(tween(duration))).togetherWith(
                                slideOutHorizontally(animationSpec = tween(duration, easing = easing)) { it } + fadeOut(tween(duration)))
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
                            icon = Icons.Default.Star
                        )
                        3 -> BudgetStep(
                            budget = state.budget,
                            cycleType = state.cycleType,
                            onBudgetChange = onBudgetChange,
                            onCycleChange = onCycleChange
                        )
                        4 -> UsersStep(
                            isMultiUser = state.isMultiUser,
                            users = state.users,
                            onMultiUserChange = onMultiUserChange,
                            onUpdateName = onUpdateName,
                            onUpdatePercentage = onUpdatePercentage,
                            onAddUser = onAddUser,
                            onRemoveUser = onRemoveUser
                        )
                    }
                }
            }

            // Footer con botones elegantes
            SetupFooter(
                currentStep = state.currentStep,
                errorResId = state.errorResId,
                onNext = {
                    if (state.currentStep < 4) viewModel.nextStep()
                    else viewModel.finishSetup()
                }
            )
        }
    }
}

@Composable
fun SetupBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MidnightBlue, DeepMidnight)
                )
            )
    )
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = CyanBlue)
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(5) { index ->
                val color by animateColorAsState(
                    targetValue = if (index == currentStep) CyanBlue else CoolBlue.copy(alpha = 0.2f),
                    animationSpec = tween(300),
                    label = "DotColor"
                )
                val size by animateDpAsState(
                    targetValue = if (index == currentStep) 10.dp else 7.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "DotSize"
                )
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(color)
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
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            color = CoolBlue.copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanBlue.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxSize(),
                tint = CyanBlue
            )
        }
        Spacer(Modifier.height(40.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PureWhite,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = LightCoolBlue,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            letterSpacing = 0.5.sp
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
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Text(
            text = stringResource(R.string.setup_budget_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = CyanBlue
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                OutlinedTextField(
                    value = budget,
                    onValueChange = onBudgetChange,
                    label = { Text(stringResource(R.string.budget_label), color = LightCoolBlue.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("$ ", color = CyanBlue, fontWeight = FontWeight.Bold) },
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = OffWhite,
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = CoolBlue.copy(alpha = 0.4f),
                        cursorColor = CyanBlue
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )

                Text(
                    text = stringResource(R.string.cycle_type_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = LightCoolBlue,
                    modifier = Modifier.padding(start = 4.dp)
                )
                
                AnimatedSegmentedPicker(
                    options = listOf("MENSUAL" to stringResource(R.string.mensual), "QUINCENAL" to stringResource(R.string.quincenal)),
                    selectedOption = cycleType,
                    onOptionSelected = onCycleChange
                )
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.users_setup_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CyanBlue
            )
        }

        item {
            AnimatedSegmentedPicker(
                options = listOf(false to stringResource(R.string.single_user), true to stringResource(R.string.multi_user)),
                selectedOption = isMultiUser,
                onOptionSelected = onMultiUserChange
            )
        }

        if (!isMultiUser) {
            item {
                OutlinedTextField(
                    value = users[0].name,
                    onValueChange = { onUpdateName(0, it) },
                    label = { Text(stringResource(R.string.user_name_label), color = LightCoolBlue.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = OffWhite,
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = CoolBlue.copy(alpha = 0.4f),
                        cursorColor = CyanBlue
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            itemsIndexed(users) { index, user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = user.name,
                            onValueChange = { onUpdateName(index, it) },
                            label = { Text(stringResource(R.string.user_name_hint), color = LightCoolBlue.copy(alpha = 0.6f)) },
                            modifier = Modifier.weight(1.8f),
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = OffWhite,
                                focusedBorderColor = CyanBlue,
                                unfocusedBorderColor = CoolBlue.copy(alpha = 0.3f)
                            )
                        )
                        OutlinedTextField(
                            value = if (user.contributionPercentage == 0.0) "" else user.contributionPercentage.toString(),
                            onValueChange = { val p = it.toDoubleOrNull() ?: 0.0; onUpdatePercentage(index, p) },
                            label = { Text("%", color = LightCoolBlue.copy(alpha = 0.6f)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = OffWhite,
                                focusedBorderColor = CyanBlue,
                                unfocusedBorderColor = CoolBlue.copy(alpha = 0.3f)
                            )
                        )
                        if (users.size > 1) {
                            IconButton(onClick = { onRemoveUser(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_user), tint = ErrorRed.copy(alpha = 0.8f))
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
                        label = { Text(stringResource(R.string.add_user), color = LightCoolBlue.copy(alpha = 0.6f)) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = OffWhite,
                            focusedBorderColor = CyanBlue,
                            unfocusedBorderColor = CoolBlue.copy(alpha = 0.3f)
                        )
                    )
                    FilledIconButton(
                        onClick = {
                            if (newUserName.isNotBlank()) {
                                onAddUser(newUserName)
                                newUserName = ""
                            }
                        },
                        modifier = Modifier.size(54.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = CyanBlue, contentColor = MidnightBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun <T> AnimatedSegmentedPicker(
    options: List<Pair<T, String>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    val selectedIndex = options.indexOfFirst { it.first == selectedOption }
    var containerWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .onGloballyPositioned { coordinates ->
                containerWidth = with(density) { coordinates.size.width.toDp() }
            }
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(androidx.compose.foundation.BorderStroke(1.dp, CoolBlue.copy(alpha = 0.2f)), CircleShape)
    ) {
        if (containerWidth > 0.dp) {
            val pillWidth = containerWidth / options.size
            val offset by animateDpAsState(
                targetValue = pillWidth * selectedIndex,
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
                label = "PillOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = offset)
                    .width(pillWidth)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(listOf(CyanBlue, CoolBlue))
                    )
            )
        }

        Row(modifier = Modifier.fillMaxSize()) {
            options.forEach { (value, label) ->
                val isSelected = value == selectedOption
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MidnightBlue else LightCoolBlue,
                    label = "TextColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onOptionSelected(value) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun SetupFooter(
    currentStep: Int,
    errorResId: Int?,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        if (errorResId != null) {
            Surface(
                color = ErrorRed.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth()
            ) {
                Text(
                    text = stringResource(errorResId),
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue, contentColor = MidnightBlue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = if (currentStep < 4) stringResource(R.string.continue_label) else stringResource(R.string.finish),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.width(12.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}
