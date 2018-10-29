package processhunter;

import javax.swing.JFrame;

public class Gui 
{
        private static final int HEIGHT = 600;
        private static final int WIDTH = 800;
        
        private final JFrame mainFrame;
        
        public Gui()
        {
                
                mainFrame = new JFrame();
                mainFrame.setSize(WIDTH, HEIGHT);
                
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
        }

        
}
