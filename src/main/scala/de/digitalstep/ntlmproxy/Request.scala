package de.digitalstep.ntlmproxy

import akka.util.ByteString

case class Header(
	name: String,
	value: String)

case class Request(
	method: String,
	path: List[String],
	query: Option[String],
	httpVersion: String,
	headers: List[Header],
	body: Option[ByteString])
