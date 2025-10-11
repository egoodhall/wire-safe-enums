package buildsrc.convention.tasks

import buildsrc.convention.util.envVar
import org.gradle.api.DefaultTask
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64
import java.util.concurrent.atomic.AtomicBoolean

open class PromoteMavenArtifactTask : DefaultTask() {
  companion object {
    private val completed = AtomicBoolean(false)
  }

  init {
    doFirst("Promote staged artifacts") {
      if (completed.getAndSet(true)) {
        return@doFirst
      }

      val namespace = envVar("OSSRH_NAMESPACE")
      val username = envVar("OSSRH_USERNAME")
      val password = envVar("OSSRH_PASSWORD")
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