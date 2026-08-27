package br.com.abrigosaolazaro.ui.screens.adoption

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.abrigosaolazaro.data.db.AnimalEntity
import br.com.abrigosaolazaro.ui.components.ShelterHeader
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionScreen(
    viewModel: AdoptionViewModel,
    onAdoptClick: (String) -> Unit,
    onContactClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                ShelterHeader()
                // Search bar
                SearchBar(
                    query         = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    favOnly       = state.showFavoritesOnly,
                    onFavToggle   = viewModel::toggleFavoritesFilter
                )
            }
        },
        bottomBar = {
            BottomNavBar(
                onContactClick  = onContactClick,
                onLocationClick = onLocationClick
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = MaterialTheme.colorScheme.primary
                )
            } else if (state.animals.isEmpty()) {
                EmptyState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns               = GridCells.Fixed(2),
                    contentPadding        = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.animals, key = { it.id }) { animal ->
                        AnimalCard(
                            animal         = animal,
                            onAdoptClick   = { onAdoptClick(animal.name) },
                            onFavClick     = { viewModel.toggleFavorite(animal.id, animal.isFavorite) },
                            onExpand       = { viewModel.recordView(animal.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    favOnly: Boolean,
    onFavToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value         = query,
            onValueChange = onQueryChange,
            modifier      = Modifier.weight(1f),
            placeholder   = { Text("Buscar animal...") },
            leadingIcon   = { Icon(Icons.Default.Search, null) },
            trailingIcon  = {
                if (query.isNotBlank())
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, null)
                    }
            },
            singleLine = true,
            shape      = RoundedCornerShape(12.dp)
        )
        FilterChip(
            selected = favOnly,
            onClick  = onFavToggle,
            label    = { Icon(Icons.Default.Favorite, null, modifier = Modifier.size(18.dp)) }
        )
    }
}

@Composable
private fun BottomNavBar(onContactClick: () -> Unit, onLocationClick: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        NavigationBarItem(
            selected = true,
            onClick  = {},
            icon     = { Icon(Icons.Default.Pets, null) },
            label    = { Text("Adotar") }
        )
        NavigationBarItem(
            selected = false,
            onClick  = onContactClick,
            icon     = { Icon(Icons.Default.Email, null) },
            label    = { Text("Contato") }
        )
        NavigationBarItem(
            selected = false,
            onClick  = onLocationClick,
            icon     = { Icon(Icons.Default.LocationOn, null) },
            label    = { Text("Localização") }
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        Spacer(Modifier.height(12.dp))
        Text("Nenhum animal encontrado", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}

@Composable
private fun AnimalCard(
    animal: AnimalEntity,
    onAdoptClick: () -> Unit,
    onFavClick: () -> Unit,
    onExpand: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(animal.imageUrl).crossfade(true).build(),
                    contentDescription = animal.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                // Ícone
                IconButton(
                    onClick  = onFavClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (animal.isFavorite) Icons.Default.Favorite
                                      else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (animal.isFavorite) Color.Red else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(animal.name, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${animal.species} • ${animal.age}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                // Descrição
                AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
                    Text(animal.description, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp))
                }

                TextButton(
                    onClick = { expanded = !expanded; if (!expanded) onExpand() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (expanded) "Menos ▲" else "Mais ▼",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = onAdoptClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Adotar 🐾", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
