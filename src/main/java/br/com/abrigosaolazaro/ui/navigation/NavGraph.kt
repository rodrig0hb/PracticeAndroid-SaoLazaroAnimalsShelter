package br.com.abrigosaolazaro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.abrigosaolazaro.data.repository.AnimalRepository
import br.com.abrigosaolazaro.data.repository.ContactRepository
import br.com.abrigosaolazaro.data.repository.RouteRepository
import br.com.abrigosaolazaro.ui.screens.adoption.AdoptionScreen
import br.com.abrigosaolazaro.ui.screens.adoption.AdoptionViewModel
import br.com.abrigosaolazaro.ui.screens.contact.ContactScreen
import br.com.abrigosaolazaro.ui.screens.contact.ContactViewModel
import br.com.abrigosaolazaro.ui.screens.location.LocationScreen
import br.com.abrigosaolazaro.ui.screens.location.LocationViewModel

sealed class Screen(val route: String) {
    object Adoption : Screen("adoption")
    object Contact  : Screen("contact?animalName={animalName}") {
        fun createRoute(animalName: String = "") = "contact?animalName=$animalName"
    }
    object Location : Screen("location")
}

@Composable
fun AbrigoNavGraph(animalRepository: AnimalRepository) {
    val navController   = rememberNavController()
    val routeRepository = RouteRepository()
    val contactRepo     = ContactRepository()

    NavHost(navController = navController, startDestination = Screen.Adoption.route) {

        // ── Tela 1 – Quero Adotar ─────────────────────────────────────
        composable(Screen.Adoption.route) {
            val vm: AdoptionViewModel = viewModel(
                factory = AdoptionViewModel.Factory(animalRepository)
            )
            AdoptionScreen(
                viewModel      = vm,
                onAdoptClick   = { name -> navController.navigate(Screen.Contact.createRoute(name)) },
                onContactClick = { navController.navigate(Screen.Contact.createRoute()) },
                onLocationClick = { navController.navigate(Screen.Location.route) }
            )
        }

        // ── Tela 2 – Contato / Denúncia ───────────────────────────────
        composable(
            route = Screen.Contact.route,
            arguments = listOf(navArgument("animalName") {
                type = NavType.StringType; defaultValue = ""
            })
        ) { back ->
            val animalName = back.arguments?.getString("animalName") ?: ""
            val vm: ContactViewModel = viewModel(
                factory = ContactViewModel.Factory(contactRepo)
            )
            ContactScreen(
                viewModel           = vm,
                prefilledAnimalName = animalName,
                onBackClick         = { navController.popBackStack() },
                onLocationClick     = { navController.navigate(Screen.Location.route) }
            )
        }

        // ── Tela 3 – Localização / Mapa ──────────────────────────────
        composable(Screen.Location.route) {
            val vm: LocationViewModel = viewModel(
                factory = LocationViewModel.Factory(routeRepository)
            )
            LocationScreen(
                viewModel   = vm,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
