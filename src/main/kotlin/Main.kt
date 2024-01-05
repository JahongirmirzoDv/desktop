//import android.app.Application
@file:OptIn(DelicateCoroutinesApi::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)

//import dev.gitlive.firebase.FirebaseApp
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.resumable.ResumableUpload
import io.github.jan.supabase.storage.resumable.SettingsResumableCache
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import javax.swing.filechooser.FileSystemView


lateinit var supabase: SupabaseClient
var progress = mutableStateOf(false)

@Composable
@Preview
private fun LoginOptions() {
    val scope = rememberCoroutineScope()
    MaterialTheme {

        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f).systemBarsPadding().padding(horizontal = 16.dp),
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
                }, colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Login with wrong password")
            }
            if (progress.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp), color = Color.Green, strokeWidth = 10.dp
                )

            }
        }
    }
}

@Preview
@Composable
fun App(window: ComposeWindow) {

    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("Hello, World!") }
    var isFileChooserOpen by remember { mutableStateOf(false) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }

    val fileSystemView = FileSystemView.getFileSystemView()


    // Get the Documents folder
    val documentsDir = fileSystemView.defaultDirectory

    val array = ArrayList<File>()

    if (isFileChooserOpen) {
        openFileDialog(
            window, "sdsd", allowMultiSelection = true, allowedExtensions = listOf("pdf", "png")
        ).forEach { f ->
            array.add(f)
            files = array
        }
        isFileChooserOpen = false

        if (files.isNotEmpty()){
            scope.launch {
                val path = "${documentsDir.path}/${LocalDateTime.now()}_merge.zip"
                Thread.sleep(5000)
                mergeZip(files, path)
            }
        }
    }

    MaterialTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(15.dp), horizontalArrangement = Arrangement.Center
            ) {
                Text("Fayllarni tanlang!", color = Color.Black, style = TextStyle(fontSize = 30.sp))
            }
            FlowColumn(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f).background(Color.LightGray)
                , horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(files.size) { k ->
                    Text("${files[k].name} ")
                }

            }
            Button(onClick = {
                isFileChooserOpen = true
            }) {
                Text("Tanlash")
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FileDialog(
    parent: Frame? = null, onCloseRequest: (result: String?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(file)
                }
            }
        }
    }, dispose = FileDialog::dispose
)

fun openFileDialog(
    window: ComposeWindow, title: String, allowedExtensions: List<String>, allowMultiSelection: Boolean = true
): Set<File> {
    return FileDialog(window, title, FileDialog.LOAD).apply {
        isMultipleMode = allowMultiSelection

        // windows
        file = allowedExtensions.joinToString(";") { "*$it" } // e.g. '*.jpg'

        // linux
        setFilenameFilter { _, name ->
            allowedExtensions.any {
                name.endsWith(it)
            }
        }

        isVisible = true
        dispose()
    }.files.toSet()
}

suspend fun uploadData(bytes: ByteArray) {
    val upload: ResumableUpload = supabase.storage.from("test").resumable.createOrContinueUpload(bytes, "", "21/n1.png")

    upload.stateFlow.onEach {
        if (it.isDone) progress.value = false
    }.launchIn(CoroutineScope(GlobalScope.coroutineContext))
    upload.startOrResumeUploading()
}

@Composable
private fun CustomCircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier.size(100.dp), color = Color.Green, strokeWidth = 10.dp
    )

}

fun main() = application {
    mainData()
//    initKoin()
    Window(onCloseRequest = ::exitApplication, title = "YourNews") {

//        LoginOptions()
        App(window)

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
