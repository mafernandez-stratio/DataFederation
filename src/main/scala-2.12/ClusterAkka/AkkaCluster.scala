package ClusterAkka

/**
  * Created by agustin on 26/05/17.
  */

import Actors.{SimpleCluster, Actor2}
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object AkkaCluster {
  def main(args: Array[String]): Unit = {
    /**
      * args debería recibir 2 argumentos:
      * 1) puerto para el único seed node (ya que, de momento, vamos a poner la IP fija a 127.0.0.1) (akka.cluster.seed-nodes)
      * 2) puerto donde este servidor que se está levantando va a recibir peticiones (akka.remote.netty.tcp.port)
      */
    if (args.isEmpty) {
      for (i <- 0 until ConfigFactory.load().getStringList("akka.cluster.seed-nodes").size() - 1)
      {
        val port = ConfigFactory.load().getStringList("akka.cluster.seed-nodes").get(i).split(":")
        startup(port(2))
      }
      //startup("0")
    }
    else
      startup(args(0))
  }

  def startup(port: String): Unit = {
      // Override the configuration of the port
    /**
      * Esta configuración que le estamos pasando al ActorSystem, solo contiene la propiedad 'akka.remote.netty.tcp.port',
      * se debería también incluir la propiedad 'akka.cluster.seed-nodes' con otra llamada a parseString
      */
    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load())

      // Create an AkkaActorSystem
      /** "ClusterSystem" es el nombre que estamos dando al cluster de Akka y que después necesitamos para escribir los
        * Seed Nodes (Ejemplo: http://doc.akka.io/docs/akka/current/scala/cluster-usage.html#joining-to-seed-nodes) y que
        * también necesitaremos en el cliente para los contacts points
        * (http://doc.akka.io/docs/akka/current/java/cluster-client.html#an-example). Podemos dejar este nombre estandar
        * o poner algo más significativo como "DataFederationSystem" o "DataFederationCluster", lo que prefieras
        */
      val system = ActorSystem.create("ClusterSystem", config)

      // Create an actor that handles cluster domain events
      // De momento, con crear un único tipo de actor va a ser suficiente
      val actor1 = system.actorOf(Props[SimpleCluster], name = "actor1")
    // Recuerda que hay que registrar al actor1 como receptor de mensajes del ClusterClient
      val actor2 = system.actorOf(Props[Actor2], name = "actor2")

      //actor1.tell("hello", actor2)
  }
}

