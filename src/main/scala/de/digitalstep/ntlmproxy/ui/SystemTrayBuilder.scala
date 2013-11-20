package de.digitalstep.ntlmproxy.ui

import java.awt.AWTException
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType
import java.util.prefs.Preferences
import com.google.common.base.Optional
import com.typesafe.scalalogging.slf4j.Logging
import de.digitalstep.ntlmproxy.HandlerListener
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import javax.swing.UIManager.{ getSystemLookAndFeelClassName ⇒ system }
import javax.swing.UIManager.{ put ⇒ uiOption }
import javax.swing.UIManager.setLookAndFeel
import java.awt.MenuItem
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.awt.CheckboxMenuItem
import java.awt.event.ItemListener
import java.awt.event.ItemEvent
import javax.swing.JOptionPane

object SystemTrayBuilder extends Logging {

	private val prefs = Preferences.userRoot.node(getClass().getPackage().getName());

	def apply(): Optional[HandlerListener] = {
		if (SystemTray.isSupported()) {
			initLookAndFeel()

			val logWindow = new LogWindow
			logWindow.getConsole().redirectOut()

			val imageIcon = new ImageIcon(getClass().getResource("/tray.png"), "tray icon").getImage()
			val trayIcon = new TrayIcon(imageIcon)

			val popupMenu = new PopupMenu()
			popupMenu add {
				val showLog = new MenuItem("Show Log")
				showLog.addActionListener(new ActionListener() {
					override def actionPerformed(e: ActionEvent) {
						logWindow.open()
					}
				})
				showLog
			}
			
			popupMenu add {
				val showBubble = new CheckboxMenuItem("Show Bubbles")
				showBubble.setState(prefs.getBoolean("showBubble", true))
				showBubble.addItemListener(new ItemListener() {
					override def itemStateChanged(e: ItemEvent) {
					    setShowBubble(showBubble.getState())
					}
					private def setShowBubble(show: Boolean) {
						prefs.putBoolean("showBubble", show)
					}
				})
				showBubble
			}
			
			popupMenu addSeparator
			
			popupMenu add {
				val aboutItem = new MenuItem("About")
				aboutItem.addActionListener(new ActionListener() {
					import de.digitalstep.ntlmproxy.ui.ApplicationProperties.{
						applicationName,
						applicationVersion
					}
					import javax.swing.JOptionPane.INFORMATION_MESSAGE
					
					override def actionPerformed(e: ActionEvent) {
						JOptionPane.showMessageDialog(
								null,
								String.format("Version %s", applicationVersion),
								applicationName,
								INFORMATION_MESSAGE)
					}
				})
				aboutItem
			}

			new PopupMenuBuilder(prefs).buildPopupMenu(logWindow, trayIcon)

			MySwingUtilities invokeLater {
				try {
					SystemTray.getSystemTray().add(trayIcon)
					logger.info("UI initialized. All log output sent to UI window")
				} catch {
					case e: AWTException ⇒ throw new RuntimeException(e)
				}
			}

			Optional.of(new HandlerListener() {
				override def onGet(uri: java.net.URI, proxy: java.net.Proxy) {
					super.onGet(uri, proxy)
					if (prefs.getBoolean("showBuggle", true)) {
						trayIcon displayMessage ("GET via " + proxy.address(), uri.toString(), MessageType.INFO)
					}
				}
			})
		} else {
			logger.info("SystemTray is not supported")
			Optional.absent()
		}
	}

	private def initLookAndFeel() {
		import javax.swing.UIManager.{
			getSystemLookAndFeelClassName ⇒ system,
			setLookAndFeel,
			put ⇒ uiOption
		}
		setLookAndFeel(system)
		uiOption("swing.boldMetal", false)
	}

}

private object MySwingUtilities {
	def invokeLater[X](exp: ⇒ X) {
		import javax.swing.SwingUtilities

		SwingUtilities invokeLater (new Runnable() {
			def run = exp
		})
	}
}