package br.com.abrigosaolazaro.data.remote.api

import br.com.abrigosaolazaro.data.remote.dto.ContactRequest
import br.com.abrigosaolazaro.data.remote.dto.ContactResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// O método POST é implementado aqui como uma simulação sem serviço de backend real
// Para uso oficial deve-se substituir a URL base pela do Firebase ou Supabase
// Usamos um mock server apenas para demonstração
// e a ViewModel lida com a resposta offline.
//
interface ContactApiService {

    @POST("contacts")
    suspend fun submitContact(@Body request: ContactRequest): Response<ContactResponse>
}
