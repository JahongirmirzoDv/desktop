//import android.app.Application
@file:OptIn(DelicateCoroutinesApi::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)

//import dev.gitlive.firebase.FirebaseApp


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
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
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.*
import java.awt.FileDialog
import java.awt.print.PrinterJob
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.MediaSizeName
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

                }, colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Login with wrong password")
            }

        }
    }
}

@Preview
@Composable
fun App(window: ComposeWindow) {

    val scope = rememberCoroutineScope()
    var isFileChooserOpen by remember { mutableStateOf(false) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    var serverPath by remember { mutableStateOf("") }
    var text by remember { mutableStateOf(TextFieldValue("")) }


    val density = LocalDensity.current

    val fileSystemView = FileSystemView.getFileSystemView()
    // Get the Documents folder
    val documentsDir = fileSystemView.defaultDirectory

    var path by remember { mutableStateOf("") }
    var img_path by remember { mutableStateOf(HttpStatusCode(404, "not found")) }


    val array = ArrayList<File>()

    if (isFileChooserOpen) {
        openFileDialog(
            window, "sdsd", allowMultiSelection = true, allowedExtensions = listOf("pdf", "png")
        ).forEach { f ->
            array.add(f)
            files = array
        }
        isFileChooserOpen = false

        if (files.isNotEmpty()) {
            scope.launch {
                if (text.text != ""){
                    progress.value = true
                    delay(5000)
                    path = mergeZip(files, "${documentsDir.path}/${text.text}.zip")
                    progress.value = false
                }
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
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f).background(Color.LightGray).horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                repeat(files.size) { k ->
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(15.dp)).padding(2.dp)
                            .background(Color.Blue.copy(alpha = 0.4f))
                    ) {
                        Text("${files[k].name} ", modifier = Modifier.padding(8.dp), color = Color.White)
                    }
                }

            }


            Button(onClick = {
                isFileChooserOpen = true
            }) {
                Text("Tanlash")
            }
            Button(onClick = {
                scope.launch {
                    println("path:$path")
                    if (path != ""){
                        progress.value = true
                        val f = File(path).readBytes()
                        serverPath = uploadData(f, path)
                    }
                }
            }) {
                Text("Yuklash")
            }
            Button(onClick = {
                scope.launch {
                    println("ss $serverPath")

                    if (serverPath.isNotEmpty()) {
                        img_path = NewsApiClient.createQR(
                            "https://dqoixoqoxdpxtowuxnke.supabase.co/storage/v1/object/public/test/${serverPath}",
                            200,
                            "aa"
                        )
                    }
                }
            }) {
                Text("qr kod yaratish")
            }

            if (img_path.isSuccess()) {
                showQr(File("${documentsDir.path}/aa.png"))
            }

            Button(onClick = {
//                printImage("${documentsDir.path}/aa.png")
                printQR(File("${documentsDir.path}/aa.png"))
            }) {
                Text("print")
            }
            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                }, modifier = Modifier
            )

            if (progress.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp), color = Color.Green, strokeWidth = 10.dp
                )

            }
        }


    }
}

@Composable
fun showQr(file: File) {
    AsyncImage(
        load = { loadImageBitmap(file) },
        painterFor = { remember { BitmapPainter(it) } },
        contentDescription = "Compose logo",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.width(200.dp)
    )
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

suspend fun uploadData(bytes: ByteArray, path: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val date = Date()
    val current = formatter.format(date)
    val date_time = LocalDateTime.now()
    val file_path = "$current/files/${date_time}_merge.zip"
    val upload: ResumableUpload = supabase.storage.from("test").resumable.createOrContinueUpload(bytes, "", file_path)

    upload.stateFlow.onEach {
        if (it.isDone) progress.value = false
    }.launchIn(CoroutineScope(GlobalScope.coroutineContext))
    upload.startOrResumeUploading()
    return file_path
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


//fun printQR(file: File) {
//
//    if (Desktop.isDesktopSupported()) {
//        val desktop = Desktop.getDesktop()
//        try {
//            desktop.print(file)
//        } catch (e: PrinterException) {
//            e.printStackTrace()
//        }
//    }
//}


fun printImage(imagePath: String) {
    try {
        val image = ImageIO.read(File(imagePath))
        val printerJob = PrinterJob.getPrinterJob()
        printerJob.setPrintable(PrintImagePage(image))
        if (printerJob.printDialog()) {
            printerJob.print()
        }
    } catch (e: Exception) {
        // Handle exceptions appropriately
        println("Error printing image: ${e.message}")
    }
}

fun printQR(file: File) {
    if (file.name != "") {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
            try {
                val printJob = PrintServiceLookup.lookupDefaultPrintService()?.createPrintJob()
                val printRequestAttributeSet = HashPrintRequestAttributeSet()
                printRequestAttributeSet.add(MediaSizeName.ISO_A4)

                printJob?.let { job ->
                    file.inputStream().use { inputStream ->
                        val doc = SimpleDoc(inputStream, DocFlavor.INPUT_STREAM.AUTOSENSE, null)
                        job.print(doc, printRequestAttributeSet)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Printing is not supported on this system.")
        }
    }
}


//@Composable
//fun TextField(
//    value: TextFieldValue,
//    onValueChange: (TextFieldValue) -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    readOnly: Boolean = false,
//    textStyle: TextStyle = LocalTextStyle.current,
//    label: @Composable (() -> Unit)? = null,
//    placeholder: @Composable (() -> Unit)? = null,
//    leadingIcon: @Composable (() -> Unit)? = null,
//    trailingIcon: @Composable (() -> Unit)? = null,
//    isError: Boolean = false,
//    visualTransformation: VisualTransformation = VisualTransformation.None,
//    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
//    keyboardActions: KeyboardActions = KeyboardActions(),
//    singleLine: Boolean = false,
//    maxLines: Int = Int.MAX_VALUE,
//    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
//    shape: CornerBasedShape =
//        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
//    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
//) {
//}


fun selectPrintService(printServices: Array<PrintService>): PrintService? {
    val selectedPrintService = ServiceUI.printDialog(null, 50, 50, printServices, printServices[0], null, null)
    return selectedPrintService
}
