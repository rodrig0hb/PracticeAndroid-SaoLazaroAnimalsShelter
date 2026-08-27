package br.com.abrigosaolazaro.data.repository

import android.util.Log
import br.com.abrigosaolazaro.data.remote.api.MapsApiService
import br.com.abrigosaolazaro.data.remote.dto.DirectionsResponse
import br.com.abrigosaolazaro.data.remote.interceptor.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repositório responsivo de rotas
 *
 * Expõe dois tipo de GET requests:
 *  - getDirections()  – Pega rota do usuário até abrigo
 *  - geocodeShelter() – Pega geolocalização de confirmação do abrigo
 */
class RouteRepository {

    private val api: MapsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(NetworkClient.buildOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapsApiService::class.java)
    }

    /**
     * Retorna null se a chamada da API falhar, assim pode mostrar um fallback.
     */
    suspend fun getDirections(
        originLat: Double,
        originLng: Double,
        apiKey: String
    ): DirectionsResponse? {
        return try {
            val origin      = "$originLat,$originLng"
            val destination = SHELTER_LAT_LNG
            api.getDirections(
                origin      = origin,
                destination = destination,
                apiKey      = apiKey
            )
        } catch (e: Exception) {
            Log.e("RouteRepository", "getDirections error: ${e.message}")
            null
        }
    }

    /**
     *  Retorna null na falha.
     */
    suspend fun geocodeShelter(apiKey: String) =
        try {
            api.geocodeAddress(
                address = SHELTER_ADDRESS,
                apiKey  = apiKey
            )
        } catch (e: Exception) {
            Log.e("RouteRepository", "geocode error: ${e.message}")
            null
        }

    companion object {
        // Abrigo São Lázaro – Av. Eng. Luiz Montenegro, 430, Siqueira, Fortaleza-CE
        const val SHELTER_LAT     = -3.8012
        const val SHELTER_LNG     = -38.6218
        const val SHELTER_LAT_LNG = "$SHELTER_LAT,$SHELTER_LNG"
        const val SHELTER_ADDRESS = "Av. Eng. Luiz Montenegro, 430 - Siqueira, Fortaleza - CE"
    }
}
