package de.digitalstep.ntlmproxy

import akka.actor.ActorSystem
import akka.actor.Props

object Main extends App {
    val port = Option(System.getenv("PORT")) map (_.toInt) getOrElse 8080
    val system = ActorSystem()
    val server = system.actorOf(Props(new HttpServer(port)))
}