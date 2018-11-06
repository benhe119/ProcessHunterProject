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
