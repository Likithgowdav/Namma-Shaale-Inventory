package com.namma_shaale.inventory.presentation.assetlist

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.presentation.components.AppTopBar
import com.namma_shaale.inventory.presentation.settings.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.namma_shaale.inventory.util.CameraUtil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AssetListScreen(
    navController: NavController,
    viewModel: AssetListViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val schoolName by settingsViewModel.schoolName.collectAsState()
    var editingAsset by remember { mutableStateOf<Asset?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Namma Shaale Inventory",
                subtitle = "Asset Directory",
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.value.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.value.assets.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No assets found. Add an asset to see it here.")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Search Bar
                OutlinedTextField(
                    value = uiState.value.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Search name, serial, or location...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.value.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                            IconButton(onClick = { showFilterSheet = true }) {
                                Icon(
                                    Icons.Default.FilterList, 
                                    contentDescription = "Filter",
                                    tint = if (uiState.value.selectedCategory != null || uiState.value.selectedLocation != null) 
                                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Active Filters Summary (Optional, but good for UX)
                if (uiState.value.selectedCategory != null || uiState.value.selectedLocation != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filters:", style = MaterialTheme.typography.labelMedium)
                        if (uiState.value.selectedCategory != null) {
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.onCategorySelect(null) },
                                label = { Text(uiState.value.selectedCategory!!) },
                                trailingIcon = { Icon(Icons.Default.Clear, null, modifier = Modifier.size(14.dp)) }
                            )
                        }
                        if (uiState.value.selectedLocation != null) {
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.onLocationSelect(null) },
                                label = { Text(uiState.value.selectedLocation!!) },
                                trailingIcon = { Icon(Icons.Default.Clear, null, modifier = Modifier.size(14.dp)) }
                            )
                        }
                    }
                }

                if (uiState.value.filteredAssets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No matching assets found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        
                        items(uiState.value.filteredAssets) { asset ->
                            AssetDirectoryCard(
                                asset = asset,
                                navController = navController,
                                onDeleteClick = { viewModel.deleteAsset(asset) },
                                onEditClick = { editingAsset = asset }
                            )
                        }
                        
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text("Filter Assets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Category", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.value.selectedCategory == null,
                        onClick = { viewModel.onCategorySelect(null) },
                        label = { Text("All") }
                    )
                    uiState.value.categories.forEach { category ->
                        FilterChip(
                            selected = uiState.value.selectedCategory == category,
                            onClick = { viewModel.onCategorySelect(category) },
                            label = { Text(category) }
                        )
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text("Location", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.value.selectedLocation == null,
                        onClick = { viewModel.onLocationSelect(null) },
                        label = { Text("All") }
                    )
                    uiState.value.locations.forEach { location ->
                        FilterChip(
                            selected = uiState.value.selectedLocation == location,
                            onClick = { viewModel.onLocationSelect(location) },
                            label = { Text(location) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }

    if (editingAsset != null) {
        EditAssetDialog(
            asset = editingAsset!!,
            onDismiss = { editingAsset = null },
            onConfirm = { updatedAsset ->
                viewModel.updateAsset(updatedAsset)
                editingAsset = null
            }
        )
    }
}

@Composable
fun AssetDirectoryCard(
    asset: Asset,
    navController: NavController,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(asset.createdDate))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (asset.photoPath != null) {
                AsyncImage(
                    model = asset.photoPath,
                    contentDescription = "Asset Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(asset.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Row {
                    IconButton(onClick = { navController.navigate("asset_history/${asset.assetId}") }) {
                        Icon(Icons.Filled.History, contentDescription = "View History")
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Asset")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete Asset", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            Text("Category: ${asset.category}", style = MaterialTheme.typography.bodyMedium)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(asset.location ?: "No Location", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            
            Text("Quantity: ${asset.quantity}", style = MaterialTheme.typography.bodyMedium)
            Text("Purchase Year: ${asset.purchaseYear}", style = MaterialTheme.typography.bodyMedium)
            
            if (!asset.serialNo.isNullOrEmpty()) {
                Text("Serial No: ${asset.serialNo}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${asset.status}", 
                    style = MaterialTheme.typography.labelLarge,
                    color = when (asset.status) {
                        "GREEN" -> MaterialTheme.colorScheme.primary
                        "RED" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
                Text(
                    text = "Added: $dateString", 
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EditAssetDialog(
    asset: Asset,
    onDismiss: () -> Unit,
    onConfirm: (Asset) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(asset.name) }
    var category by remember { mutableStateOf(asset.category) }
    var quantity by remember { mutableStateOf(asset.quantity.toString()) }
    var purchaseYear by remember { mutableStateOf(asset.purchaseYear.toString()) }
    var serialNo by remember { mutableStateOf(asset.serialNo ?: "") }
    var location by remember { mutableStateOf(asset.location ?: "") }
    var photoPath by remember { mutableStateOf(asset.photoPath) }
    
    var tempPhotoUriStr by remember { mutableStateOf<String?>(null) }
    var tempPhotoPath by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUriStr != null) {
            photoPath = tempPhotoPath
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            photoPath = com.namma_shaale.inventory.util.CameraUtil.copyUriToInternalStorage(context, uri)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Asset") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Photo Preview and Edit
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    if (photoPath != null) {
                        AsyncImage(
                            model = photoPath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("No Photo", style = MaterialTheme.typography.labelMedium)
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val photoResult = com.namma_shaale.inventory.util.CameraUtil.createImageFileUri(context)
                            if (photoResult.isSuccess) {
                                val photoInfo = photoResult.getOrNull()!!
                                tempPhotoUriStr = photoInfo.first.toString()
                                tempPhotoPath = photoInfo.second
                                cameraLauncher.launch(photoInfo.first)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Camera", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Gallery", style = MaterialTheme.typography.labelSmall)
                    }
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Asset Name") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") })
                OutlinedTextField(value = purchaseYear, onValueChange = { purchaseYear = it }, label = { Text("Purchase Year") })
                OutlinedTextField(value = serialNo, onValueChange = { serialNo = it }, label = { Text("Serial Number") })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location/Room") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(asset.copy(
                    name = name,
                    category = category,
                    quantity = quantity.toIntOrNull() ?: asset.quantity,
                    purchaseYear = purchaseYear.toIntOrNull() ?: asset.purchaseYear,
                    serialNo = serialNo,
                    location = location,
                    photoPath = photoPath
                ))
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
