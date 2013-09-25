package de.digitalstep.ntlmproxy.net.proxy

import java.net.Proxy
import java.net.SocketAddress
import java.util.Collections

object Helper {
	
	def emptyProxyList(): List[Proxy] = List()

	def create(proxyType: Proxy.Type, address: SocketAddress) = new Proxy(proxyType, address)

	def proxyType(protocol: String) = protocol match {
		case "socks" | "socket" ⇒ Proxy.Type.SOCKS
		case _ ⇒ Proxy.Type.HTTP
	}

}