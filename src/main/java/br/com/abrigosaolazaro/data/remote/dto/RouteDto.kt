package br.com.abrigosaolazaro.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Directions API response ───────────────────────────────────────────

data class DirectionsResponse(
    val routes: List<Route> = emptyList(),
    val status: String = ""
)

data class Route(
    val legs: List<Leg> = emptyList(),
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline = OverviewPolyline()
)

data class Leg(
    val distance: TextValue = TextValue(),
    val duration: TextValue = TextValue(),
    @SerializedName("start_address") val startAddress: String = "",
    @SerializedName("end_address") val endAddress: String = ""
)

data class TextValue(val text: String = "", val value: Int = 0)

data class OverviewPolyline(
    @SerializedName("points") val encodedPolyline: String = ""
)

// ── Geocoding API response ────────────────────────────────────────────

data class GeocodingResponse(
    val results: List<GeoResult> = emptyList(),
    val status: String = ""
)

data class GeoResult(
    @SerializedName("formatted_address") val formattedAddress: String = "",
    val geometry: Geometry = Geometry()
)

data class Geometry(val location: LatLngDto = LatLngDto())

data class LatLngDto(val lat: Double = 0.0, val lng: Double = 0.0)

// ── POST: contact form sent to server ────────────────────────────────

data class ContactRequest(
    val name: String,
    val email: String,
    val phone: String,
    val animalName: String,
    val message: String,
    val type: String   // "ADOPTION" | "ABUSE_REPORT"
)

data class ContactResponse(
    val success: Boolean = false,
    val message: String = "",
    val id: String = ""
)
