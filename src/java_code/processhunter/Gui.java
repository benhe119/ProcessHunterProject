/*
 * The MIT License
 *
 * Copyright 2018 Fadi Nassereddine.
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

package processhunter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import processhunter.core.HitListListener;
import processhunter.core.ProcessHitList;
import processhunter.core.ProcessHunter;
import processhunter.core.ProcessHunterControls;
import processhunter.core.ProcessKilledCallback;
import processhunter.core.WantedProcessInfo;
import processhunter.util.ProcessHunterException;
import processhunter.util.ProcessInfo;

public class Gui implements HitListListener, ProcessKilledCallback, ActionListener
{
        private static final int HEIGHT = 900;
        private static final int WIDTH = 860;
        
        private final JFrame mainFrame;
        
        private JButton saveLogButton;
        private JButton clearLogButton;
        private JButton enableDisableLogButton;
        
        private JTextPane logPane;
        
        private JTable processListTable;
        private DefaultTableModel model;
        private JButton startStopButton;
        
        private JTextField processIdentifierTF;
        private JButton addProcessButton;
        
        private JCheckBox nameEqualCB;
        private JCheckBox caseSensativeCB;
        private JCheckBox killOnceCB;
        
        private boolean logOn;
        private boolean engaged;
        
        private ProcessHunterControls hunterControls;
        private final ProcessHitList hitList;
        
        public Gui() 
        {
                JPanel logPanel = createLogPanel(new Dimension(WIDTH - 10, HEIGHT / 4));
                JPanel processListPanel = createListPanel(new Dimension(WIDTH / 2, (HEIGHT / 4) * 3));
                JPanel addProcessPanel = createAddProcessPanel(new Dimension(WIDTH / 2, (HEIGHT / 4) * 4));
                JPanel startStopPanel = createStartStopPanel(new Dimension(WIDTH - 10, HEIGHT / 10));
                
                mainFrame = new JFrame();
                mainFrame.setSize(WIDTH, HEIGHT);
                
                JPanel upperPanel = new JPanel();
                upperPanel.setPreferredSize( new Dimension(WIDTH - 10, (HEIGHT / 3) / 2));
                
                upperPanel.setLayout(new GridLayout(1, 2, 10, 10));
                upperPanel.add(addProcessPanel, 0);
                upperPanel.add(processListPanel, 1);
                
                mainFrame.setLayout(new GridLayout(3, 1, 10, 10));
                mainFrame.add(upperPanel, 0);
                mainFrame.add(logPanel, 1);
                mainFrame.add(startStopPanel, 2);
                
                hitList = ProcessHitList.getInstance();
        }
        
        private void enableDisablePressed()
        {
                logOn = !logOn;
                String nMsg = (logOn ? "disable log" : "enable log");
                enableDisableLogButton.setText(nMsg);
                logPane.setEnabled(logOn);
        }
        
        private void saveLogPressed()
        {
                JFileChooser fc = new JFileChooser();
                
                if (fc.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                        String msg = logPane.getText();
                        File fHandle = fc.getSelectedFile();
                        try {
                                PrintWriter pw = new PrintWriter(fHandle);
                                pw.print(msg);
                                pw.close();
                        } catch (FileNotFoundException ex) {
                                JOptionPane.showMessageDialog(mainFrame, "Error writing log file", "log file error", JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        
                        JOptionPane.showMessageDialog(mainFrame, "Current Log written to " + fHandle.getAbsolutePath(), "log file", JOptionPane.INFORMATION_MESSAGE);
                } else {
                        JOptionPane.showMessageDialog(mainFrame, "Error creating file", "log file error", JOptionPane.ERROR_MESSAGE);
                }
        }
        
        private void addProcessPressed()
        {
                if (model.getRowCount() >= 99) {
                        JOptionPane.showMessageDialog(mainFrame, "To many process identifiers please remove some", "Process Identifier", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                
                String procId = processIdentifierTF.getText();
                if (procId == null)
                        return;
                
                procId = procId.trim();
                if (procId.isEmpty() || procId.contains(" ")) {
                        JOptionPane.showMessageDialog(mainFrame, "Bad Process Identifier", "Process Identifier", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                
                if (!hitList.addProcess(new WantedProcessInfo(procId, nameEqualCB.isSelected(), caseSensativeCB.isSelected(), killOnceCB.isSelected()))) 
                        JOptionPane.showMessageDialog(mainFrame, "Error adding process Identifier", "Process Identifier", JOptionPane.ERROR_MESSAGE);
                
        }
        
        private void clearLogPressed()
        {
                logPane.setText("");
        }
        
        private synchronized void log(String msg)
        {
                if (!logOn)
                        return;
                
                String old = logPane.getText();
                old += msg + "\n";
                logPane.setText(old);
        }
        
        private void startStopPressed()
        {
                if (!engaged) {
                        if (hunterControls.isRunning())
                                return;
                        hunterControls.start();
                        startStopButton.setText("Stop");
                        engaged = true;
                } else {
                        hunterControls.stop();
                        while (hunterControls.isRunning());
                        engaged = false;
                        startStopButton.setText("Start");
                }
        }
        
        private void wireUpGui() 
        {
                logOn = false;
                engaged = false;
                logPane.setEnabled(false);
                enableDisableLogButton.addActionListener(this);
                saveLogButton.addActionListener(this);
                clearLogButton.addActionListener(this);
                addProcessButton.addActionListener(this);
                startStopButton.addActionListener(this);
                
                this.processListTable.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent me) {
                                if (processListTable == (JTable)me.getSource()) {
                                        if (me.getClickCount() == 2 && processListTable.getSelectedRow() != -1) {
                                                if (JOptionPane.showConfirmDialog(mainFrame, "Remove this Identification", "remove", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
                                                        hitList.removeProcess(new WantedProcessInfo((String)model.getValueAt(processListTable.getSelectedRow(), 0), false, false, false));
                                        }
                                }
                        }
                });
        }
        
        private JPanel createLogPanel(Dimension dimension) 
        {
                JPanel ret = new JPanel();
                ret.setPreferredSize(dimension);
                
                logPane = new JTextPane();
                logPane.setEditable(false);
                
                int paneWidth = dimension.width - 40;
                int paneHeight = (dimension.height / 3) * 2;
                
                int buttonWidth = dimension.width;
                int buttonHeight = dimension.height - paneHeight;
                
                Dimension logPaneDimension = new Dimension(paneWidth, paneHeight);
                
                logPane.setPreferredSize(logPaneDimension);
                JPanel textPanePanel = new JPanel();
                textPanePanel.setPreferredSize(logPaneDimension);
                textPanePanel.setLayout(new BorderLayout());
                JScrollPane sp = new JScrollPane(logPane);
                sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                sp.setAutoscrolls(true);
                textPanePanel.add(sp, BorderLayout.CENTER);
                
                JPanel buttonPanel = new JPanel();
                buttonPanel.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                
                
                Dimension buttonDimension = new Dimension((buttonPanel.getPreferredSize().width - 30) / 3, buttonPanel.getPreferredSize().height / 2);
                saveLogButton = new JButton("Save Log");
                saveLogButton.setPreferredSize(buttonDimension);
                
                clearLogButton = new JButton("Clear Log");
                clearLogButton.setPreferredSize(buttonDimension);
                
                enableDisableLogButton = new JButton("enable log");
                enableDisableLogButton.setPreferredSize(buttonDimension);
                
                buttonPanel.setLayout(new FlowLayout(0));
                buttonPanel.add(saveLogButton);
                buttonPanel.add(clearLogButton);
                buttonPanel.add(enableDisableLogButton);
                
                ret.setLayout(new GridLayout(2, 1, 10, 10));
                
                ret.add(textPanePanel, 0);
                ret.add(buttonPanel, 1);
                
                return ret;
        }

        private JPanel createListPanel(Dimension dimension) 
        {
                JPanel ret = new JPanel();
                ret.setPreferredSize(dimension);
                
                Dimension tableDimension = new Dimension(dimension.width - 30, dimension.height - 50);
                
                ret.setPreferredSize(new Dimension( tableDimension.width - 10, tableDimension.height - 10));
                
                model = new DefaultTableModel(0, 0) {
                        @Override
                        public boolean isCellEditable(int i, int i1) 
                        {
                                return false;
                        }
                };
                
                processListTable = new JTable(model);
                processListTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                processListTable.getTableHeader().setReorderingAllowed(false);
                processListTable.setPreferredSize(tableDimension);
                processListTable.setMaximumSize(tableDimension);
                processListTable.setPreferredScrollableViewportSize(tableDimension);
                processListTable.setFillsViewportHeight(true);
                
                JScrollPane sp = new JScrollPane(processListTable);
                sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                
                ret.setLayout(new BorderLayout());
                ret.add(sp, BorderLayout.CENTER);
                
                return ret;
        }

        private JPanel createAddProcessPanel(Dimension dimension) 
        {
                JPanel ret = new JPanel();
                ret.setPreferredSize(dimension);
                
                Dimension textFieldButtonDimension = new Dimension(dimension.width - 10, (dimension.height / 10));
                
                JPanel identifierTFPanel = new JPanel();
                JPanel addProcessPanel = new JPanel();
                
                identifierTFPanel.setPreferredSize(textFieldButtonDimension);
                addProcessPanel.setPreferredSize(textFieldButtonDimension);
                
                processIdentifierTF = new JTextField("Process Identifier");
                processIdentifierTF.setPreferredSize(textFieldButtonDimension);
                
                addProcessButton = new JButton("Add Process");
                addProcessButton.setPreferredSize(textFieldButtonDimension);
                
                identifierTFPanel.setLayout(new FlowLayout());
                addProcessPanel.setLayout(new FlowLayout());
                
                identifierTFPanel.add(processIdentifierTF);
                addProcessPanel.add(addProcessButton);
                
                JPanel indentifierCBPanel = new JPanel();
                indentifierCBPanel.setPreferredSize(new Dimension(dimension.width - 10, (dimension.height / 10) * 8));
                
                Dimension cBDim = new Dimension(indentifierCBPanel.getPreferredSize().width - 5, (indentifierCBPanel.getPreferredSize().height / 3));
                
                nameEqualCB = new JCheckBox("Identifier Text should match");
                caseSensativeCB = new JCheckBox("Case Sensitive");
                killOnceCB = new JCheckBox("Kill Process Once");
                
                nameEqualCB.setPreferredSize(cBDim);
                caseSensativeCB.setPreferredSize(cBDim);
                killOnceCB.setPreferredSize(cBDim);
                
                indentifierCBPanel.setLayout(new GridLayout(3, 1, 10, 10));
                indentifierCBPanel.add(nameEqualCB, 0);
                indentifierCBPanel.add(caseSensativeCB, 1);
                indentifierCBPanel.add(killOnceCB, 2);
                
                ret.setLayout(new GridLayout(3, 1, 10, 10));
                ret.add(processIdentifierTF, 0);
                ret.add(indentifierCBPanel, 1);
                ret.add(addProcessButton, 2);
                
                return ret;
        }

        private JPanel createStartStopPanel(Dimension dimension) 
        {
                startStopButton = new JButton("Start");
                JPanel ret = new JPanel();
                ret.setPreferredSize(dimension);
                startStopButton.setPreferredSize(dimension);
                
                ret.setLayout(new FlowLayout());
                ret.add(startStopButton);
                
                return ret;
        }
        
        public static void setLookAndFeel()
        {
                try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
        }
        
        public void init()
        {
                try {
                        hunterControls = ProcessHunter.getInstance(hitList, this);
                } catch (ProcessHunterException ex) {
                        JOptionPane.showMessageDialog(null, "Error starting up process Hunter", "Process Hunter Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                }
                
                hitList.registerListener(this);
                
                wireUpGui();
                model.addColumn("Process");
                model.addColumn("Must Equal - Case Sensitive - kill once");
                
                mainFrame.pack();
                mainFrame.setResizable(false);
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
        }

        @Override
        public void processAdded(WantedProcessInfo process) 
        {
                String[] row = new String[2];
                
                row[0] = process.getProcessName();
                row[1] = String.format("%c | %c | %c", process.justEqualsName() ? 'T' : 'F', process.isCaseSensative() ? 'T' : 'F', process.justKillOnce() ? 'T' : 'F');
                model.addRow(row);
                log("Process Added: " + process.getProcessName());
        }

        @Override
        public void processRemoved(WantedProcessInfo process) 
        {
                String str;
                int row = model.getRowCount(), i;
                for (i = 0; i < row; i++) {
                        str = (String)model.getValueAt(i, 0);
                        if (str.equals(process.getProcessName())) {
                                model.removeRow(i);
                                log("Process Removed: " + process.getProcessName());
                                return;
                        }
                }
        }

        @Override
        public void onProcessKill(ProcessInfo info, Date date) 
        {
                
                
                StringBuilder sb = new StringBuilder();
                sb.append("==============================\n");
                sb.append("Process Killed: ");
                sb.append(info.getProcessName());
                sb.append(" | ");
                sb.append(info.getPid());
                sb.append("\n");
                sb.append("Snapshot date : ");
                sb.append(date.toString());
                sb.append("\n");
                sb.append("==============================");
                log(sb.toString());
        }

        @Override
        public void actionPerformed(ActionEvent e) 
        {
                if (e.getSource() instanceof JButton) {
                        JButton button = (JButton)e.getSource();
                        
                        if (button.equals(clearLogButton))
                                clearLogPressed();
                        else if (button.equals(enableDisableLogButton))
                                enableDisablePressed();
                        else if (button.equals(saveLogButton))
                                saveLogPressed();
                        else if (button.equals(addProcessButton))
                                addProcessPressed();
                        else if (button.equals(startStopButton))
                                startStopPressed();
                }
        }
}
