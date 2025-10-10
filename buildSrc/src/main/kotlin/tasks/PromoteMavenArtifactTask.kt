package buildsrc.convention.tasks

import org.gradle.api.DefaultTask
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

open class PromoteMavenArtifactTask : DefaultTask() {
  init {
    doFirst("Promote staged artifacts") {
      val namespace = System.getenv("OSSRH_NAMESPACE")?.takeIf(String::isNotBlank)!!
      val username = System.getenv("OSSRH_USERNAME")?.takeIf(String::isNotBlank)!!
      val password = System.getenv("OSSRH_PASSWORD")?.takeIf(String::isNotBlank)!!
      val token = encoder.encodeToString("$username:$password".toByteArray())

      val request = HttpRequest
        .newBuilder()
        .POST(HttpRequest.BodyPublishers.noBody())
        .uri(URI.create("https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/$namespace"))
        .header("Authorization", "Bearer $token")
        .build()

      val response = client.send(request, HttpResponse.BodyHandlers.ofString())
      require(response.statusCode() in 200..299) {
        "Unable to promote staged maven artifact(s): [${response.statusCode()}] ${response.body()}"
      }
    }
  }
}

private val encoder = Base64.getUrlEncoder()
private val client = HttpClient.newBuilder()
  .followRedirects(HttpClient.Redirect.NORMAL)
  .build()