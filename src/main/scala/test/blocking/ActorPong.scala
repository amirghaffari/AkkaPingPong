/**
 * Author: Amir Ghaffari <amir_ghaffari@yahoo.com>
 */

package test.blocking

import akka.actor._

class ActorPong extends Actor {
    override def receive: Receive = {
        case Ping => println(Ping)
        sender() ! Pong
        case _ => sender() ! Status.Failure(new Exception("unknown message"))
    }
}
