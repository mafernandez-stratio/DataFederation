package Actors

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

/**
  * Created by agustin on 1/06/17.
  */
class Actor2 extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  var counter = 0

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) => log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case "hello" => println("Hello " + counter)
      counter+=1
      sender ! "received"
    case "received" => println("Hi! " + counter)
      counter+=1
      sender ! "close"
    case "close" => println("Bye! " + counter)
      context.system.terminate()
      println("Closed friend 2!!")
    case _: MemberEvent => // ignore
  }
}
