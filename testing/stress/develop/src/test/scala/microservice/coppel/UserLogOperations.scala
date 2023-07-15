package microservice.coppel

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Test1 extends Simulation {
  val httpProtocol = http
    .baseUrl("https://www.ejemplo.com")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")

     //mvn gatling:test -Dgatling.simulationClass=microservice.coppel.Test1
  val scn = scenario("Mi Escenario")
    .exec(http("Página de inicio")
      .get("/")
      .check(status.is(200)))
    .pause(5)
    .exec(http("Búsqueda")
      .get("/search?q=gatling")
      .check(status.is(200)))

  setUp(
    scn.inject(
      rampUsersPerSec(1) to (5) during (10 seconds),
      constantUsersPerSec(5) during (20 seconds),
      rampUsersPerSec(5) to (1) during (10 seconds)
    ).protocols(httpProtocol)
  )}