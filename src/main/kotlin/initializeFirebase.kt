import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


object FirebaseService {
    private val database: DatabaseReference by lazy {
        // Initialize FirebaseApp if not already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            val options = FirebaseOptions.builder().setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl("https://testdesktop-bf282-default-rtdb.firebaseio.com/message_list.json") // Replace with your Firebase project URL
                .build()

            FirebaseApp.initializeApp(options)
        }

        // Return the default database reference
        FirebaseDatabase.getInstance().reference
    }

    fun saveData(data: String) {
        // Assuming you have a "data" node in your Firebase Realtime Database
        val dataRef = database.child("data").push()

        // Save data to Firebase
        dataRef.setValue(data, DatabaseReference.CompletionListener { databaseError, databaseReference ->
            println("Data saved to Firebase: $data")
            println("Error saving data to Firebase: ${databaseError.message}")
        })
    }
}