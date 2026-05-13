package com.namma_shaale.inventory.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.namma_shaale.inventory.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.namma_shaale.inventory.presentation.auth.AuthViewModel
import com.namma_shaale.inventory.data.repository.AuthRepository
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val schoolName by viewModel.schoolName.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var newName by remember(schoolName) { mutableStateOf(schoolName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.school_information),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Name Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(stringResource(R.string.school_name), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(schoolName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = { 
                        newName = schoolName
                        showEditDialog = true 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Address Card
            val schoolAddress by viewModel.schoolAddress.collectAsState()
            var showAddressDialog by remember { mutableStateOf(false) }
            var newAddress by remember(schoolAddress) { mutableStateOf(schoolAddress) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(stringResource(R.string.school_address), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(schoolAddress, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = { 
                        newAddress = schoolAddress
                        showAddressDialog = true 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Code Card
            val schoolCode by viewModel.schoolCode.collectAsState()
            var showCodeDialog by remember { mutableStateOf(false) }
            var newCode by remember(schoolCode) { mutableStateOf(schoolCode) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Badge, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(stringResource(R.string.school_code), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(schoolCode, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = { 
                        newCode = schoolCode
                        showCodeDialog = true 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                stringResource(R.string.app_appearance),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val isDarkMode by viewModel.isDarkMode.collectAsState()
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = isDarkMode == null,
                        onClick = { viewModel.updateDarkMode(null) }
                    )
                    Text(stringResource(R.string.system_default), modifier = Modifier.padding(start = 8.dp))
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = isDarkMode == false,
                        onClick = { viewModel.updateDarkMode(false) }
                    )
                    Text(stringResource(R.string.light_mode), modifier = Modifier.padding(start = 8.dp))
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = isDarkMode == true,
                        onClick = { viewModel.updateDarkMode(true) }
                    )
                    Text(stringResource(R.string.dark_mode), modifier = Modifier.padding(start = 8.dp))
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                stringResource(R.string.language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val languageCode by viewModel.languageCode.collectAsState()
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = languageCode == "en",
                        onClick = { viewModel.updateLanguage("en") }
                    )
                    Text("English", modifier = Modifier.padding(start = 8.dp))
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = languageCode == "kn",
                        onClick = { viewModel.updateLanguage("kn") }
                    )
                    Text("ಕನ್ನಡ (Kannada)", modifier = Modifier.padding(start = 8.dp))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                stringResource(R.string.danger_zone),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )

            var showResetDialog by remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.DeleteForever, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All Inventory Data")
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { Text("Reset Inventory?", color = MaterialTheme.colorScheme.error) },
                    text = { Text("This will permanently delete ALL assets, issue logs, and repair records. This action cannot be undone.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.resetDatabase()
                                showResetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete Everything")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            // Dialogs for School Info
            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit School Name") },
                    text = {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("School Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateSchoolName(newName)
                                showEditDialog = false
                            },
                            enabled = newName.isNotBlank()
                        ) {
                            Text("Update")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showAddressDialog) {
                AlertDialog(
                    onDismissRequest = { showAddressDialog = false },
                    title = { Text("Edit School Address") },
                    text = {
                        OutlinedTextField(
                            value = newAddress,
                            onValueChange = { newAddress = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateSchoolAddress(newAddress)
                                showAddressDialog = false
                            },
                            enabled = newAddress.isNotBlank()
                        ) {
                            Text("Update")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddressDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showCodeDialog) {
                AlertDialog(
                    onDismissRequest = { showCodeDialog = false },
                    title = { Text("Edit DISE / School Code") },
                    text = {
                        OutlinedTextField(
                            value = newCode,
                            onValueChange = { newCode = it },
                            label = { Text("School Code") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateSchoolCode(newCode)
                                showCodeDialog = false
                            },
                            enabled = newCode.isNotBlank()
                        ) {
                            Text("Update")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCodeDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Namma Shaale Inventory • Version 1.0.0",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
