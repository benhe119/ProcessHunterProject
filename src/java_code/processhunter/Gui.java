package processhunter;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

public class Gui 
{
        private static final int HEIGHT = 900;
        private static final int WIDTH = 860;
        
        private final JFrame mainFrame;
        
        private JButton saveLogButton;
        private JButton clearLogButton;
        private JButton enableDisableLogButton;
        
        private JTextPane logPane;
        
        private JTable processListTable;
        private JButton startStopButton;
        
        private JTextField processIdentifierTF;
        private JButton addProcessButton;
        
        private JCheckBox nameEqualCB;
        private JCheckBox caseSensativeCB;
        private JCheckBox killOnceCB;
        
        public Gui()
        {
                
                JPanel logPanel = createLogPanel(new Dimension(WIDTH - 10, HEIGHT / 4));
                JPanel processListPanel = createListPanel(new Dimension(WIDTH / 2, (HEIGHT / 4) * 3));
                JPanel addProcessPanel = createAddProcessPanel(new Dimension(WIDTH / 2, (HEIGHT / 4) * 4));
                JPanel startStopPanel = createStartStopPanel(new Dimension(WIDTH - 10, HEIGHT / 4));
                
                mainFrame = new JFrame();
                mainFrame.setSize(WIDTH, HEIGHT);
                
                JPanel upperPanel = new JPanel();
                upperPanel.setPreferredSize( new Dimension(WIDTH - 10, (HEIGHT / 3) / 2));
                
                upperPanel.setLayout(new GridLayout(1, 2, 10, 10));
                upperPanel.add(addProcessPanel, 0);
                upperPanel.add(processListPanel, 1);
                
                startStopButton.setPreferredSize(new Dimension(WIDTH - 20, (HEIGHT / 20) * 19));
                
                mainFrame.setLayout(new GridLayout(3, 1, 10, 10));
                mainFrame.add(upperPanel, 0);
                mainFrame.add(logPanel, 1);
                mainFrame.add(startStopPanel, 2);
                
                mainFrame.pack();
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
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
                textPanePanel.setLayout(new FlowLayout());
                textPanePanel.add(logPane);
                
                JPanel buttonPanel = new JPanel();
                buttonPanel.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                
                
                Dimension buttonDimension = new Dimension((buttonPanel.getPreferredSize().width - 30) / 3, buttonPanel.getPreferredSize().height / 2);
                saveLogButton = new JButton("Save Log");
                saveLogButton.setPreferredSize(buttonDimension);
                
                clearLogButton = new JButton("Clear Log");
                clearLogButton.setPreferredSize(buttonDimension);
                
                enableDisableLogButton = new JButton("enable Log");
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
                
                Dimension tableDimension = new Dimension(dimension.width - 30, dimension.height - 10);
                
                JPanel tablePanel = new JPanel();
                tablePanel.setPreferredSize(tableDimension);
                String[] header = {"process", "pid"};
                
                DefaultTableModel model = new DefaultTableModel(0, header.length);
                model.setColumnIdentifiers(header);
                processListTable = new JTable(model);
                processListTable.setPreferredSize(tableDimension);
                tablePanel.setLayout(new FlowLayout());
                tablePanel.add(processListTable);
                
                /*
                Dimension buttonDimension = new Dimension(dimension.width -10, (dimension.height / 15));
                JPanel buttonPanel = new JPanel();
                buttonPanel.setPreferredSize(buttonDimension);
                startStopButton = new JButton("start");
                startStopButton.setPreferredSize(buttonDimension);
                buttonPanel.add(startStopButton);
                */
                ret.setLayout(new FlowLayout());
                ret.add(tablePanel);
                
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
                
                processIdentifierTF = new JTextField("Process Indentifier");
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
                
                nameEqualCB = new JCheckBox("Indentifier Text should match");
                caseSensativeCB = new JCheckBox("Case Sensative");
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
}
