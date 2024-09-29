import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: Materializer = Materializer(system)

    // Definindo as rotas GET e POST
    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to Scala API!</h1>"))
        } ~
        post {
          entity(as[String]) { body =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Received POST request: $body</h1>"))
          }
        }
      }

    // Iniciando o servidor na porta 8080
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println("Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // Espera que a tecla Enter seja pressionada para desligar o servidor

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate()) // Quando parar, termina o sistema
  }
}
