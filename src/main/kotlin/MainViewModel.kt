import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val email = "example@gmail.com"
    private val password = "123456"
    val auth = FirebaseAuth.getInstance()

    val authenticationResult = MutableSharedFlow<AuthenticationResult>()

    suspend fun loginWithCorrectPassword() {
        try {
            auth.signInWithEmailAndPassword(
                email = email, password = password
            )
            auth.signOut()
            authenticationResult.emit(AuthenticationResult.Success)
        } catch (firebaseException: FirebaseException) {
            authenticationResult.emit(AuthenticationResult.Failure(firebaseException))
        }
    }


        suspend fun loginWithIncorrectPassword() {
            try {
                auth.signInWithEmailAndPassword(
                    email = email, password = password.plus("wrong")
                )
            } catch (firebaseException: FirebaseException) {
                authenticationResult.emit(AuthenticationResult.Failure(firebaseException))
            }
        }
    }

sealed interface AuthenticationResult {
    object Success : AuthenticationResult

    @JvmInline
    value class Failure(val firebaseException: FirebaseException) : AuthenticationResult
}