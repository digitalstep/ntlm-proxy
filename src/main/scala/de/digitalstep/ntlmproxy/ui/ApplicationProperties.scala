package de.digitalstep.ntlmproxy.ui

import java.util.Properties

object ApplicationProperties {
	private val properties = new Properties
	properties.load(getClass().getResourceAsStream("/application.properties"))

	val applicationName = properties.getProperty("application.name")
	val applicationVersion = properties.getProperty("application.version")

}