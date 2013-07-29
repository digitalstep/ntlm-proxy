package de.digitalstep.ntlmproxy.ui;

import static java.awt.BorderLayout.CENTER;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

class LogWindow extends JFrame {

    private static final long serialVersionUID = -2537609075527015144L;

    private final Preferences prefs = Preferences.userRoot().node(getClass().getName());
    private final MessageConsole console;

    LogWindow() {
        super("NTLM Proxy Log");

        this.setLayout(new BorderLayout());
        final JTextComponent textArea = textArea();
        final JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, CENTER);
        this.console = new MessageConsole(textArea);

        this.pack();
        this.setBounds(
                prefs.getInt("x", this.getX()),
                prefs.getInt("y", this.getY()),
                prefs.getInt("width", getWidth()),
                prefs.getInt("height", getHeight()));

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                prefs.putInt("width", LogWindow.this.getWidth());
                prefs.putInt("height", LogWindow.this.getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                prefs.putInt("x", LogWindow.this.getX());
                prefs.putInt("y", LogWindow.this.getY());
            }
        });
    }

    @SuppressWarnings("serial")
    private JTextComponent textArea() {
        final JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(false);
        textArea.setComponentPopupMenu(new JPopupMenu());
        textArea.getComponentPopupMenu().add(new JMenuItem(new AbstractAction("Clear") {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        }));
        textArea.getComponentPopupMenu().add(new JCheckBoxMenuItem(new AbstractAction("Word Wrap") {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setLineWrap(this.enabled = !textArea.getLineWrap());
            }
        }));
        return textArea;
    }

    public void open() {
        this.setVisible(true);
    }

    public MessageConsole getConsole() {
        return console;
    }

}
