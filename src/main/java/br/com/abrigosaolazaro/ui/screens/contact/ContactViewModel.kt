package br.com.abrigosaolazaro.ui.screens.contact

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.abrigosaolazaro.data.remote.dto.ContactRequest
import br.com.abrigosaolazaro.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ContactType { ADOPTION, ABUSE_REPORT }

data class ContactFormState(
    val name            : String      = "",
    val email           : String      = "",
    val phone           : String      = "",
    val animalName      : String      = "",
    val message         : String      = "",
    val contactType     : ContactType = ContactType.ADOPTION,
    val isSubmitting    : Boolean     = false,
    val isSubmitted     : Boolean     = false,
    val submitError     : String?     = null,
    val nameError       : String?     = null,
    val emailError      : String?     = null,
    val messageError    : String?     = null
)

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    private val _state = MutableStateFlow(ContactFormState())
    val state: StateFlow<ContactFormState> = _state.asStateFlow()

    fun setPrefilledAnimalName(name: String) {
        if (name.isNotBlank() && _state.value.animalName.isBlank())
            _state.update { it.copy(animalName = name, contactType = ContactType.ADOPTION) }
    }

    fun onNameChange(v: String)           = _state.update { it.copy(name = v,        nameError    = null) }
    fun onEmailChange(v: String)          = _state.update { it.copy(email = v,       emailError   = null) }
    fun onPhoneChange(v: String)          = _state.update { it.copy(phone = v) }
    fun onAnimalNameChange(v: String)     = _state.update { it.copy(animalName = v) }
    fun onMessageChange(v: String)        = _state.update { it.copy(message = v,     messageError = null) }
    fun onContactTypeChange(t: ContactType) = _state.update { it.copy(contactType = t) }

    /** Valida e envia via POST (Retrofit). */
    fun submit() {
        val s = _state.value
        val nameErr    = if (s.name.isBlank()) "Informe seu nome" else null
        val emailErr   = when {
            s.email.isBlank() -> "Informe seu e-mail"
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> "E-mail inválido"
            else -> null
        }
        val msgErr = if (s.message.isBlank()) "Informe sua mensagem" else null

        if (nameErr != null || emailErr != null || msgErr != null) {
            _state.update { it.copy(nameError = nameErr, emailError = emailErr, messageError = msgErr) }
            return
        }

        _state.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            val req = ContactRequest(
                name       = s.name,
                email      = s.email,
                phone      = s.phone,
                animalName = s.animalName,
                message    = s.message,
                type       = s.contactType.name
            )
            val result = repository.submitContact(req)
            _state.update { it.copy(isSubmitting = false, isSubmitted = result.success) }
        }
    }

    fun resetForm() = _state.update { ContactFormState() }

    class Factory(private val repo: ContactRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(cls: Class<T>): T = ContactViewModel(repo) as T
    }
}
