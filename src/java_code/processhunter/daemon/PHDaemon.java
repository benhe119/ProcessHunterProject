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
import processhunter.core.ProcessHunter;
import processhunter.core.ProcessHunterControls;
import processhunter.core.ProcessKilledCallback;
import processhunter.core.WantedProcessInfo;
import processhunter.util.ProcessHunterException;
import processhunter.util.ProcessInfo;

public class PHDaemon implements HitListListener, ProcessKilledCallback
{
        public static final String PH_DAEMON_CONFIG_FILENAME = "ph_daemon.config";
        
        private ProcessHunterControls hunterControls;
        private final ProcessHitList hitList;
        private final PrintWriter log;
        private final ConfigParser parser;
        
        private volatile int processCount;
        
        public PHDaemon()
        {
                File file = new File(PH_DAEMON_CONFIG_FILENAME);
                if (!file.exists() || !file.isFile()) 
                        throw new RuntimeException("File does not exist");
                
                log = PHD_Log.getInstance();
                if (log == null)
                        throw new Error("Fatal error logger for daemon unable to start");
                
                hitList = ProcessHitList.getInstance();
                parser = new ConfigParser(hitList, file);
                processCount = 0;
        }
        
        public void engage()
        {
                hitList.registerListener(this);
                long timer;
                try {
                        timer = parser.parse();
                } catch (RuntimeException ex) {
                        log.printf("Error parsing config file reason: %s\n", ex.getMessage());
                        log.flush();
                        throw new RuntimeException(ex.getMessage());
                }
                
                log.println("Hunter starting...");
                log.flush();
                try {
                        hunterControls = ProcessHunter.getInstance(hitList, this);
                } catch (ProcessHunterException ex) {
                        log.printf("Failed to create process hunter. Reason: %s\n", ex.getMessage());
                        log.flush();
                        throw new RuntimeException(ex.getMessage());
                }
                if (timer > 0) {
                        log.printf("Hunter on for %d milliseconds\r\n", timer);
                        log.flush();
                        hunterControls.start();

                        try {
                                Thread.sleep(timer);
                        } catch (InterruptedException ex) {

                        }

                } else {
                        log.println("Hunter on for inf milliseconds");
                        log.flush();
                        hunterControls.start();
                        
                        while (this.processCount > 0) {
                                try {
                                        Thread.sleep(100);
                                } catch (InterruptedException ex) {

                                }
                        }
                        
                }
                
                hunterControls.stop();
                log.println("Hunter done"); 
                log.flush();
                
        }
        
        @Override
        public void processAdded(WantedProcessInfo process) 
        {
                ++this.processCount;
                log.printf("Process added %s\r\n", process.toString());
                log.flush();
        }

        @Override
        public void processRemoved(WantedProcessInfo process) 
        {
                --this.processCount;
                log.printf("Process removed %s\r\n", process.toString());
                log.flush();
        }

        @Override
        public void onProcessKill(ProcessInfo info, Date date) 
        {
                log.printf("Process killed: name: %s pid: %d | snapshot date: %s\r\n", info.getProcessName(), info.getPid(), date.toString());
                log.flush();
        }
}
