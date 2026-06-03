package br.com.abrigosaolazaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.abrigosaolazaro.data.repository.AnimalRepository
import br.com.abrigosaolazaro.ui.navigation.AbrigoNavGraph
import br.com.abrigosaolazaro.ui.theme.AbrigoSaoLazaroTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        AnimalRepository((application as AbrigoApplication).database.animalDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AbrigoSaoLazaroTheme {
                AbrigoNavGraph(animalRepository = repository)
            }
        }
    }
}
