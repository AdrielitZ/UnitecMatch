package com.example.unitecmatch.ui.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.unitecmatch.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegistrationScreen(onRegistrationSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var isOfAge by remember { mutableStateOf(true) }
    // New, expanded list of interests
    val availableInterests = listOf(
        "Deportes", "Música", "Arte", "Lectura", "Cine", "Viajar", "Gaming",
        "Cocina", "Fotografía", "Bailar", "Yoga", "Senderismo", "Tecnología", "Moda", "Mascotas"
    )
    var selectedInterests by remember { mutableStateOf<Set<String>>(emptySet()) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var isLocationLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // --- Launchers ---
    fun createImageFile(): File = File.createTempFile("JPEG_", ".jpg", context.cacheDir)
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success) imageUri = tempImageUri }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            tempImageUri = FileProvider.getUriForFile(Objects.requireNonNull(context), "${context.packageName}.provider", createImageFile())
            cameraLauncher.launch(tempImageUri)
        } else { Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show() }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            isLocationLoading = true
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { loc ->
                    isLocationLoading = false
                    if (loc != null) location = loc.latitude to loc.longitude
                    else Toast.makeText(context, "Could not get location. Make sure GPS is enabled.", Toast.LENGTH_LONG).show()
                }.addOnFailureListener { isLocationLoading = false }
        } else { Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show() }
    }

    val isFormValid by derivedStateOf { name.isNotBlank() && lastName.isNotBlank() && birthDate != null && isOfAge && selectedInterests.isNotEmpty() && imageUri != null && location != null }

    // --- Dialogs ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let {
                        val selectedDate = Date(it).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        birthDate = selectedDate
                        isOfAge = Period.between(selectedDate, LocalDate.now()).years >= 18
                    }
                }) { Text("OK") }
            },
            dismissButton = { Button(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }
    if (showImageSourceDialog) {
        AlertDialog(onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Elige una opción") },
            confirmButton = { Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA); showImageSourceDialog = false }) { Text("Cámara") } },
            dismissButton = { Button(onClick = { galleryLauncher.launch("image/*"); showImageSourceDialog = false }) { Text("Galería") } })
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear Perfil") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).clickable { showImageSourceDialog = true }, contentAlignment = Alignment.Center) {
                if (imageUri != null) AsyncImage(model = imageUri, contentDescription = "Foto de perfil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Image(painter = painterResource(id = R.drawable.placeholder), contentDescription = "Placeholder", modifier = Modifier.size(70.dp), alpha = 0.5f)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Toca para seleccionar una foto", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))

            // --- Form Fields ---
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = birthDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                onValueChange = {},
                label = { Text("Fecha de Nacimiento") },
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, "Select date", Modifier.clickable { showDatePicker = true }) },
                isError = !isOfAge,
                modifier = Modifier.fillMaxWidth()
            )
            if (!isOfAge) Text("Debes ser mayor de 18 años para registrarte.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            
            // --- Interests Chips ---
            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecciona tus intereses", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                availableInterests.forEach { interest ->
                    FilterChip(
                        selected = selectedInterests.contains(interest),
                        onClick = {
                            selectedInterests = if (selectedInterests.contains(interest)) selectedInterests - interest else selectedInterests + interest
                        },
                        label = { Text(interest) }
                    )
                }
            }
            
            // --- Buttons ---
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) }, enabled = !isLocationLoading, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                if (isLocationLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text(if (location != null) "Ubicación Obtenida" else "Permitir Ubicación")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRegistrationSuccess, enabled = isFormValid, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text("REGISTRARSE", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
