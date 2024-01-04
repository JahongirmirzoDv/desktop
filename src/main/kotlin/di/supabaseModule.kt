package di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.resumable.SettingsResumableCache
import io.github.jan.supabase.storage.storage
import org.koin.dsl.module

const val BUCKET = "test"

@OptIn(SupabaseExperimental::class)
val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://dqoixoqoxdpxtowuxnke.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRxb2l4b3FveGRweHRvd3V4bmtlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDI5ODI4NTYsImV4cCI6MjAxODU1ODg1Nn0.241WpeIsjLySnAqROpt951OtOns8cpwAd--_IilF2KY"
        ) {
            install(Storage) {
                resumable {
                    cache = SettingsResumableCache()
                }
            }
        }
    }
    single {
        get<SupabaseClient>().storage[BUCKET].resumable
    }
}