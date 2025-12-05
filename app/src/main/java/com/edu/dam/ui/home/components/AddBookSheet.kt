package com.edu.dam.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookSheet(
    sheetState: SheetState,
    title: String,
    author: String,
    numPage: String,
    body: String,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onNumPageChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: (isFavorite: Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {

    val focus = LocalFocusManager.current
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        val titleLength = title.trim().length
        val titleWithinLimit = titleLength <= 80

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nuevo Libro",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("$titleLength / 80") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                value = author,
                onValueChange = onAuthorChange,
                label = { Text("Autor *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = numPage,
                onValueChange = onNumPageChange,
                label = { Text("Número de páginas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = body,
                onValueChange = onBodyChange,
                label = { Text("Sinopsis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                minLines = 4,
                maxLines = 8
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = { onSave(false) },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank() && author.isNotBlank()
                ) {
                    Text("Guardar")
                }

                IconButton(
                    onClick = { onSave(true) },
                    enabled = title.isNotBlank() && author.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Guardar como favorito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}