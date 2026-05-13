package com.namma_shaale.inventory.presentation.issue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.namma_shaale.inventory.presentation.components.AppTopBar
import com.namma_shaale.inventory.presentation.settings.SettingsViewModel
import com.namma_shaale.inventory.data.local.entities.Asset
import com.namma_shaale.inventory.data.local.entities.IssueLog
import com.namma_shaale.inventory.data.local.entities.RepairRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueRepairScreen(
    navController: NavController,
    viewModel: IssueRepairViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val schoolName by settingsViewModel.schoolName.collectAsState()
    
    var showLogIssueDialog by remember { mutableStateOf(false) }
    var resolvingRepairId by remember { mutableStateOf<String?>(null) }
    var editingIssue by remember { mutableStateOf<IssueLog?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.value.successMessage) {
        uiState.value.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.value.errorMessage) {
        uiState.value.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Namma Shaale Inventory",
                subtitle = "Issues & Repair Tracking"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.value.selectedRepairTab == 0) {
                FloatingActionButton(onClick = { showLogIssueDialog = true }) {
                    Icon(Icons.Filled.Add, "Log Issue")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = uiState.value.selectedRepairTab) {
                Tab(
                    selected = uiState.value.selectedRepairTab == 0,
                    onClick = { viewModel.setRepairTab(0) },
                    text = { Text("Issue Log") }
                )
                Tab(
                    selected = uiState.value.selectedRepairTab == 1,
                    onClick = { viewModel.setRepairTab(1) },
                    text = { Text("Repair Queue") }
                )
            }

            if (uiState.value.selectedRepairTab == 0) {
                IssueLogTab(
                    issues = uiState.value.issues,
                    assets = uiState.value.assets,
                    onEditClick = { editingIssue = it },
                    onDeleteClick = { viewModel.deleteIssue(it) }
                )
            } else {
                RepairQueueTab(
                    repairRequests = uiState.value.repairRequests.filter { it.status != "RESOLVED" },
                    assets = uiState.value.assets,
                    onResolveClick = { requestId -> resolvingRepairId = requestId }
                )
            }
        }
    }

    if (showLogIssueDialog) {
        LogIssueDialog(
            assets = uiState.value.assets,
            onDismiss = { showLogIssueDialog = false },
            onConfirm = { assetId, type, reason ->
                viewModel.setSelectedAsset(assetId)
                viewModel.setIssueType(type)
                viewModel.setIssueReason(reason)
                viewModel.logIssue()
                showLogIssueDialog = false
            }
        )
    }

    if (resolvingRepairId != null) {
        ResolveRepairDialog(
            onDismiss = { resolvingRepairId = null },
            onConfirm = { notes ->
                viewModel.markRepairResolved(resolvingRepairId!!, notes)
                resolvingRepairId = null
            }
        )
    }

    if (editingIssue != null) {
        EditIssueDialog(
            issue = editingIssue!!,
            onDismiss = { editingIssue = null },
            onConfirm = { type, reason ->
                viewModel.updateIssue(editingIssue!!, type, reason)
                editingIssue = null
            }
        )
    }
}

@Composable
fun IssueLogTab(
    issues: List<IssueLog>,
    assets: List<Asset>,
    onEditClick: (IssueLog) -> Unit,
    onDeleteClick: (IssueLog) -> Unit
) {
    if (issues.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No issues logged.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(issues) { issue ->
                val assetName = assets.find { it.assetId == issue.assetId }?.name ?: "Unknown Asset"
                IssueLogCard(
                    issue = issue,
                    assetName = assetName,
                    onEditClick = { onEditClick(issue) },
                    onDeleteClick = { onDeleteClick(issue) }
                )
            }
        }
    }
}

@Composable
fun IssueLogCard(
    issue: IssueLog,
    assetName: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(issue.date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(assetName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(dateString, style = MaterialTheme.typography.labelSmall)
                }
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Type: ${issue.type}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Reason: ${issue.reason}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RepairQueueTab(repairRequests: List<RepairRequest>, assets: List<Asset>, onResolveClick: (String) -> Unit) {
    if (repairRequests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending repairs!", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(repairRequests) { request ->
                val assetName = assets.find { it.assetId == request.assetId }?.name ?: "Unknown Asset"
                RepairRequestCard(request, assetName, onResolveClick)
            }
        }
    }
}

@Composable
fun RepairRequestCard(request: RepairRequest, assetName: String, onResolveClick: (String) -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(request.raisedDate))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (request.priority == "HIGH") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(assetName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Icon(Icons.Filled.Build, contentDescription = "Repair")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Priority: ${request.priority}", fontWeight = FontWeight.Bold)
            Text("Raised: $dateString", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onResolveClick(request.requestId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mark as Resolved")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogIssueDialog(
    assets: List<Asset>,
    onDismiss: () -> Unit,
    onConfirm: (assetId: String, type: String, reason: String) -> Unit
) {
    var selectedAssetId by remember { mutableStateOf(if (assets.isNotEmpty()) assets[0].assetId else "") }
    var selectedType by remember { mutableStateOf("DAMAGE") }
    var reason by remember { mutableStateOf("") }
    
    var assetExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log New Issue") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Asset Dropdown
                ExposedDropdownMenuBox(
                    expanded = assetExpanded,
                    onExpandedChange = { assetExpanded = !assetExpanded }
                ) {
                    OutlinedTextField(
                        value = assets.find { it.assetId == selectedAssetId }?.name ?: "Select Asset",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Asset") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assetExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = assetExpanded,
                        onDismissRequest = { assetExpanded = false }
                    ) {
                        assets.forEach { asset ->
                            DropdownMenuItem(
                                text = { Text(asset.name) },
                                onClick = {
                                    selectedAssetId = asset.assetId
                                    assetExpanded = false
                                }
                            )
                        }
                    }
                }

                // Type Dropdown
                val types = listOf("DAMAGE", "LOST", "STOLEN", "OTHER")
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Issue Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (selectedAssetId.isNotEmpty() && reason.isNotBlank()) {
                    onConfirm(selectedAssetId, selectedType, reason)
                }
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIssueDialog(
    issue: IssueLog,
    onDismiss: () -> Unit,
    onConfirm: (type: String, reason: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(issue.type) }
    var reason by remember { mutableStateOf(issue.reason) }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Issue Log") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Type Dropdown
                val types = listOf("DAMAGE", "LOST", "STOLEN", "OTHER")
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Issue Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (reason.isNotBlank()) {
                    onConfirm(selectedType, reason)
                }
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ResolveRepairDialog(
    onDismiss: () -> Unit,
    onConfirm: (notes: String) -> Unit
) {
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resolve Repair") },
        text = {
            Column {
                Text("Are you sure you want to mark this repair as resolved? The asset will be marked as working (GREEN) again.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("SDMC / Resolution Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(notes) }) {
                Text("Resolve")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
