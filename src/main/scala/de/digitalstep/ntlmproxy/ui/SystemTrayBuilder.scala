package de.digitalstep.ntlmproxy.ui

import java.awt.SystemTray
import com.google.common.base.Optional
import com.typesafe.scalalogging.slf4j.Logging
import de.digitalstep.ntlmproxy.HandlerListener
import javax.swing.UIManager

object SystemTrayBuilder extends Logging {

	def apply(): Optional[HandlerListener] =
		SystemTray.isSupported() match {

			case false ⇒
				logger.info("SystemTray is not supported")
				Optional.absent()

			case true ⇒
				UIManager setLookAndFeel "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
				UIManager put ("swing.boldMetal", false)

				val logWindow = new LogWindow
				logWindow.getConsole().redirectOut()
				
				val builder = new TrayIconBuilder
				val trayIcon = builder.trayIcon(logWindow)

				builder.build(trayIcon)
		}

}