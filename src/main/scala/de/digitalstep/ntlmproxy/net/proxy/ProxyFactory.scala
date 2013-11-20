package de.digitalstep.ntlmproxy.net.proxy

import java.net.InetSocketAddress.createUnresolved
import java.net.Proxy
import java.net.Proxy.NO_PROXY
import java.net.Proxy.Type
import java.net.Proxy.Type.HTTP
import java.net.Proxy.Type.SOCKS
import scala.collection.immutable.List

object ProxyFactory {

	implicit def stringToProxyType(proxyType: String) = proxyType match {
		case "socks" | "socket" ⇒ SOCKS
		case _ ⇒ HTTP
	}

	val emptyProxyList: List[Proxy] = List()

	val directAccessProxyList: List[Proxy] = List(NO_PROXY)

	private def createProxy(proxyType: Type, host: String, port: Int): Proxy =
		new Proxy(proxyType, createUnresolved(host, port))

}