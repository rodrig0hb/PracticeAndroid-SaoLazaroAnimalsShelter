package br.com.abrigosaolazaro.ui.screens.adoption

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.abrigosaolazaro.data.db.AnimalEntity
import br.com.abrigosaolazaro.ui.components.ShelterHeader
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AdoptionScreen(
    viewModel: AdoptionViewModel,
    onAdoptClick: (animalName: String) -> Unit,
    onContactClick: () -> Unit
) {
    val animals by viewModel.animals.collectAsState()

    Scaffold(
        topBar = { ShelterHeader() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick          = onContactClick,
                icon             = { Icon(Icons.Default.Email, contentDescription = null) },
                text             = { Text("Contato / Denúncia") },
                containerColor   = MaterialTheme.colorScheme.secondary,
                contentColor     = MaterialTheme.colorScheme.onSecondary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Título da seção
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text  = "Animais para Adoção",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (animals.isEmpty()) {
                // Carregando do Room
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Carregando animais...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns             = GridCells.Fixed(2),
                    contentPadding      = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    modifier            = Modifier.fillMaxSize()
                ) {
                    items(animals, key = { it.id }) { animal ->
                        AnimalCard(
                            animal       = animal,
                            onAdoptClick = { onAdoptClick(animal.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimalCard(
    animal: AnimalEntity,
    onAdoptClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Foto do animal (Coil)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(animal.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${animal.name} – foto",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text     = animal.name,
                    style    = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = "${animal.species} • ${animal.age}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick         = onAdoptClick,
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(8.dp),
                    contentPadding  = PaddingValues(vertical = 6.dp)
                ) {
                    Text(text = "Adotar 🐾", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
