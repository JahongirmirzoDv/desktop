//import android.app.Application
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.GlobalScope
//import dev.gitlive.firebase.FirebaseApp
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    LoginOptions()
}

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
//                        viewModel.add()
//                        viewModel.loginWithCorrectPassword()
                        scope.launch {
                            FirebaseService.saveData("salom")
                        }
                    }
                },
            ) {
                Text(text = "Login with correct password")
            }


            Button(
                onClick = {
//                    scope.launch { viewModel.loginWithIncorrectPassword() }
                },
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Login with wrong password")
            }


        }
    }
}

fun main() = application {

    val keyFilePath = "testdesktop-bf282-firebase-adminsdk-qo7r8-315cb58a2b.json"
    System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", keyFilePath)

    Window(onCloseRequest = ::exitApplication, title = "YourNews") {

        LoginOptions()




        LoggerFactory.getLogger("Main").info("GOOGLE_APPLICATION_CREDENTIALS: ${System.getenv("GOOGLE_APPLICATION_CREDENTIALS")}")
        ("GOOGLE_APPLICATION_CREDENTIALS: ${System.getenv("GOOGLE_APPLICATION_CREDENTIALS")}")
    }

}

suspend fun mainData(applicationContext: io.grpc.Context) {
    val client = Client(applicationContext.toString())
        .setEndpoint("https://cloud.appwrite.io/v1") // Your API Endpoint
        .setProject("65809c047af30991f306") // Your project ID

    val storage = Storage(client)

    val file = storage.createFile(
        bucketId = "65809daaf22ac5a80281",
        fileId = ID.unique(),
        file = InputFile.fromFile(File("/Users/jahongirmirzotolqinov/Documents/idea/desktop/desktop/google-services.json"))
    )
}
