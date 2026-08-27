package br.com.abrigosaolazaro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ShelterHeader(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Logo circle: ícone de patinha como fundo; Coil carrega por cima
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
            // Corrigido: alignment = Alignment.CenterStart para focar no brasão
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://abrigosaolazaro.org.br/wp-content/uploads/2020/03/logo-abrigo-sao-lazaro.jpg")
                    .crossfade(true)
                    .build(),
                contentDescription = "Logo Abrigo São Lázaro",
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = "Abrigo São Lázaro",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
            Text(
                text = "Proteção e amor para todos os animais",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
