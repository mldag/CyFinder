/* 
 * Copyright 2015 Charles Allen Schultz II.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.claflin.cyfinder.internal.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Represents an error panel containing error information.  Specifically, 
 * contains an informative string and a stack trace printed to a concealable 
 * JTextArea.
 * 
 * @author Charles Allen Schultz II
 * @version 1.0 June 19, 2015
 */
public class ErrorPanel extends JPanel implements ActionListener {
    
    /**
     * The custom dialog implemented by the ErrorPanel for self-display.
     */
    private JDialog dialog = null;
    /**
     * The list ActionListeners to notify of the event "DISPLAY_CHANGED".
     */
    private ArrayList<ActionListener> listeners = new ArrayList<>();
    
    /**
     * The custom String description of the error that was thrown.
     */
    private final String description;
    /**
     * The Throwable that triggered the error.
     */
    private final Throwable error;
    
    // UI Components
    
    /**
     * A private JPanel reference used to display and hide the details of the 
     * error.
     */
    private JPanel hiddenPanel;
    
    /**
     * Constructs the ErrorPanel.
     * 
     * @param description a String representing a custom message to display.
     * @param error the Throwable object that triggered the error.
     */
    public ErrorPanel(String description, Throwable error) {
        this.description = description;
        this.error = error;
        
        init();
    }
    /**
     * Initializes the UI.
     */
    private void init() {
        setLayout(new BorderLayout());
        
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));
        hiddenPanel = new JPanel();
        hiddenPanel.setLayout(new BorderLayout());
        hiddenPanel.setVisible(false);
        
        JLabel messageLabel = new JLabel(description);
        JButton displayButton = new JButton("Show");
        displayButton.addActionListener(this);
        displayButton.setActionCommand("DISPLAY_CHANGED");
        
        messagePanel.add(Box.createHorizontalStrut(10));
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createHorizontalGlue());
        messagePanel.add(displayButton);
        messagePanel.add(Box.createHorizontalStrut(10));
        add(messagePanel, BorderLayout.PAGE_START);
        
        JTextArea errorArea = new JTextArea();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        errorArea.setText(sw.toString());
        errorArea.setEditable(false);
        errorArea.setRows(15);
        JScrollPane scroller = new JScrollPane(errorArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        hiddenPanel.add(scroller, BorderLayout.CENTER);
        add(hiddenPanel);
    }

    /**
     * Adds an ActionListener to the notify list.
     * @param aL the ActionListener to add.
     */
    public void addActionListener(ActionListener aL) {
        listeners.add(aL);
    }
    /**
     * Removes an ActionListener from the notify list.
     * @param aL the AcitonListener to remove.
     */
    public void removeActionListener(ActionListener aL) {
        listeners.remove(aL);
    }

    /**
     * Shows and hides the error details.  If the ErrorPanel is handling its own
     * display it will automatically adjust the size of it's parent component.
     * If the user adds the ErrorPanel to a container the user is responsible for 
     * ensuring it is resized when this action event is triggered by attaching 
     * ActionListeners to this component.  The Action Command thrown by these 
     * events is "DISPLAY_CHANGED".
     * <br>
     * {@inheritDoc }
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        hiddenPanel.setVisible(!hiddenPanel.isVisible());
        JButton displayButton = (JButton) e.getSource();
        displayButton.setText(hiddenPanel.isVisible() ? "Hide" : "Show");
        if (dialog != null) {
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.repaint();
        }

        listeners.parallelStream().forEach((aL) -> {
            aL.actionPerformed(e);
        });
    }

    public void display(Component parent, String title) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane opPane = new JOptionPane(this, 
                    JOptionPane.ERROR_MESSAGE);
            dialog = opPane.createDialog(parent, title);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            dialog.dispose();
            dialog = null;
        });
    }
}
