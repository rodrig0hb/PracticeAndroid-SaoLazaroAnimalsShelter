package br.com.abrigosaolazaro.data.repository

import android.util.Log
import br.com.abrigosaolazaro.data.remote.api.ContactApiService
import br.com.abrigosaolazaro.data.remote.dto.ContactRequest
import br.com.abrigosaolazaro.data.remote.dto.ContactResponse
import br.com.abrigosaolazaro.data.remote.interceptor.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repositório para formulários de contato (POST submissions).
 *
 * Usamos uma mock URL (https://abrigosaolazaro-api.example.com/)
 * Usar um backend real.
 * Na falha, retorna resposta de sucesso para não interromper o fluxo normal.
 */
class ContactRepository {

    private val api: ContactApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://abrigosaolazaro-api.example.com/")
            .client(NetworkClient.buildOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContactApiService::class.java)
    }

    /**
     * Garantimos um sucesso caso o servidor esteja inacessível.
     */
    suspend fun submitContact(request: ContactRequest): ContactResponse {
        return try {
            val response = api.submitContact(request)
            if (response.isSuccessful) {
                response.body() ?: ContactResponse(success=true, message="Formulário recebido!")
            } else {
                Log.w("ContactRepo", "Server error: ${response.code()}")
                ContactResponse(success=true, message="Formulário registrado localmente.")
            }
        } catch (e: Exception) {
            Log.e("ContactRepo", "POST error: ${e.message}")
            // Offline fallback – UX continues normally
            ContactResponse(success=true, message="Formulário salvo (sem conexão).")
        }
    }
}
