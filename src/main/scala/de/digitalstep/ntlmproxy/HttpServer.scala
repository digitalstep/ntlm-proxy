package de.digitalstep.ntlmproxy

import akka.actor._
import akka.util.{ ByteStringBuilder, ByteString }
import java.net.InetSocketAddress

object HttpConstants {
    val CRLF = ByteString("\r\n")
}

class HttpServer(port: Int) extends Actor {

	val state = IO.IterateeRef.Map.async[IO.Handle]()(context.dispatcher)

	override def preStart {
		IOManager(context.system) listen new InetSocketAddress(port)
	}

	def receive = {

		case IO.NewClient(server) ⇒
			val socket = server.accept()
			state(socket) flatMap (_ ⇒ HttpServer.processRequest(socket))

		case IO.Read(socket, bytes) ⇒
			state(socket)(IO Chunk bytes)

		case IO.Closed(socket, cause) ⇒
			state(socket)(IO EOF)
			state -= socket

	}

}

object HttpServer {
import uk.co.bigbeeconsultants.http.HttpClient
import uk.co.bigbeeconsultants.http.response.Response
import java.net.URL

	import HttpIteratees._

	def loadGoogle = {
		val client = new HttpClient
		val response = client.get(new URL("http://www.google.com/"))
		response.body.asString
}
	
	def processRequest(socket: IO.SocketHandle): IO.Iteratee[Unit] =
		IO repeat {
			for {
				request ← readRequest
			} yield {
				val response = request match {
					case Request("GET", "ping" :: Nil, _, _, headers, _) ⇒
						OKResponse(ByteString("<p>pong</p>"),
							request.headers.exists {
								case Header(n, v) ⇒ n.toLowerCase == "connection" && v.toLowerCase == "keep-alive"
							})
					case Request("GET", "google" :: Nil, _, _, headers, _) ⇒
						OKResponse(ByteString(loadGoogle),
							request.headers.exists {
								case Header(n, v) ⇒ n.toLowerCase == "connection" && v.toLowerCase == "keep-alive"
							})
					case req ⇒
						OKResponse(ByteString("<p>" + req.toString + "</p>"),
							request.headers.exists {
								case Header(n, v) ⇒ n.toLowerCase == "connection" && v.toLowerCase == "keep-alive"
							})
				}
				socket write OKResponse.bytes(response).compact
				if (!response.keepAlive) socket.close()
			}
		}

}
