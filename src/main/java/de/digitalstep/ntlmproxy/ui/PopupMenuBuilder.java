package de.digitalstep.ntlmproxy.ui;

import static de.digitalstep.ntlmproxy.ui.ApplicationProperties.applicationName;
import static de.digitalstep.ntlmproxy.ui.ApplicationProperties.applicationVersion;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

class PopupMenuBuilder {
    private final Preferences prefs;

    PopupMenuBuilder(Preferences preferences) {
        this.prefs = preferences;
    }

    void buildPopupMenu(final LogWindow logWindow, final TrayIcon trayIcon) {
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
        popup.add(exitItem(trayIcon));

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(openLogWindow);
    }

    private MenuItem aboutItem() {
        MenuItem menuItem = new MenuItem("About");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        String.format("Version %s", applicationVersion()), applicationName(),
                        INFORMATION_MESSAGE);
            }
        });
        return menuItem;
    }

    private MenuItem exitItem(final TrayIcon trayIcon) {
        MenuItem menuItem = new MenuItem("Exit");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SystemTray.getSystemTray().remove(trayIcon);
                System.exit(0);
            }
        });
        return menuItem;
    }

    private void setShowBubble(boolean showBubble) {
        prefs.putBoolean("showBubble", showBubble);
    }

    private MenuItem showBubble() {
        final CheckboxMenuItem menuItem = new CheckboxMenuItem("Show Bubbles");
        menuItem.setState(prefs.getBoolean("showBubble", true));
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
}