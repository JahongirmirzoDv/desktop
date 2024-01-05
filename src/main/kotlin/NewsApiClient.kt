import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import java.io.File
import java.io.FileOutputStream
import javax.swing.filechooser.FileSystemView

object NewsApiClient {
    val fileSystemView = FileSystemView.getFileSystemView()
    // Get the Documents folder
    val documentsDir = fileSystemView.defaultDirectory
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun createQR(data:String,size:Int,name:String): String {
        val img_path = "${documentsDir.path}/${name}.png"
        val url = "http://api.qrserver.com/v1/create-qr-code/?data=${data}!&size=${size}x${size}"
        val readBytes = client.get(url).readBytes()
        saveImageToFile(readBytes, img_path)
        return img_path
    }

//    @OptIn(InternalAPI::class)
//    suspend fun postRequest(){
//        val data = MyData("John Doe", "30")
//
//        val response = client.post {
//            // configure the request using the builder
//            url("https://testdesktop-bf282-default-rtdb.firebaseio.com/message_list.json")
//            contentType(ContentType.Application.Json)
//            body = data
//        }
//
//        if (response.status.isSuccess()) {
//            println("Success! Response: ${response.bodyAsText()}")
//        } else {
//            println("Error: ${response.status.value}")
//        }
//
//        client.close()
//
//    }
//    suspend fun getSearchedNews(searchedText: String): News {
//        val url = "https://newsapi.org/v2/everything?q=$searchedText&apiKey=${Constants.API_KEY}"
//        return client.get(url).body()
//    }
}

fun saveImageToFile(imageData: ByteArray, fileName: String) {
    val file = File(fileName)
    val outputStream = FileOutputStream(file)
    outputStream.write(imageData)
    outputStream.close()
}
data class MyData(val s: String, val i: String)