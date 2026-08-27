package br.com.abrigosaolazaro.ui.screens.adoption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.abrigosaolazaro.data.db.AnimalEntity
import br.com.abrigosaolazaro.data.repository.AnimalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdoptionUiState(
    val animals: List<AnimalEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val showFavoritesOnly: Boolean = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AdoptionViewModel(private val repository: AnimalRepository) : ViewModel() {

    private val _searchQuery    = MutableStateFlow("")
    private val _favoritesOnly  = MutableStateFlow(false)
    private val _isLoading      = MutableStateFlow(true)

    val uiState: StateFlow<AdoptionUiState> = combine(
        _searchQuery,
        _favoritesOnly
    ) { query, favOnly -> query to favOnly }
        .debounce(300L)
        .flatMapLatest { (query, favOnly) ->
            when {
                favOnly        -> repository.favorites
                query.isBlank() -> repository.animals
                else           -> repository.search(query)
            }
        }
        .map { list ->
            AdoptionUiState(
                animals         = list,
                searchQuery     = _searchQuery.value,
                isLoading       = false,
                showFavoritesOnly = _favoritesOnly.value
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), AdoptionUiState())

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(q: String) { _searchQuery.value = q }
    fun toggleFavoritesFilter()       { _favoritesOnly.value = !_favoritesOnly.value }

    fun toggleFavorite(id: Long, current: Boolean) = viewModelScope.launch {
        repository.toggleFavorite(id, current)
    }

    fun recordView(id: Long) = viewModelScope.launch {
        repository.recordView(id)
    }

    /** DELETE: marca animal como adotado e remove do banco. */
    fun markAdopted(id: Long) = viewModelScope.launch {
        repository.markAdopted(id)
    }

    class Factory(private val repo: AnimalRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(cls: Class<T>): T =
            AdoptionViewModel(repo) as T
    }
}
