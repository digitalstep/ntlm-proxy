package de.digitalstep.ntlmproxy

import akka.util.ByteString
import akka.util.ByteStringBuilder

object OKResponse {
	import HttpConstants.CRLF

	val okStatus = ByteString("HTTP/1.1 200 OK")
	val contentType = ByteString("Content-Type: text/html; charset=utf-8")
	val cacheControl = ByteString("Cache-Control: no-cache")
	val date = ByteString("Date: ")
	val server = ByteString("Server: Akka")
	val contentLength = ByteString("Content-Length: ")
	val connection = ByteString("Connection: ")
	val keepAlive = ByteString("Keep-Alive")
	val close = ByteString("Close")

	def bytes(rsp: OKResponse) = {
		new ByteStringBuilder ++=
			okStatus ++= CRLF ++=
			contentType ++= CRLF ++=
			cacheControl ++= CRLF ++=
			date ++= ByteString(new java.util.Date().toString) ++= CRLF ++=
			server ++= CRLF ++=
			contentLength ++= ByteString(rsp.body.length.toString) ++= CRLF ++=
			connection ++= (if (rsp.keepAlive) keepAlive else close) ++= CRLF ++=
			CRLF ++= rsp.body result
	}
}

case class OKResponse(body: ByteString, keepAlive: Boolean)