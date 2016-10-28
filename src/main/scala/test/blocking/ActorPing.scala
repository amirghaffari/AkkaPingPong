/**
 * Author: Amir Ghaffari <amir_ghaffari@yahoo.com>
 */

package test.blocking

import akka.actor._
import scala.concurrent.duration._

case object Tick
case object Ping
case object Pong

class ActorPing(pong:ActorRef) extends Actor {
    private var tickCounter=0;
    override def receive: Receive = {
        case Tick =>
                    tickCounter+=1
                    pong ! Ping
                    println("Tick number "+tickCounter)
        case Pong => println(Pong)
                    if(tickCounter>=100){
                        println("Shouting down")
                        context.system.shutdown()
                    }
        case _ => sender() ! Status.Failure(new Exception("unknown message"))
    }
}

object Initializer extends App {
    val system = ActorSystem("PingPong")
    import system.dispatcher

    // 1 ping every .05 seconds.
    val tickFrequency=50 // 50 milliseconds

    // create a pong actor
    val pong = system.actorOf(Props(classOf[ActorPong]), name = "pong")

    // create a ping actor
    val ping = system.actorOf(Props(classOf[ActorPing],pong), name = "ping")

    val cancellable:Cancellable = system.scheduler.schedule(tickFrequency milliseconds, tickFrequency milliseconds, ping, Tick)
}
