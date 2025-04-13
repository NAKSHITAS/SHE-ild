package com.example.she_ild.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EmergencyContactScreen(
    viewModel: SHEildViewModel = viewModel(),
    onContinueClicked: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var contactName by remember { mutableStateOf(uiState.emergencyContactName) }
    var contactNumber by remember { mutableStateOf(uiState.emergencyContactNumber) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Emergency Contact Details",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = contactName,
            onValueChange = {
                contactName = it
                viewModel.updateEmergencyContactName(it)
            },
            label = { Text("Contact Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contactNumber,
            onValueChange = {
                contactNumber = it
                viewModel.updateEmergencyContactNumber(it)
            },
            label = { Text("Contact Number") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinueClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = contactName.isNotBlank() && contactNumber.length >= 10
        ) {
            Text("Continue")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EmergencyContactScreenPreview() {
    EmergencyContactScreen()
}
