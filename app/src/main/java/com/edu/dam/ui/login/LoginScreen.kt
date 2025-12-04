package com.edu.dam.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.edu.dam.data.AppState
import com.edu.dam.data.prefs.UserPrefsRepository
import com.edu.dam.ui.common.UserNameSupportingText
import com.edu.dam.ui.common.validateUserName
import com.edu.dam.navigation.Home
import com.edu.dam.navigation.Login
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(nav: NavController, state: AppState, prefs: UserPrefsRepository) {
    var nick by rememberSaveable { mutableStateOf("") }
    val validation = remember(nick) { validateUserName(nick) }
    val nickOk = validation.isValid

    val savedName by prefs.userNameFlow.collectAsState(initial = "")
    LaunchedEffect(savedName) {
        if (nick.isEmpty() && savedName.isNotEmpty()) nick = savedName
    }

    data class Captcha(val a: Int, val b: Int, val op: Char){
        val result: Int = if(op == '+') a + b else a - b
        override fun toString() = "$a $op $b ="
    }

    fun newCaptcha(): Captcha{
        val plus = listOf(true, false).random()
        return if (plus){
            Captcha((1..9).random(), (1..9).random(), '+')
        } else {
            val x = (1..9).random()
            val y = (1..9).random()
            Captcha(x, y, '-')
        }
    }

    var cap by remember {mutableStateOf(newCaptcha())}
    var answer by rememberSaveable { mutableStateOf("") }
    val capOk = answer.toIntOrNull() == cap.result

    val canEnter = nickOk && capOk
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {CenterAlignedTopAppBar(title = {Text("Iniciar Sesion")})}
    ){  innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text(
                "Navegacion + Estado",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = nick,
                onValueChange = {nick = it},
                label = {Text("Inserte su Nickname")},
                singleLine = true,
                isError = nick.isNotEmpty() && (!validation.lengthAllowed || !validation.charsetAllowed),
                supportingText = {UserNameSupportingText(validation)},
                trailingIcon = {
                    if (nick.isNotEmpty()){
                        IconButton(onClick = {nick = ""}) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar Nick")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Demuestra que eres humano")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(cap.toString(), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    placeholder = { Text("resultado") },
                    singleLine = true,
                    isError = answer.isNotEmpty() && !capOk,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (canEnter) {
                                state.userName.value = validation.trimmed
                                scope.launch { prefs.setUserName(validation.trimmed) }
                                focus.clearFocus()
                                nav.navigate(Home) {
                                    popUpTo(Login) { inclusive = true }
                                }
                            }
                        }
                    ),
                    modifier = Modifier.width(140.dp)
                )
                IconButton(onClick = {
                    cap = newCaptcha()
                    answer = ""
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Nuevo ejercicio")
                }
            }

            // Entrar
            Button(
                enabled = canEnter,
                onClick = {
                    state.userName.value = validation.trimmed
                    scope.launch { prefs.setUserName(validation.trimmed) }
                    focus.clearFocus()
                    nav.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar") }

            // Help de error
            if (!canEnter && (answer.isNotBlank() || nick.isNotBlank())) {
                val msg = when {
                    !nickOk -> "Revisa tu nick (longitud/caracteres)."
                    !capOk -> "Resultado incorrecto."
                    else -> ""
                }
                if (msg.isNotEmpty()) {
                    Text(
                        "$msg",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}