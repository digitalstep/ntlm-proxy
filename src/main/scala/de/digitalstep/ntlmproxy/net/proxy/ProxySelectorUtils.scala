package de.digitalstep.ntlmproxy.net.proxy

object ProxySelectorUtils {

	private def defaultPortFor(protocol: String): Int = protocol match {
		case "http" => 80
		case "https" => 443
		case "socks"|"socket" => 1080
		case _ => 80
	}

}