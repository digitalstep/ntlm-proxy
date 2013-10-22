package de.digitalstep.ntlmproxy

import com.google.common.base.Optional
import de.digitalstep.ntlmproxy.ui.TrayIconBuilder
import java.net.ServerSocket
import com.typesafe.scalalogging.slf4j.Logging
import java.util.concurrent.Executors
import de.digitalstep.ntlmproxy.ui.SystemTrayBuilder

object NtlmProxy extends Logging {
	
	val EXECUTOR = Executors.newCachedThreadPool

	def main(args: Array[String]) {
		System.setProperty("java.net.useSystemProxies", "true")

		val listener = SystemTrayBuilder() 
		val port = Integer.parseInt(args(0))
		val server = new ServerSocket(port)
		logger.info(s"Proxy listening on port ${port}")
		while (true) {
			val socket = server.accept()
			EXECUTOR.execute(new Handler(socket, listener))
		}
		
	}

}