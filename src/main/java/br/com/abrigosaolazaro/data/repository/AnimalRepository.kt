package br.com.abrigosaolazaro.data.repository

import br.com.abrigosaolazaro.data.db.AnimalDao
import br.com.abrigosaolazaro.data.db.AnimalEntity
import kotlinx.coroutines.flow.Flow

class AnimalRepository(private val dao: AnimalDao) {

    /** Stream of available animals from Room */
    val animals: Flow<List<AnimalEntity>> = dao.getAvailableAnimals()

    /** Populate with sample data if DB is empty (first launch) */
    suspend fun seedIfEmpty() {
        if (dao.count() == 0) dao.insertAll(sampleAnimals())
    }

    private fun sampleAnimals(): List<AnimalEntity> = listOf(
        AnimalEntity(
            name = "Thor",
            age = "2 anos",
            species = "Cachorro",
            breed = "Vira-lata caramelo",
            imageUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400&q=80&fit=crop",
            description = "Thor é muito carinhoso e brincalhão. Adora crianças e convive bem com outros animais. Vacinado e castrado."
        ),
        AnimalEntity(
            name = "Luna",
            age = "1 ano",
            species = "Gata",
            breed = "SRD",
            imageUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400&q=80&fit=crop",
            description = "Luna é tranquila e muito afetuosa. Ideal para apartamentos. Vacinada e castrada."
        ),
        AnimalEntity(
            name = "Rex",
            age = "3 anos",
            species = "Cachorro",
            breed = "Labrador mix",
            imageUrl = "https://images.unsplash.com/photo-1561037404-61cd46aa615b?w=400&q=80&fit=crop",
            description = "Rex é leal e protetor. Ótimo para famílias. Já está castrado e vacinado em dia."
        ),
        AnimalEntity(
            name = "Mel",
            age = "6 meses",
            species = "Gata",
            breed = "SRD",
            imageUrl = "https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=400&q=80&fit=crop",
            description = "Mel é um filhote cheio de energia e curiosidade, precisa de muito carinho."
        ),
        AnimalEntity(
            name = "Buddy",
            age = "4 anos",
            species = "Cachorro",
            breed = "Beagle mix",
            imageUrl = "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400&q=80&fit=crop",
            description = "Buddy é calmo e paciente, ideal para famílias com crianças. Adora passeios."
        ),
        AnimalEntity(
            name = "Mia",
            age = "2 anos",
            species = "Gata",
            breed = "SRD",
            imageUrl = "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400&q=80&fit=crop",
            description = "Mia é independente, mas muito carinhosa com quem conquista sua confiança."
        ),
        AnimalEntity(
            name = "Bob",
            age = "5 anos",
            species = "Cachorro",
            breed = "SRD",
            imageUrl = "https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?w=400&q=80&fit=crop",
            description = "Bob está há mais tempo no abrigo e tem muito amor a dar. Precisa de um lar!"
        ),
        AnimalEntity(
            name = "Nina",
            age = "8 meses",
            species = "Gata",
            breed = "SRD",
            imageUrl = "https://images.unsplash.com/photo-1495360010541-f48722b34f7d?w=400&q=80&fit=crop",
            description = "Nina é brincalhona e sociável. Adapta-se bem a outros animais e crianças."
        )
    )
}
