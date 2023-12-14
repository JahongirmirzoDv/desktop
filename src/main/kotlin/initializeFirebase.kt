
import com.google.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import java.io.FileInputStream

fun initializeFirebase() {
    val serviceAccount = FileInputStream("path/to/your/serviceAccountKey.json")

    val options = com.google.firebase.FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://your-firebase-database-url.firebaseio.com")
        .build()

    FirebaseApp.initializeApp(options)
}