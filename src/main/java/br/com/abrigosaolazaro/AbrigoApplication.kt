package br.com.abrigosaolazaro

import android.app.Application
import br.com.abrigosaolazaro.data.db.AppDatabase

class AbrigoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}
