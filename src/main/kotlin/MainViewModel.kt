import androidx.compose.runtime.rememberCoroutineScope
//import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

//class MainViewModel : ViewModel() {
//    var new : NewsApiClient = NewsApiClient
//    private val email = "example@gmail.com"
//    private val password = "123456"
//    val auth = FirebaseAuth.getInstance()
//
//    val authenticationResult = MutableSharedFlow<AuthenticationResult>()
//
//    suspend fun add(){
//        new.postRequest()
//    }
//
//}
//sealed interface AuthenticationResult {
//    object Success : AuthenticationResult
//
//    @JvmInline
//    value class Failure(val firebaseException: FirebaseException) : AuthenticationResult
//}