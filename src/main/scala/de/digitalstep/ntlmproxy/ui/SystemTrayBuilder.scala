package de.digitalstep.ntlmproxy.ui

class SystemTrayBuilder {
	
	val delegate = new TrayIconBuilder
	
	def build() = delegate.build()

}

object SystemTrayBuilder {
	def apply() = new SystemTrayBuilder().build
}