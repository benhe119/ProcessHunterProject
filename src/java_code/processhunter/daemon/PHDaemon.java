package processhunter.daemon;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import processhunter.core.HitListListener;
import processhunter.core.ProcessHitList;
import processhunter.core.ProcessKilledCallback;
import processhunter.core.WantedProcessInfo;
import processhunter.util.ProcessInfo;

public class PHDaemon implements HitListListener, ProcessKilledCallback
{
        public static final String PH_DAEMON_CONFIG_FILENAME = "ph_daemon.config";
        
        private final PrintWriter log;
        private final ProcessHitList hitList;
        
        public PHDaemon()
        {
                log = PHD_Log.getInstance();
                if (log == null)
                        throw new Error("Fatal error logger for daemon unable to start");
                
                File file = new File(PH_DAEMON_CONFIG_FILENAME);
                
                if (!file.exists() || !file.isFile()) {
                        log.printf("%s does not exists\n", PH_DAEMON_CONFIG_FILENAME);
                        throw new RuntimeException("File does not exist");
                }
                
                hitList = ProcessHitList.getInstance();
                hitList.registerListener(this);
                
                try {
                        ConfigParser parser = new ConfigParser(hitList, file);
                        parser.parse();
                } catch (RuntimeException ex) {
                        log.printf("Error parsing config file reason: %s\n", ex.getMessage());
                        throw new RuntimeException(ex.getMessage());
                }
        }

        @Override
        public void processAdded(WantedProcessInfo process) 
        {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void processRemoved(WantedProcessInfo process) 
        {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void onProcessKill(ProcessInfo info, Date date) 
        {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
}
