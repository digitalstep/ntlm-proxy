package de.digitalstep.ntlmproxy

import java.net.URL
import com.google.common.io.ByteStreams
import de.compeople.commons.net.proxy.CompoundProxySelectorFactory
import java.net.URI
import java.net.InetSocketAddress

object UseSystemProxyTest extends App {

	def setProxy(uri: URI) {
		val proxy = CompoundProxySelectorFactory.getProxySelector().select(uri).get(0)
		val address = proxy.address().asInstanceOf[InetSocketAddress]

		print("Proxy address is " + address + ": ")

		if (null == address) {
			System.setProperty("http.proxyHost", "");
			System.setProperty("http.proxyPort", "");
		} else {
			System.setProperty("http.proxyHost", address.getHostName());
			System.setProperty("http.proxyPort", Integer.toString(address.getPort()));
		}
	}

	def toString(uri: URI) = {
		setProxy(uri)
		val in = (uri toURL).openStream()
		val string = new String(ByteStreams.toByteArray(in))
		in.close()
		string
	}

	println("Testing simple JDK system proxy")

	val local = URI.create("http://localhost:8085/artifactory/libs-release-local/de/skandia/ibanvalidator/ibanvalidator-client/2.0.0/ibanvalidator-client-2.0.0.pom")
	val remote = URI.create("http://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-resources-plugin/2.5/maven-resources-plugin-2.5.pom")

	print((toString(local) substring (0, 19)) + " ...\n")
	print((toString(remote) substring (0, 19)) + " ...\n")

}