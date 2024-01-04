//import android.app.Application
@file:OptIn(DelicateCoroutinesApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.initKoin
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseClientBuilder
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.UploadData
import io.github.jan.supabase.storage.resumable.ResumableUpload
import io.github.jan.supabase.storage.resumable.SettingsResumableCache
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
//import dev.gitlive.firebase.FirebaseApp
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

lateinit var supabase: SupabaseClient
 var  progress =  mutableStateOf(false)

@Composable
@Preview
private fun LoginOptions() {
    val scope = rememberCoroutineScope()
    MaterialTheme {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = {
                    scope.launch {

                    }
                },
            ) {
                Text(text = "Login with correct password")
            }


            Button(
                onClick = {
                    progress.value = true
                    scope.launch {
                        val f = File("/Users/jahongirmirzotolqinov/Downloads/", "qra.png").readBytes()
                        uploadData(f)
                    }
                },
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Login with wrong password")
            }
            if (progress.value){
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    color = Color.Green,
                    strokeWidth = 10.dp)

            }
        }
    }
}

@Preview
@Composable
fun App(){
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fayllarni tanlang!")
        }
    }
}

suspend fun uploadData(bytes: ByteArray) {
    val upload: ResumableUpload = supabase.storage.from("test")
        .resumable.createOrContinueUpload(bytes, "", "21/n1.png")

    upload.stateFlow
        .onEach {
            if (it.isDone) progress.value = false
        }
        .launchIn(CoroutineScope(GlobalScope.coroutineContext))
    upload.startOrResumeUploading()
}

@Composable
private fun CustomCircularProgressBar(){
    CircularProgressIndicator(
        modifier = Modifier.size(100.dp),
        color = Color.Green,
        strokeWidth = 10.dp)

}

fun main() = application {
    mainData()
//    initKoin()
    Window(onCloseRequest = ::exitApplication, title = "YourNews") {

//        LoginOptions()
        App()

    }

}

fun mainData() {
    supabase = createSupabaseClient(
        supabaseUrl = "https://dqoixoqoxdpxtowuxnke.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRxb2l4b3FveGRweHRvd3V4bmtlIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTcwMjk4Mjg1NiwiZXhwIjoyMDE4NTU4ODU2fQ.n2tuO5s4PNTQkfAbF_VtrXxZBLDUtZtgUEqfMTcJh6w"
    ) {
        install(Auth)
        install(Storage) {
            resumable {
                cache = SettingsResumableCache()
            }
        }
        install(Postgrest)
        //install other modules
    }

}
