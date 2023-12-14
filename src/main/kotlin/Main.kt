import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.gitlive.firebase.FirebaseApp
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }


}

@Composable
@Preview
private fun LoginOptions(viewModel: MainViewModel) {
    val authenticationResult by viewModel.authenticationResult.collectAsState(initial = null)

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
                        viewModel.loginWithCorrectPassword()
                    }
                },
            ) {
                Text(text = "Login with correct password")
            }


            Button(
                onClick = { scope.launch { viewModel.loginWithIncorrectPassword() } },
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Login with wrong password")
            }

            val authenticationResultText = authenticationResult?.let { result ->
                when (result) {
                    is AuthenticationResult.Success -> "Success"
                    is AuthenticationResult.Failure -> result.firebaseException.toString()
                }
            }.orEmpty()

            Text(
                text = authenticationResultText
            )
        }
    }
}

fun main() = application {
    val mainViewModel = remember { MainViewModel() }
    Window(onCloseRequest = ::exitApplication) {
        LoginOptions(mainViewModel)
    }
}
