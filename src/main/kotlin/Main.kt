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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.flow.*
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
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

lateinit var supabase: SupabaseClient
var progress = mutableStateOf(false)


@Composable
fun <T> Flow<T>.collectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext, block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block).flowOn(context).launchIn(this)
    }
}

var imgSrc: File? = null

@Preview
@Composable
fun App(window: ComposeWindow) {

    val scope = rememberCoroutineScope()
    var isFileChooserOpen by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    var serverPath by remember { mutableStateOf("") }
    var text by remember { mutableStateOf(TextFieldValue("")) }

    var slc by remember { mutableStateOf(false) }


//    var imgSrc by remember { mutableStateOf(File("")) }


    val density = LocalDensity.current

    val fileSystemView = FileSystemView.getFileSystemView()
    // Get the Documents folder
    val documentsDir  by remember { mutableStateOf(JFileChooser()) }

    var path by remember { mutableStateOf("") }
    var img_path by remember { mutableStateOf(HttpStatusCode(404, "not found")) }
    val dialogState = remember { mutableStateOf(false) }


    val array = ArrayList<File>()

    if (isFileChooserOpen) {
        openFileDialog(
            window, "sdsd", allowMultiSelection = true, allowedExtensions = listOf("pdf", "png")
        ).forEach { f ->
            array.add(f)
            files = array
        }
        isFileChooserOpen = false
    }

    MaterialTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(15.dp)) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray).clickable {
                        if (text.text.isNotEmpty()) {
                            isFileChooserOpen = true
                            slc = false
                        } else {
                            dialogState.value = true
                            openDialog = true
                        }
                    }
            ) {
                FlowColumn(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f).horizontalScroll(
                        rememberScrollState()
                    ), horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {

                    repeat(files.size) { k ->
                        Row(
                            modifier = Modifier.clip(RoundedCornerShape(15.dp)).padding(2.dp)
                                .background(Color.Blue.copy(alpha = 0.4f)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${files[k].name} ", modifier = Modifier.padding(8.dp), color = Color.White)
                            AsyncImage(
                                load = {
                                    loadSvgPainter(
                                        File("/Users/jahongirmirzotolqinov/Documents/idea/desktop/desktop/src/main/resources/image/cross.svg"),
                                        density
                                    )
                                },
                                painterFor = { remember { it } },
                                contentDescription = "Compose logo",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.width(20.dp).padding(1.dp).clickable {
                                    val list = files.toMutableList()
                                    list.removeAt(k)
                                    files = list
                                }
                            )
                        }
                    }
                }

                if (files.isEmpty()) {
                    Column(
                        modifier = Modifier.clip(
                            RoundedCornerShape(20.dp),
                        ).border(1.dp, Color.Gray, shape = RoundedCornerShape(20.dp)).padding(
                            horizontal = 50.dp, vertical = 5.dp
                        )
                    ) {
                        AsyncImage(
                            load = {
                                loadSvgPainter(
                                    File("/Users/jahongirmirzotolqinov/Documents/idea/desktop/desktop/src/main/resources/image/add.svg"),
                                    density
                                )
                            },
                            painterFor = { remember { it } },
                            contentDescription = "Compose logo",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.width(100.dp)
                        )
                        Text("Fayl tanlash")
                    }
                }

                Box(modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)) {
                    if (progress.value) {
                        LimitedTrigger(modifier = Modifier.size(60.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(vertical = 15.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                    Box(modifier = Modifier.weight(0.2f)) {
                        Button(
                            onClick = {
                                scope.launch {
//                                    documentsDir = JFileChooser()
                                    documentsDir.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                    documentsDir.showSaveDialog(null)
                                    if (files.isNotEmpty() && documentsDir.currentDirectory.canWrite()) {
                                        progress.value = true
                                        scope.launch {
                                            if (text.text != "") {
                                                path = mergeZip(files, "${documentsDir.currentDirectory}/${text.text}.zip")
                                            }
                                        }
                                    }
                                    delay(5000)
                                    println("path:$path")
                                    if (path != "") {
                                        try {
                                            println("path if found:$path")
                                            val f = File(path).readBytes()
//                                        serverPath = uploadData(f, path)

                                            val formatter = SimpleDateFormat("yyyy-MM-dd")
                                            val date = Date()
                                            val current = formatter.format(date)
                                            val date_time = LocalDateTime.now()
                                            val file_path = "$current/files/${date_time}_merge.zip"
                                            val upload: ResumableUpload =
                                                supabase.storage.from("test").resumable.createOrContinueUpload(
                                                    f,
                                                    "",
                                                    file_path
                                                )

                                            upload.stateFlow.onEach {
                                                if (it.isDone){
                                                    progress.value = false
                                                    serverPath = file_path
                                                }
                                            }.launchIn(CoroutineScope(GlobalScope.coroutineContext))
                                            upload.startOrResumeUploading()
                                        } catch (e: Exception) {
                                            println(e.message)
                                        }
                                    }
                                }
                            }, modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Yuklash")
                        }
                    }
                    Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.Center) {
                        TextField(
                            value = text, onValueChange = { newText ->
                                text = newText
                            }
                        )
                    }
                }
                Row {
                    Box(modifier = Modifier.weight(0.2f)) {
                        Button(onClick = {
                            scope.launch {
                                try {
                                    println("ss $serverPath")
                                    if (serverPath.isNotEmpty()) {
                                        img_path = NewsApiClient.createQR(
                                            "https://dqoixoqoxdpxtowuxnke.supabase.co/storage/v1/object/public/test/${serverPath}",
                                            200,
                                            text.text,
                                            documentsDir.currentDirectory.path
                                        )
                                    }
                                } catch (e: Exception) {
                                    println(e.message)
                                }
                                slc = true
                            }
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("QR kod yaratish")
                        }
                    }
                    Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.background(Color.LightGray)) {
                            if (slc) {
                                AsyncImage(
                                    load = { loadImageBitmap(File("${documentsDir.currentDirectory}/${text.text}.png")) },
                                    painterFor = { remember { BitmapPainter(it) } },
                                    contentDescription = "Compose logo",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.width(200.dp)
                                )
                            }
                        }
                    }
                }
                if (slc) {
                    Button(onClick = {
//                printImage("${documentsDir.path}/aa.png")
                        printQR(File("${documentsDir.currentDirectory}/${text.text}.png"))
                    }) {
                        Text("print")
                    }
                }
            }

            if (openDialog) {
                if (dialogState.value) {
                    AlertDialog(
                        onDismissRequest = {},
                        confirmButton = {
                            Button(onClick = {
                                dialogState.value = false
                            }) {
                                Text("Yopish")
                            }
                        },
                        text = { Text("Avval arxiv va qr kod uchun nom kiriting", color = Color.Black) })
                }
            }
        }
    }
}

@Composable
fun changeQr(file: File) {
    AsyncImage(
        load = { loadImageBitmap(file) },
        painterFor = { remember { BitmapPainter(it) } },
        contentDescription = "Compose logo",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.width(200.dp)
    )
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
    val upload: ResumableUpload =
        supabase.storage.from("test").resumable.createOrContinueUpload(bytes, "", file_path)

    upload.stateFlow.onEach {
        if (it.isDone) progress.value = false
    }.launchIn(CoroutineScope(GlobalScope.coroutineContext))
    upload.startOrResumeUploading()
    return file_path
}

@Composable
private fun AlertDialog() {
    val dialogState = remember { mutableStateOf(false) }
    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = { dialogState.value = false },
            confirmButton = {},
            text = { Text("Avval arxiv va qr kod uchun nom kiriting", color = Color.Black) })
    }
}

var iconIndex = mutableStateOf(1)
fun main() = application {
    mainData()
    Window(onCloseRequest = ::exitApplication, title = "F.A.H.R") {
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


@Composable
fun LimitedTrigger(modifier: Modifier) {

    CircularProgressIndicator(modifier = modifier)
}

//@Composable
//fun MyIndicator(indicatorProgress: Float) {
//    var progress by animateFloatAsState(targetValue = ) // Progress value (0.0 to 1.0)
//    if((progress * 10000) <= indicatorProgress)
//    DisposableEffect(Unit) { // DisposableEffect for cleanup
//        val handle = CoroutineScope(Dispatchers.IO).launch { // Launch coroutine in IO dispatcher
//            while ( true ) {
//                    delay(1000) // Update progress every second
//                    progress = (progress + 0.1f) % 1.0f // Increment progress (capped at 1.0)
//                if ((progress * 10000) == indicatorProgress){
//                    break
//                }
//            }
//        }
//        onDispose { // Cleanup when composable disposes
//            handle.cancel()
//        }
//    }
//
//    println("tn : $progress")
//
//    LinearProgressIndicator(progress = (progress), modifier = Modifier.fillMaxWidth())
//}


fun selectPrintService(printServices: Array<PrintService>): PrintService? {
    val selectedPrintService = ServiceUI.printDialog(null, 50, 50, printServices, printServices[0], null, null)
    return selectedPrintService
}




