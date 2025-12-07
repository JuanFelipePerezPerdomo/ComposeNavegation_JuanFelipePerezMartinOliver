package com.edu.dam.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edu.dam.data.AppState
import com.edu.dam.data.prefs.ThemeMode
import com.edu.dam.data.prefs.UserPrefsRepository
import com.edu.dam.navigation.Settings
import com.edu.dam.ui.common.UserNameSupportingText
import com.edu.dam.ui.common.validateUserName
import com.edu.dam.ui.components.AppBottomBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    nav: NavController,
    state: AppState,
    prefs: UserPrefsRepository
) {
    val currentName by state.userName.collectAsState()
    val savedName by prefs.userNameFlow.collectAsState(initial = "")
    val currentThemeMode by prefs.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)
    val scope = rememberCoroutineScope()
    var tempName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(savedName) { tempName = savedName }

    val validation = remember(tempName) { validateUserName(tempName) }
    val trimmed = validation.trimmed
    val lengthOk = validation.lengthAllowed
    val charsetOk = validation.charsetAllowed
    val hasChanges = trimmed != currentName
    val canSave = trimmed.isNotEmpty() && lengthOk && charsetOk && hasChanges

    val focus = LocalFocusManager.current

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = { AppBottomBar(nav = nav, current = Settings) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ═══════════════════════════════════════════════════════════════
            // PERFIL
            // ═══════════════════════════════════════════════════════════════
            Text("Perfil", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                isError = tempName.isNotEmpty() && (!lengthOk || !charsetOk),
                supportingText = { UserNameSupportingText(validation) },
                trailingIcon = {
                    if (tempName.isNotEmpty()) {
                        IconButton(onClick = { tempName = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (canSave) {
                            state.userName.value = trimmed
                            focus.clearFocus()
                            nav.popBackStack()
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Botones de perfil
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        state.userName.value = trimmed
                        scope.launch { prefs.setUserName(trimmed) }
                        focus.clearFocus()
                        nav.popBackStack()
                    },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar y volver") }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { tempName = currentName },
                        enabled = hasChanges,
                        modifier = Modifier.weight(1f)
                    ) { Text("Revertir") }

                    OutlinedButton(
                        onClick = { tempName = "" },
                        modifier = Modifier.weight(1f)
                    ) { Text("Limpiar") }
                }

                TextButton(
                    onClick = { focus.clearFocus(); nav.popBackStack() },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Cancelar") }
            }

            // ═══════════════════════════════════════════════════════════════
            // TEMA DE LA APLICACIÓN - 3 opciones: Claro, Oscuro, Sistema
            // ═══════════════════════════════════════════════════════════════
            SettingsSection(
                title = "Tema de la aplicación",
                subtitle = "Selecciona el modo de visualización"
            ) {
                ThemeModeSelector(
                    currentMode = currentThemeMode,
                    onModeChange = { mode ->
                        scope.launch { prefs.setThemeMode(mode) }
                    }
                )
            }

            // ═══════════════════════════════════════════════════════════════
            // MENSAJE DE BIENVENIDA
            // ═══════════════════════════════════════════════════════════════
            SettingsSection(
                title = "Mensaje de bienvenida",
                subtitle = "Controla si quieres volver a ver la introducción"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Column(Modifier.weight(1f)) {
                        Text(
                            "Vuelve a mostrar el diálogo en el próximo inicio.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(onClick = { scope.launch { prefs.setWelcomeShow(false) } }) {
                        Text("Reiniciar")
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SELECTOR DE TEMA - 3 botones segmentados
// ═══════════════════════════════════════════════════════════════════════════════

private data class ThemeModeOption(
    val mode: ThemeMode,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeSelector(
    currentMode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        ThemeModeOption(ThemeMode.LIGHT, "Claro", Icons.Filled.LightMode),
        ThemeModeOption(ThemeMode.DARK, "Oscuro", Icons.Filled.DarkMode),
        ThemeModeOption(ThemeMode.SYSTEM, "Sistema", Icons.Filled.PhoneAndroid)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = currentMode == option.mode,
                    onClick = { onModeChange(option.mode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    icon = {
                        SegmentedButtonDefaults.Icon(active = currentMode == option.mode) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    }
                ) {
                    Text(option.label)
                }
            }
        }

        // Texto descriptivo según el modo seleccionado
        val description = when (currentMode) {
            ThemeMode.LIGHT -> "La interfaz usará colores claros siempre"
            ThemeMode.DARK -> "La interfaz usará colores oscuros siempre"
            ThemeMode.SYSTEM -> "El tema seguirá la configuración de tu dispositivo"
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// COMPONENTE REUTILIZABLE - Sección de ajustes
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}