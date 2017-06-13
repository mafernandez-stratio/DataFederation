package AkkaClusterClient

/**
  * Created by agustin on 13/06/17.
  */

import akka.actor.{ActorPath, ActorSystem, Props}
import akka.cluster.client.{ClusterClientReceptionist, ClusterClientSettings, ClusterClient}
import akka.protobuf.Service
import com.typesafe.config.ConfigFactory

import scala.io.StdIn


object AkkaClient  extends App{

  val system = ActorSystem("ClientSystem", ConfigFactory.load())
  val initialContacts = Set(ActorPath.fromString("akka.tcp://OtherSys@host1:2552/system/receptionist"),
                            ActorPath.fromString("akka.tcp://OtherSys@host2:2552/system/receptionist"))
  val settings = ClusterClientSettings(system).withInitialContacts(initialContacts)

 /* runOn(host1) {
    val serviceA = system.actorOf(Props[Service], "serviceA")
    ClusterClientReceptionist(system).registerService(serviceA)
  }

  runOn(host2, host3) {
    val serviceB = system.actorOf(Props[Service], "serviceB")
    ClusterClientReceptionist(system).registerService(serviceB)
  }

  runOn(client) {
    val c = system.actorOf(ClusterClient.props(
      ClusterClientSettings(system).withInitialContacts(initialContacts)), "client")
    c ! ClusterClient.Send("/user/serviceA", "hello", localAffinity = true)
    c ! ClusterClient.SendToAll("/user/serviceB", "hi")
  }

*/
  val serviceA = system.actorOf(Props(classOf[Service], "serviceA"))
  ClusterClientReceptionist(system).registerService(serviceA)

  val serviceB = system.actorOf(Props(classOf[Service], "serviceB"))
  ClusterClientReceptionist(system).registerService(serviceB)

  val clusterActor = system.actorOf(ClusterClient.props(ClusterClientSettings(system).withInitialContacts(initialContacts)), "client")

  clusterActor ! ClusterClient.Send("/user/serviceA", "hello", localAffinity = true)
  clusterActor ! ClusterClient.SendToAll("/user/serviceB", "hi")

  StdIn.readLine()
}
