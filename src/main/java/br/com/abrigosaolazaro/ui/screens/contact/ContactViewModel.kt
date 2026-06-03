package br.com.abrigosaolazaro.ui.screens.contact

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Tipo de solicitação no formulário */
enum class ContactType { ADOPTION, ABUSE_REPORT }

/** Estado imutável do formulário */
data class ContactFormState(
    val name           : String      = "",
    val email          : String      = "",
    val phone          : String      = "",
    val animalName     : String      = "",
    val message        : String      = "",
    val contactType    : ContactType = ContactType.ADOPTION,
    val isSubmitted    : Boolean     = false,
    // Erros de validação
    val nameError      : String?     = null,
    val emailError     : String?     = null,
    val messageError   : String?     = null
)

class ContactViewModel : ViewModel() {

    private val _state = MutableStateFlow(ContactFormState())
    val state: StateFlow<ContactFormState> = _state.asStateFlow()

    // Pré-preenche quando vem do botão "Adotar"
    fun setPrefilledAnimalName(name: String) {
        if (name.isNotBlank() && _state.value.animalName.isBlank()) {
            _state.update { it.copy(animalName = name, contactType = ContactType.ADOPTION) }
        }
    }

    fun onNameChange(v: String)        = _state.update { it.copy(name        = v, nameError    = null) }
    fun onEmailChange(v: String)       = _state.update { it.copy(email       = v, emailError   = null) }
    fun onPhoneChange(v: String)       = _state.update { it.copy(phone       = v) }
    fun onAnimalNameChange(v: String)  = _state.update { it.copy(animalName  = v) }
    fun onMessageChange(v: String)     = _state.update { it.copy(message     = v, messageError = null) }
    fun onContactTypeChange(t: ContactType) = _state.update { it.copy(contactType = t) }

    /** Valida e marca como enviado se tudo OK */
    fun submit() {
        val s = _state.value
        val nameErr    = if (s.name.isBlank()) "Informe seu nome" else null
        val emailErr   = when {
            s.email.isBlank() -> "Informe seu e-mail"
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> "E-mail inválido"
            else -> null
        }
        val messageErr = if (s.message.isBlank()) "Informe sua mensagem" else null

        if (nameErr != null || emailErr != null || messageErr != null) {
            _state.update { it.copy(nameError = nameErr, emailError = emailErr, messageError = messageErr) }
            return
        }
        _state.update { it.copy(isSubmitted = true) }
    }

    fun resetForm() = _state.update { ContactFormState() }
}
