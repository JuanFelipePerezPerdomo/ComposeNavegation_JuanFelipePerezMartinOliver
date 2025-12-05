package com.edu.dam.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditNoteSheet(
    sheetState: SheetState,
    title: String,
    synopsis: String,
    numPage: Int,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onNumPageChange: (Int) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val focus = LocalFocusManager.current
    val titleLength = title.trim().length
    val titleWithinLimit = titleLength <= 80

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ){
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text("Editar Nota", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = title,
                onValueChange = { newValue ->
                    if (newValue.length <= 80) onTitleChange(newValue)
                },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("$titleLength / 80") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                value = if (numPage > 0) numPage.toString() else "",
                onValueChange = { newValue ->
                    val parsedValue = newValue.toIntOrNull()
                    if (parsedValue != null && parsedValue >= 0) {
                        onNumPageChange(parsedValue)
                    } else if (newValue.isEmpty()) {
                        onNumPageChange(0)
                    }
                },
                label = { Text("Número de páginas") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                value = synopsis,
                onValueChange = onBodyChange,
                label = { Text("Contenido (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* guardar con botón */ })
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancelar") }

                Button(
                    onClick = onSave,
                    enabled = title.isNotBlank() && titleWithinLimit,
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar cambios") }
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}