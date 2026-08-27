package br.com.abrigosaolazaro.data.remote.api

import br.com.abrigosaolazaro.data.remote.dto.DirectionsResponse
import br.com.abrigosaolazaro.data.remote.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Fazendo GET requests.
 *
 * Base URL: https://maps.googleapis.com/maps/api/
 */
interface MapsApiService {

    /**
     * Usando GET para obter rota entre dois pontos.
     */
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin")      origin:      String,
        @Query("destination") destination: String,
        @Query("mode")        mode:        String = "driving",
        @Query("language")    language:    String = "pt-BR",
        @Query("key")         apiKey:      String
    ): DirectionsResponse

    /**
     * Usando GET geocoding para confirmar coordenadas.
     */
    @GET("geocode/json")
    suspend fun geocodeAddress(
        @Query("address")  address: String,
        @Query("language") language: String = "pt-BR",
        @Query("key")      apiKey: String
    ): GeocodingResponse
}
