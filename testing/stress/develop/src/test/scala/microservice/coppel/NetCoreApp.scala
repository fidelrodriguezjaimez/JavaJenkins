//*carpeta padre y subcarpeta 
package microservice.coppel


//*librerias
import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class NetCoreApp extends Simulation{

    //mvn gatling:test -Dgatling.simulationClass=microservice.coppel.NetCoreApp

  val config = ConfigFactory.load("application")
  var baseUrl = config.getString("url.baseUrlNetCore")
  var baseAuthUrl = config.getString("url.baseAuthUrlNetCore")

  object Temperaturas {

        var weatherForecast =
        //feed(Obtenermatriculas)
        exec(http("WeatherForecast")
        .get(baseUrl + "WeatherForecast/ping") // https://localhost:44372/WeatherForecast/ping
        .check(status.is(200)))

        var weatherForecastPost =  
        exec(http("WeatherForecast")
        .post(baseUrl + "WeatherForecast")
        .header("accept", "text/plain")
        .header("content-type", "application/json")
        .body(StringBody("""{ "valor": "static Value" }"""))
        .check(status.is(200)))

  }

      val scn = scenario("Temperatura test")
     .pause(5)
     .exec(
         Temperaturas.weatherForecast,
         Temperaturas.weatherForecastPost
     )

    setUp(
     scn.inject(atOnceUsers(1))
       ).protocols()

}