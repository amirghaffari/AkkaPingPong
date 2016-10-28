/**
 * Author: Amir Ghaffari <amir_ghaffari@yahoo.com>
 */

package test.unblocking

import akka.actor._
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._

case object Tick
case object Ping
case object Pong

class ActorPing(pong:ActorRef) extends Actor {
    private var tickCounter=0;
    override def receive: Receive = {
        case Tick =>
                    import ExecutionContext.Implicits.global
                    tickCounter+=1
                    // Implicit timeout for the future, i.e. how long to wait for a result before considering it a failure
                    implicit val timeout = Timeout(2 seconds)
                    val future = pong ? Ping
                    future.onSuccess {
                        case x => println(x)
                        tickCounter+=1
                        if(tickCounter>=100){
                            println("Shouting down")
                            context.system.shutdown()
                        }
                    }
                    println("Tick number "+tickCounter)
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
