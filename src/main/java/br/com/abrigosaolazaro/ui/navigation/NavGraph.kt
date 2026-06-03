package br.com.abrigosaolazaro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.abrigosaolazaro.data.repository.AnimalRepository
import br.com.abrigosaolazaro.ui.screens.adoption.AdoptionScreen
import br.com.abrigosaolazaro.ui.screens.adoption.AdoptionViewModel
import br.com.abrigosaolazaro.ui.screens.contact.ContactScreen
import br.com.abrigosaolazaro.ui.screens.contact.ContactViewModel

/**
 * Rotas de navegação do aplicativo.
 */
sealed class Screen(val route: String) {
    object Adoption : Screen("adoption")
    object Contact  : Screen("contact?animalName={animalName}") {
        fun createRoute(animalName: String = "") = "contact?animalName=$animalName"
    }
}

/**
 * Grafo de navegação principal com Navigation Compose.
 * Cada tela tem seu próprio Scaffold (cabeçalho + conteúdo).
 */
@Composable
fun AbrigoNavGraph(animalRepository: AnimalRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Adoption.route
    ) {

        // ── Tela 1: Quero Adotar ──────────────────────────────────────
        composable(Screen.Adoption.route) {
            val viewModel: AdoptionViewModel = viewModel(
                factory = AdoptionViewModel.Factory(animalRepository)
            )
            AdoptionScreen(
                viewModel      = viewModel,
                onAdoptClick   = { name ->
                    navController.navigate(Screen.Contact.createRoute(name))
                },
                onContactClick = {
                    navController.navigate(Screen.Contact.createRoute())
                }
            )
        }

        // ── Tela 2: Contato / Denúncia ────────────────────────────────
        composable(
            route = Screen.Contact.route,
            arguments = listOf(
                navArgument("animalName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val animalName = backStackEntry.arguments?.getString("animalName") ?: ""
            val viewModel: ContactViewModel = viewModel()
            ContactScreen(
                viewModel           = viewModel,
                prefilledAnimalName = animalName,
                onBackClick         = { navController.popBackStack() }
            )
        }
    }
}
