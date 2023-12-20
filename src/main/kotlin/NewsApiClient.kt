import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*

object NewsApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

//    suspend fun getTopHeadlines(): News {
//        val url = "https://testdesktop-bf282-default-rtdb.firebaseio.com/message_list.json"
//        return client.get(url).body()
//    }

    @OptIn(InternalAPI::class)
    suspend fun postRequest(){
        val data = MyData("John Doe", "30")

        val response = client.post {
            // configure the request using the builder
            url("https://testdesktop-bf282-default-rtdb.firebaseio.com/message_list.json")
            contentType(ContentType.Application.Json)
            body = data
        }

        if (response.status.isSuccess()) {
            println("Success! Response: ${response.bodyAsText()}")
        } else {
            println("Error: ${response.status.value}")
        }

        client.close()

    }
//    suspend fun getSearchedNews(searchedText: String): News {
//        val url = "https://newsapi.org/v2/everything?q=$searchedText&apiKey=${Constants.API_KEY}"
//        return client.get(url).body()
//    }
}

data class MyData(val s: String, val i: String)