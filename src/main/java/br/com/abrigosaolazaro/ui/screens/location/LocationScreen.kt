package br.com.abrigosaolazaro.ui.screens.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.abrigosaolazaro.data.repository.RouteRepository
import br.com.abrigosaolazaro.ui.components.ShelterHeader
import br.com.abrigosaolazaro.util.NotificationHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// ── NOTA IMPORTANTE ───────────────────────────────────────────────────
// Para realização do desenvolvimento e testes foi utilizada uma Chave de demonstração do Maps
// Por questões de segurança ela não foi incluída no projeto

private const val MAPS_API_KEY = "YOUR_MAPS_API_KEY"

// Coordenadas fixas do abrigo São Lázaro
private val SHELTER_LATLNG = LatLng(RouteRepository.SHELTER_LAT, RouteRepository.SHELTER_LNG)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(
    viewModel  : LocationViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val state   by viewModel.state.collectAsState()

    // ── Permission state (location + notifications) ───────────────────
    val permissionsToRequest = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            add(Manifest.permission.POST_NOTIFICATIONS)
    }
    val permissionsState = rememberMultiplePermissionsState(permissionsToRequest)

    // ── Posição centrada em Fortaleza / São Lázaro ───────────────────
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(SHELTER_LATLNG, 13f)
    }

    // ── Checando se todas permissões foram concedidas para encontrar localização primeira ─────────────
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            fetchUserLocation(context, viewModel)
        }
    }

    // ── Mover a camera quando a localização do usuário é recebida ────────────────────────
    LaunchedEffect(state.userLocation) {
        state.userLocation?.let { loc ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(loc, 14f), durationMs = 800
            )
        }
    }

    // ── Route notification ────────────────────────────────────────────
    LaunchedEffect(state.routeActive) {
        if (state.routeActive && state.distanceText.isNotBlank()) {
            NotificationHelper.showRouteNotification(context, state.distanceText, state.durationText)
            Toast.makeText(context, "Rota iniciada! Acompanhe pela notificação.", Toast.LENGTH_LONG).show()
        } else {
            NotificationHelper.cancelRouteNotification(context)
        }
    }

    // ── Error toast ───────────────────────────────────────────────────
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Column {
                ShelterHeader()
                TopAppBar(
                    title = { Text("Localização do Abrigo", style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        IconButton(onClick = {
                            NotificationHelper.cancelRouteNotification(context)
                            onBackClick()
                        }) { Icon(Icons.Default.ArrowBack, "Voltar") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor            = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor         = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Google Map ────────────────────────────────────────────
            GoogleMap(
                modifier             = Modifier.fillMaxSize(),
                cameraPositionState  = cameraPositionState,
                uiSettings           = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false),
                properties           = MapProperties(isMyLocationEnabled = permissionsState.allPermissionsGranted)
            ) {
                // Marcador do abrigo
                Marker(
                    state   = MarkerState(position = SHELTER_LATLNG),
                    title   = "Abrigo São Lázaro",
                    snippet = state.shelterAddress,
                    icon    = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )

                // Marcador do usuário
                state.userLocation?.let { loc ->
                    Marker(
                        state   = MarkerState(position = loc),
                        title   = "Sua localização",
                        icon    = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }

                // Decoded polyline route
                if (state.routePolyline.isNotEmpty()) {
                    Polyline(
                        points = state.routePolyline,
                        color  = Color(0xFF1565C0),
                        width  = 10f
                    )
                }
            }

            // ── info card ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Info depois que a rota aparece
                if (state.distanceText.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoChip(icon = Icons.Default.Route,    label = "Distância", value = state.distanceText)
                        InfoChip(icon = Icons.Default.Schedule, label = "Tempo",     value = state.durationText)
                    }
                }

                // Card
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        Text("Abrigo São Lázaro", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Text(state.shelterAddress, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

                        when {
                            !permissionsState.allPermissionsGranted -> {
                                // Butão de requisição de permissão
                                Button(
                                    onClick  = {
                                        permissionsState.launchMultiplePermissionRequest()
                                        Toast.makeText(context,
                                            "Permissão de localização necessária para traçar rota.",
                                            Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape    = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.LocationOn, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Permitir localização")
                                }
                            }
                            state.isLoadingRoute -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color    = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text("Calculando rota...")
                                }
                            }
                            state.routeActive -> {
                                OutlinedButton(
                                    onClick  = { viewModel.stopRoute() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape    = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Close, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Parar rota")
                                }
                            }
                            else -> {
                                Button(
                                    onClick  = { fetchUserLocation(context, viewModel) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape    = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Directions, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Traçar rota")
                                }
                            }
                        }

                        // Mensagem de permissão negada
                        if (state.permissionDenied) {
                            Text(
                                "⚠️ Permissão negada. Habilite a localização nas configurações do dispositivo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // ── Definindo overlay para o geocoding inicial ─────────────────
            if (state.isLoadingRoute && state.userLocation == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

/** Usando FusedLocationProviderClient para obter a última localização, então engatilhar a calculação da rota com toast para cada contexto */
@SuppressLint("MissingPermission")
private fun fetchUserLocation(context: Context, viewModel: LocationViewModel) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    client.lastLocation
        .addOnSuccessListener { loc ->
            if (loc != null) {
                Toast.makeText(context, "Localização obtida! Calculando rota...", Toast.LENGTH_SHORT).show()
                viewModel.onLocationReceived(loc.latitude, loc.longitude, MAPS_API_KEY)
            } else {
                // Request fresh location
                Toast.makeText(context, "Aguardando sinal de GPS...", Toast.LENGTH_SHORT).show()
                val req = com.google.android.gms.location.LocationRequest.Builder(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5_000L
                ).setMaxUpdates(1).build()

                client.requestLocationUpdates(
                    req,
                    object : com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                            result.lastLocation?.let { fresh ->
                                Toast.makeText(context, "GPS obtido! Traçando rota...", Toast.LENGTH_SHORT).show()
                                viewModel.onLocationReceived(fresh.latitude, fresh.longitude, MAPS_API_KEY)
                                client.removeLocationUpdates(this)
                            }
                        }
                    },
                    android.os.Looper.getMainLooper()
                )
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Erro ao obter localização: ${it.message}", Toast.LENGTH_LONG).show()
            viewModel.onPermissionDenied()
        }
}
