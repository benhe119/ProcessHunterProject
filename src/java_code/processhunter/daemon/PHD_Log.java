package processhunter.daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class PHD_Log extends PrintWriter 
{
        public static final String LOG_FILENAME_BEG = "PH_DAEMON_LOG_";
        
        private static PrintWriter instance = null;
        
        private static File getLogFile() throws IOException
        {
                int counter = 0;
                String fn;
                File ret;
                
                for (;;) {
                        fn = String.format("%s%d.log", LOG_FILENAME_BEG, ++counter);
                        ret = new File(fn);
                        if (!ret.exists()) {
                                ret.createNewFile();
                                return ret;
                        }
                }
        }
        
        private PHD_Log() throws IOException
        {       
                super(new FileOutputStream(getLogFile()));
                Runtime.getRuntime().addShutdownHook(new Thread() { 
                        @Override
                        public void run() 
                        { 
                                synchronized (PHD_Log.class) {
                                        if (instance != null) 
                                                instance.close();
                                }
                        } 
                }); 
        }
        
        public static PrintWriter getInstance()
        {
                if (instance == null) {
                        synchronized (PHD_Log.class) {
                                if (instance == null) {
                                        try {
                                                instance = new PHD_Log();
                                        } catch (IOException ex) {
                                                return null;
                                        }
                                }
                                        
                        }
                }
                
                return instance;
        }
}
