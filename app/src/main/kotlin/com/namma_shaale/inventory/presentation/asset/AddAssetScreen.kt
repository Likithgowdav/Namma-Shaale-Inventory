package com.namma_shaale.inventory.presentation.asset

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.namma_shaale.inventory.presentation.components.AppTopBar
import com.namma_shaale.inventory.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    navController: NavController,
    viewModel: AddAssetViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val schoolName by settingsViewModel.schoolName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val expandedCategory = remember { mutableStateOf(false) }
    val context = LocalContext.current
    var currentPhotoUriStr by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    var currentPhotoPath by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(uiState.value.successMessage) {
        if (uiState.value.successMessage != null) {
            navController.popBackStack()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUriStr != null) {
            viewModel.updatePhoto(Uri.parse(currentPhotoUriStr), currentPhotoPath)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = com.namma_shaale.inventory.util.CameraUtil.copyUriToInternalStorage(context, uri)
            viewModel.updatePhoto(uri, path)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Namma Shaale Inventory",
                subtitle = "Add New Asset",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.value.assetName,
                    onValueChange = { viewModel.updateAssetName(it) },
                    label = { Text("Asset Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expandedCategory.value,
                    onExpandedChange = { expandedCategory.value = !expandedCategory.value }
                ) {
                    OutlinedTextField(
                        value = uiState.value.category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedCategory.value
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory.value,
                        onDismissRequest = { expandedCategory.value = false }
                    ) {
                        listOf("Sports Equipment", "Lab Equipment", "IT/Digital", "Furniture").forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    viewModel.updateCategory(it)
                                    expandedCategory.value = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = uiState.value.serialNo,
                    onValueChange = { viewModel.updateSerialNo(it) },
                    label = { Text("Serial Number (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = uiState.value.purchaseYearStr,
                    onValueChange = { viewModel.updatePurchaseYear(it) },
                    label = { Text("Purchase Year *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = uiState.value.quantityStr,
                    onValueChange = { viewModel.updateQuantity(it) },
                    label = { Text("Quantity *") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                val qty = uiState.value.quantityStr.toIntOrNull() ?: 0
                if (qty > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = uiState.value.createIndividually,
                            onCheckedChange = { viewModel.toggleCreateIndividually(it) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Track items individually", 
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Will create $qty separate records (e.g. ${uiState.value.assetName} #1, #2...)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = uiState.value.location,
                    onValueChange = { viewModel.updateLocation(it) },
                    label = { Text("Room Number / Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { 
                            val photoResult = com.namma_shaale.inventory.util.CameraUtil.createImageFileUri(context)
                            if (photoResult.isSuccess) {
                                val photoInfo = photoResult.getOrNull()!!
                                currentPhotoUriStr = photoInfo.first.toString()
                                currentPhotoPath = photoInfo.second
                                try {
                                    cameraLauncher.launch(photoInfo.first)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Launch Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                                }
                            } else {
                                val errorMsg = photoResult.exceptionOrNull()?.message ?: "Unknown error"
                                android.widget.Toast.makeText(context, "File Error: $errorMsg", android.widget.Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Capture")
                    }
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Gallery")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (uiState.value.photoUri != null) {
                item {
                    Text("Photo Preview:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = uiState.value.photoUri,
                        contentDescription = "Captured Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            item {
                Button(
                    onClick = { viewModel.saveAsset() },
                    enabled = uiState.value.isFormValid && !uiState.value.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.value.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text("Save Asset")
                    }
                }
                if (uiState.value.successMessage != null) {
                    Text(text = uiState.value.successMessage!!, color = MaterialTheme.colorScheme.primary)
                }
                if (uiState.value.errorMessage != null) {
                    Text(text = uiState.value.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
