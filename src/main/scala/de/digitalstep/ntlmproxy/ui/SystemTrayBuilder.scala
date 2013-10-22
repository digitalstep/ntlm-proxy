package de.digitalstep.ntlmproxy.ui

import java.awt.SystemTray
import com.google.common.base.Optional
import com.typesafe.scalalogging.slf4j.Logging
import de.digitalstep.ntlmproxy.HandlerListener
import javax.swing.UIManager

object SystemTrayBuilder extends Logging {
	
	import javax.swing.UIManager.{
		getSystemLookAndFeelClassName,
		setLookAndFeel,
		put ⇒ uiOption
	}

	def apply(): Optional[HandlerListener] =
		SystemTray.isSupported() match {
			case true ⇒
				setLookAndFeel(getSystemLookAndFeelClassName)
				uiOption("swing.boldMetal", false)

				val logWindow = new LogWindow
				logWindow.getConsole().redirectOut()

				val builder = new TrayIconBuilder
				val trayIcon = builder.trayIcon(logWindow)

				builder.build(trayIcon)
			case false ⇒
				logger.info("SystemTray is not supported")
				Optional.absent()
		}

}