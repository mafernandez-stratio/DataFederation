package AkkaClusterClient

/**
  * Created by agustin on 13/06/17.
  */

import akka.actor.{ActorPath, ActorSystem, Props}
import akka.cluster.client.{ClusterClientReceptionist, ClusterClientSettings, ClusterClient}
import akka.protobuf.Service
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/**
  * Objetivos de esta fase:
  * 1) hacer un put de los ficheros parquet de vuelos en HDFS
  * 2) Formar un cluster de Akka con 2 nodos
  * 3) que cada servidor, ejecute las sentencias SQL que vaya recibiendo en su Spark Session
  * 4) conseguir enviar sentencias SQL desde el Akka Client al cluster de Akka:
  *   - create table (registrando las tablas de los vuelos)
  *   - select (sobre esa tabla recién registrada)
  */
object AkkaClient  extends App{

  val system = ActorSystem("ClientSystem", ConfigFactory.load())

  /**
    * De momento, es suficiente con poner un único contact point (Akka se encarga por debajo de que luego en realidad
    * el cluster client conozca todos los nodos del cluster de Akka de forma transparente)
    * Atención a los siguientes parámetros:
    * - El nombre del sistema tiene que coincidir con el nombre que le hayamos dado al cluster de Akka en los servidores
    * - El host de momento lo vamos a poner a 127.0.0.1
    * - El puerto tiene que coincidir con uno de los puertos en los que esta escuchando uno de los servidores
    *   previamente levantados (propiedad akka.remote.netty.tcp.port del server)
    */
  val initialContacts = Set(ActorPath.fromString("akka.tcp://OtherSys@host1:2552/system/receptionist"),
                            ActorPath.fromString("akka.tcp://OtherSys@host2:2552/system/receptionist"))

  // Se puede borrar, ya que no se utiliza
  val settings = ClusterClientSettings(system).withInitialContacts(initialContacts)

  // Se puede borrar
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

  // Se puede borrar
  val serviceA = system.actorOf(Props(classOf[Service], "serviceA"))
  ClusterClientReceptionist(system).registerService(serviceA)

  // Se puede borrar
  val serviceB = system.actorOf(Props(classOf[Service], "serviceB"))
  ClusterClientReceptionist(system).registerService(serviceB)

  val clusterActor = system.actorOf(ClusterClient.props(ClusterClientSettings(system).withInitialContacts(initialContacts)), "client")

  /**
    * Se puede borrar aunque algo parecido se tendrá que uilizar más abajo.
    * Es importante que el nombre que va después de /user/ coincida con el nombre del actor que se crea en los servidores
    */
  clusterActor ! ClusterClient.Send("/user/serviceA", "hello", localAffinity = true)
  clusterActor ! ClusterClient.SendToAll("/user/serviceB", "hi")

  /**
    * Mírate http://otfried.org/scala/reading_terminal.html para descubrir como poner un prompt a la shell.
    * Para recibir más sentencias SQL, esta línea de código debería estar rodeada de un bucle infinito y que se salga
    * de él cuando reciba la palabra exit por línea de comandos
    */
  val query = StdIn.readLine()

  // Ahora hay que enviar esa query al cluster del Data Federation


}
