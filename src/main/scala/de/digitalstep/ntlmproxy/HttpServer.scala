package de.digitalstep.ntlmproxy

import akka.actor._
import akka.util.{ ByteStringBuilder, ByteString }
import java.net.InetSocketAddress

object HttpConstants {
	val SP = ByteString(" ")
	val HT = ByteString("\t")
	val CRLF = ByteString("\r\n")
	val COLON = ByteString(":")
	val PERCENT = ByteString("%")
	val PATH = ByteString("/")
	val QUERY = ByteString("?")
}

object HttpIteratees {
	import HttpConstants._

	def readRequest =
		for {
			requestLine ← readRequestLine
			(method, (path, query), httpVersion) = requestLine
			headers ← readHeaders
			body ← readBody(headers)
		} yield Request(method, path, query, httpVersion, headers, body)

	def ascii(bytes: ByteString): String = bytes.decodeString("US-ASCII").trim

	def readRequestLine =
		for {
			method ← IO takeUntil SP
			uri ← readRequestURI
			_ ← IO takeUntil SP // ignore the rest
			httpVersion ← IO takeUntil CRLF
		} yield (ascii(method), uri, ascii(httpVersion))

	def readRequestURI = IO peek 1 flatMap {
		case PATH ⇒
			for {
				path ← readPath
				query ← readQuery
			} yield (path, query)
		case _ ⇒ sys.error("Not Implemented")
	}

	def readPath = {
			def step(segments: List[String]): IO.Iteratee[List[String]] = IO peek 1 flatMap {
				case PATH ⇒ IO drop 1 flatMap (_ ⇒ readUriPart(pathChar) flatMap (segment ⇒ step(segment :: segments)))
				case _ ⇒ segments match {
					case "" :: rest ⇒ IO Done rest.reverse
					case _ ⇒ IO Done segments.reverse
				}
			}
		step(Nil)
	}

	def readQuery: IO.Iteratee[Option[String]] = IO peek 1 flatMap {
		case QUERY ⇒ IO drop 1 flatMap (_ ⇒ readUriPart(queryChar) map (Some(_)))
		case _ ⇒ IO Done None
	}

	val alpha = Set.empty ++ ('a' to 'z') ++ ('A' to 'Z') map (_.toByte)
	val digit = Set.empty ++ ('0' to '9') map (_.toByte)
	val hexDigit = digit ++ (Set.empty ++ ('a' to 'f') ++ ('A' to 'F') map (_.toByte))
	val subDelim = Set('!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=') map (_.toByte)
	val pathChar = alpha ++ digit ++ subDelim ++ (Set(':', '@') map (_.toByte))
	val queryChar = pathChar ++ (Set('/', '?') map (_.toByte))

	def readUriPart(allowed: Set[Byte]): IO.Iteratee[String] = for {
		str ← IO takeWhile allowed map ascii
		pchar ← IO peek 1 map (_ == PERCENT)
		all ← if (pchar) readPChar flatMap (ch ⇒ readUriPart(allowed) map (str + ch + _)) else IO Done str
	} yield all

	def readPChar = IO take 3 map {
		case Seq('%', rest @ _*) if rest forall hexDigit ⇒
			java.lang.Integer.parseInt(rest map (_.toChar) mkString, 16).toChar
	}

	def readHeaders = {
			def step(found: List[Header]): IO.Iteratee[List[Header]] = {
				IO peek 2 flatMap {
					case CRLF ⇒ IO takeUntil CRLF flatMap (_ ⇒ IO Done found)
					case _ ⇒ readHeader flatMap (header ⇒ step(header :: found))
				}
			}
		step(Nil)
	}

	def readHeader =
		for {
			name ← IO takeUntil COLON
			value ← IO takeUntil CRLF flatMap readMultiLineValue
		} yield Header(ascii(name), ascii(value))

	def readMultiLineValue(initial: ByteString): IO.Iteratee[ByteString] = IO peek 1 flatMap {
		case SP ⇒ IO takeUntil CRLF flatMap (bytes ⇒ readMultiLineValue(initial ++ bytes))
		case _ ⇒ IO Done initial
	}

	def readBody(headers: List[Header]) =
		if (headers.exists(header ⇒ header.name == "Content-Length" || header.name == "Transfer-Encoding"))
			IO.takeAll map (Some(_))
		else
			IO Done None

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

object Main extends App {
	val port = Option(System.getenv("PORT")) map (_.toInt) getOrElse 8080
	val system = ActorSystem()
	val server = system.actorOf(Props(new HttpServer(port)))
}
