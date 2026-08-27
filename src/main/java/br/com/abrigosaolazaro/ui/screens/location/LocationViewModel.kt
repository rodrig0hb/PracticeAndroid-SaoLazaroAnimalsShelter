package br.com.abrigosaolazaro.ui.screens.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.abrigosaolazaro.data.repository.RouteRepository
import br.com.abrigosaolazaro.util.PolylineDecoder
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
//Data class de variáveis de estado para a LocationScreen
data class LocationUiState(
    val userLocation    : LatLng?       = null,
    val shelterLocation : LatLng        = LatLng(RouteRepository.SHELTER_LAT, RouteRepository.SHELTER_LNG),
    val routePolyline   : List<LatLng>  = emptyList(),
    val distanceText    : String        = "",
    val durationText    : String        = "",
    val isLoadingRoute  : Boolean       = false,
    val errorMessage    : String?       = null,
    val permissionDenied: Boolean       = false,
    val routeActive     : Boolean       = false,
    // From GET geocoding
    val shelterAddress  : String        = RouteRepository.SHELTER_ADDRESS
)

class LocationViewModel(private val repository: RouteRepository) : ViewModel() {

    private val _state = MutableStateFlow(LocationUiState())
    val state: StateFlow<LocationUiState> = _state.asStateFlow()

    /** Chamado quando o FusedLocationProviderClient entrega uma localição do usuário. */
    fun onLocationReceived(lat: Double, lng: Double, apiKey: String) {
        val userLatLng = LatLng(lat, lng)
        _state.update { it.copy(userLocation = userLatLng, isLoadingRoute = true, errorMessage = null) }

        viewModelScope.launch {
            // GET 1 - Endereço do abrigo
            val geoResult = repository.geocodeShelter(apiKey)
            val displayAddress = geoResult?.results?.firstOrNull()?.formattedAddress
                ?: RouteRepository.SHELTER_ADDRESS
            _state.update { it.copy(shelterAddress = displayAddress) }

            // GET 2 – Direções até lá
            val directions = repository.getDirections(lat, lng, apiKey)
            if (directions != null && directions.status == "OK") {
                val leg      = directions.routes.firstOrNull()?.legs?.firstOrNull()
                val encoded  = directions.routes.firstOrNull()?.overviewPolyline?.encodedPolyline ?: ""
                val polyline = if (encoded.isNotBlank()) PolylineDecoder.decode(encoded) else emptyList()
                _state.update {
                    it.copy(
                        routePolyline  = polyline,
                        distanceText   = leg?.distance?.text ?: "",
                        durationText   = leg?.duration?.text ?: "",
                        isLoadingRoute = false,
                        routeActive    = polyline.isNotEmpty()
                    )
                }
            } else {
                // Rota indisponível
                _state.update {
                    it.copy(
                        isLoadingRoute = false,
                        errorMessage   = "Rota indisponível. Verifique sua chave de API do Maps."
                    )
                }
            }
        }
    }

    fun onPermissionDenied() {
        _state.update { it.copy(permissionDenied = true, isLoadingRoute = false) }
    }

    fun clearError() = _state.update { it.copy(errorMessage = null) }

    fun stopRoute() {
        _state.update { it.copy(routeActive = false, routePolyline = emptyList()) }
    }

    class Factory(private val repo: RouteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(cls: Class<T>): T = LocationViewModel(repo) as T
    }
}
