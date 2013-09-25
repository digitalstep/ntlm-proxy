/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.digitalstep.ntlmproxy.ui;

import static de.digitalstep.ntlmproxy.ui.ApplicationProperties.applicationName;
import static de.digitalstep.ntlmproxy.ui.ApplicationProperties.applicationVersion;
import static java.lang.Boolean.FALSE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.Proxy;
import java.net.URI;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import de.digitalstep.ntlmproxy.HandlerListener;

public class TrayIconBuilder {

    private static final Logger log = LoggerFactory.getLogger(TrayIconBuilder.class);

    public static void main(String[] args) {
        new TrayIconBuilder().build();
    }

    private final LogWindow logWindow;
    private final Preferences prefs = Preferences.userRoot().node(getClass().getName());

    private final TrayIcon trayIcon;

    public TrayIconBuilder() {
        this(new LogWindow());
    }
    
    public TrayIconBuilder(LogWindow logWindow) {
        this.logWindow = logWindow;
        this.trayIcon = trayIcon();
    }
    
    public Optional<HandlerListener> build() {
        if (!SystemTray.isSupported()) {
            log.info("SystemTray is not supported");
            return Optional.absent();
        }

        logWindow.getConsole().redirectOut();

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        UIManager.put("swing.boldMetal", FALSE);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    SystemTray.getSystemTray().add(trayIcon);
                } catch (AWTException ex) {
                    throw new RuntimeException(ex);
                }

                log.info("UI initialized. All log output sent to UI window");
            }
        });
        return Optional.<HandlerListener> of(new HandlerListener() {
            @Override
            protected void onGet(URI uri, Proxy proxy) {
                super.onGet(uri, proxy);
                if (isShowBubble()) {
                    trayIcon.displayMessage("GET via " + proxy.address(), uri.toString(), MessageType.INFO);
                }
            }
        });
    }

    private MenuItem aboutItem() {
        MenuItem menuItem = new MenuItem("About");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        String.format("Version %s", applicationVersion()),
                        applicationName(),
                        INFORMATION_MESSAGE);
            }
        });
        return menuItem;
    }

    private MenuItem exitItem() {
        MenuItem menuItem = new MenuItem("Exit");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SystemTray.getSystemTray().remove(trayIcon);
                System.exit(0);
            }
        });
        return menuItem;
    }

    private boolean isShowBubble() {
        return prefs.getBoolean("showBubble", true);
    }

    private void setShowBubble(boolean showBubble) {
        prefs.putBoolean("showBubble", showBubble);
    }

    private MenuItem showBubble() {
        final CheckboxMenuItem menuItem = new CheckboxMenuItem("Show Bubbles");
        menuItem.setState(isShowBubble());
        menuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setShowBubble(menuItem.getState());
            }
        });
        return menuItem;
    }

    private MenuItem showLog(final ActionListener listener) {
        final MenuItem menuItem = new MenuItem("Show Log");
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private TrayIcon trayIcon() {
        final PopupMenu popup = new PopupMenu();

        final ActionListener openLogWindow = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logWindow.open();
            }
        };
        popup.add(showLog(openLogWindow));
        popup.add(showBubble());
        popup.addSeparator();
        popup.add(aboutItem());
        popup.add(exitItem());

        final TrayIcon trayIcon = new TrayIcon((new ImageIcon(getClass().getResource("/tray.png"), "tray icon")).getImage());
        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(openLogWindow);

        return trayIcon;
    }
}
