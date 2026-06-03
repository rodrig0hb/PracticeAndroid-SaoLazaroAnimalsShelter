package br.com.abrigosaolazaro.ui.screens.adoption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.abrigosaolazaro.data.db.AnimalEntity
import br.com.abrigosaolazaro.data.repository.AnimalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdoptionViewModel(private val repository: AnimalRepository) : ViewModel() {

    /** Lista de animais disponíveis, exposta como StateFlow para a UI */
    val animals: StateFlow<List<AnimalEntity>> = repository.animals
        .stateIn(
            scope   = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    init {
        // Popula o banco com dados de exemplo na primeira execução
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    // ── ViewModelFactory sem Hilt / DI framework ──────────────────────
    class Factory(private val repository: AnimalRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdoptionViewModel::class.java))
                return AdoptionViewModel(repository) as T
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
