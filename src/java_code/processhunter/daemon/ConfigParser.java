package processhunter.daemon;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import processhunter.core.ProcessHitList;

public class ConfigParser 
{
        private Scanner configScanner;
        private ProcessHitList hitList;
        
        public ConfigParser(ProcessHitList hitList, File file)
        {
                if (hitList == null)
                        throw new NullPointerException();
                else
                        this.hitList = hitList;
                
                try {
                        configScanner = new Scanner(file);
                } catch (FileNotFoundException ex) {
                        throw new RuntimeException("File not found");
                }
        }
        
        public void parse()
        {
                
        }
}
