package com.namma_shaale.inventory.presentation.healthcheck

import androidx.compose.ui.res.stringResource
import com.namma_shaale.inventory.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import com.namma_shaale.inventory.data.local.entities.Asset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.namma_shaale.inventory.presentation.components.AppTopBar
import com.namma_shaale.inventory.presentation.settings.SettingsViewModel
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.lazy.LazyRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthCheckScreen(
    navController: NavController,
    viewModel: HealthCheckViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val schoolName by settingsViewModel.schoolName.collectAsState()
    var showFilterSheet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.monthly_health_check)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search and Filter Bar
            OutlinedTextField(
                value = uiState.value.searchQuery,
                onValueChange = { query -> viewModel.updateSearchQuery(query) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                placeholder = { Text("Search assets...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (uiState.value.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, null)
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

            // Active Filter Labels
            if (uiState.value.selectedCategory != null || uiState.value.selectedLocation != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.value.selectedCategory?.let {
                        SuggestionChip(
                            onClick = { viewModel.updateSelectedCategory(null) },
                            label = { Text(it) },
                            icon = { Icon(Icons.Default.Clear, null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                    uiState.value.selectedLocation?.let {
                        SuggestionChip(
                            onClick = { viewModel.updateSelectedLocation(null) },
                            label = { Text(it) },
                            icon = { Icon(Icons.Default.Clear, null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${uiState.value.updatedCount}/${uiState.value.totalCount} items verified",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!uiState.value.isCompleted && uiState.value.totalCount > 0) {
                    TextButton(
                        onClick = { viewModel.markAllRemainingAsWorking() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All Working", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = {
                    if (uiState.value.totalCount > 0) {
                        uiState.value.updatedCount.toFloat() / uiState.value.totalCount
                    } else {
                        0f
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.value.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredAssets = uiState.value.assets.filter { asset ->
                    val matchesCategory = uiState.value.selectedCategory == null || asset.category == uiState.value.selectedCategory
                    val matchesLocation = uiState.value.selectedLocation == null || asset.location == uiState.value.selectedLocation
                    val matchesSearch = uiState.value.searchQuery.isBlank() || 
                        asset.name.contains(uiState.value.searchQuery, ignoreCase = true) ||
                        (asset.serialNo?.contains(uiState.value.searchQuery, ignoreCase = true) ?: false)
                    
                    matchesCategory && matchesLocation && matchesSearch
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAssets) { asset ->
                        val isVerified = uiState.value.checkedAssetIds.contains(asset.assetId)
                        val note = uiState.value.assetNotes[asset.assetId] ?: ""
                        AssetHealthCheckCard(
                            asset = asset,
                            isVerified = isVerified,
                            note = note,
                            onNoteChange = { newNote -> viewModel.updateAssetNote(asset.assetId, newNote) },
                            onGreenClick = { viewModel.updateAssetStatus(asset.assetId, "GREEN") },
                            onYellowClick = { viewModel.updateAssetStatus(asset.assetId, "YELLOW") },
                            onRedClick = { viewModel.updateAssetStatus(asset.assetId, "RED") }
                        )
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
                Text("Filter Health Check", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Category", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.value.selectedCategory == null,
                        onClick = { viewModel.updateSelectedCategory(null) },
                        label = { Text("All Categories") }
                    )
                    uiState.value.categories.forEach { category ->
                        FilterChip(
                            selected = uiState.value.selectedCategory == category,
                            onClick = { viewModel.updateSelectedCategory(category) },
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
                        onClick = { viewModel.updateSelectedLocation(null) },
                        label = { Text("All Locations") }
                    )
                    uiState.value.locations.forEach { location ->
                        FilterChip(
                            selected = uiState.value.selectedLocation == location,
                            onClick = { viewModel.updateSelectedLocation(location) },
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
}

@Composable
private fun AssetHealthCheckCard(
    asset: Asset,
    isVerified: Boolean,
    note: String,
    onNoteChange: (String) -> Unit,
    onGreenClick: () -> Unit,
    onYellowClick: () -> Unit,
    onRedClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isVerified) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isVerified) 0.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Status Dot
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(10.dp)) {
                        drawCircle(
                            color = when (asset.status) {
                                "GREEN" -> Color(0xFF4CAF50)
                                "YELLOW" -> Color(0xFFFFC107)
                                "RED" -> Color(0xFFF44336)
                                else -> Color.Gray
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = asset.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isVerified) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            if (isVerified) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.CheckCircle, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = asset.serialNo ?: "NO-SERIAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(" • ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = asset.location ?: "Unassigned",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    if (isVerified) {
                        Text(
                            "VERIFIED", 
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    SuggestionChip(
                        onClick = {},
                        label = { Text(asset.category, style = MaterialTheme.typography.labelSmall) },
                        enabled = false
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Add remarks (e.g., Screen flickering, dusty)", style = MaterialTheme.typography.bodySmall) },
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(8.dp),
                maxLines = 2,
                enabled = !isVerified
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Working Button
                OutlinedButton(
                    onClick = onGreenClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    colors = if (asset.status == "GREEN") ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)) else ButtonDefaults.outlinedButtonColors(),
                    border = if (asset.status == "GREEN") androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)) else ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("WORKING", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, maxLines = 1)
                }
                
                // Repair Button
                OutlinedButton(
                    onClick = onYellowClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    colors = if (asset.status == "YELLOW") ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFFF8E1), contentColor = Color(0xFFF57F17)) else ButtonDefaults.outlinedButtonColors(),
                    border = if (asset.status == "YELLOW") androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF57F17)) else ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.Build, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("REPAIR", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, maxLines = 1)
                }
                
                // Broken Button
                OutlinedButton(
                    onClick = onRedClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    colors = if (asset.status == "RED") ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFC62828)) else ButtonDefaults.outlinedButtonColors(),
                    border = if (asset.status == "RED") androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828)) else ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.Cancel, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("BROKEN", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, maxLines = 1)
                }
            }
        }
    }
}
