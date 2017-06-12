/**
  * Created by agustin on 26/05/17.
  */
/*
package AkkaActorSystem

import Actors.actor1
import Actors.actor2
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.client.ClusterClientReceptionist
import akka.protobuf.Service

import scala.io.StdIn


object ActoresAkka extends App{

  val actorSystem = ActorSystem("FederationActorSystem")

  val actor1 = actorSystem.actorOf(Props[actor1], "actor1")
  val actor2 = actorSystem.actorOf(Props[actor2], name = "actor2")

  actor2.tell("hello", actor1)

 /* runOn(host1) {
    val serviceA = actorSystem.actorOf(Props[Service], "serviceA")
    ClusterClientReceptionist(actorSystem).registerService(serviceA)
  }

  runOn(host2, host3) {
    val serviceB = actorSystem.actorOf(Props[Service], "serviceB")
    ClusterClientReceptionist(actorSystem).registerService(serviceB)
  }
*/

  StdIn.readLine()
}
*/

package ClusterAkka

import Actors.{SimpleCluster, Actor2}
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object AkkaCluster {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      for (i <- 0 to ConfigFactory.load().getStringList("akka.cluster.seed-nodes").size() - 1)
      {
        val port = ConfigFactory.load().getStringList("akka.cluster.seed-nodes").get(i).split(":")
        startup(port(2))
      }
      startup("0")
    }
    else
      startup(args(0))
  }

  def startup(port: String): Unit = {
      // Override the configuration of the port
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load())

      // Create an AkkaActorSystem
      val system = ActorSystem.create("ClusterSystem", config)

      // Create an actor that handles cluster domain events
      val actor1 = system.actorOf(Props[SimpleCluster], name = "actor1")
     // val actor2 = system.actorOf(Props[Actor2], name = "actor2")

      //actor1.tell("hello", actor2)
  }
}

