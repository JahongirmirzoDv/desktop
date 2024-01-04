package di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(additionalConfiguration: KoinApplication.() -> Unit = {}) {
    startKoin {
        modules(supabaseModule)
        additionalConfiguration()
    }
}