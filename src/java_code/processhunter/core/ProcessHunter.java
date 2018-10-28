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

package processhunter.core;

import java.util.Date;
import java.util.LinkedList;
import processhunter.nativecontrol.NativeControlImpl;
import processhunter.nativecontrol.NativeControlSpec;
import processhunter.util.ProcessHunterException;
import processhunter.util.ProcessInfo;

public class ProcessHunter implements HitListListener, ProcessHunterControls
{
        private static ProcessHunterControls instance = null;
        
        private final ProcessHitList hitList;
        private Thread runningThread;
        private boolean running;
        
        private WantedProcessInfo[] wantedProcessess;
        
        private NativeControlSpec nativeControls;
        
        private final ProcessKilledCallback callback;
        
        private ProcessHunter(ProcessHitList hitList, ProcessKilledCallback callback) throws ProcessHunterException
        {
                if (instance != null)
                        throw new ProcessHunterException("Hunter instance running");
                
                if (hitList == null || callback == null)
                        throw new NullPointerException();
                this.hitList = hitList;
                this.wantedProcessess = hitList.getCurrentInfoList();
                running = false;
                nativeControls = NativeControlImpl.createInstance();
                this.callback = callback;
                hitList.registerListener(this);
        }
        
        public static ProcessHunterControls getInstance(ProcessHitList hitList, ProcessKilledCallback callback) throws ProcessHunterException
        {
                if (instance == null) {
                        synchronized (ProcessHunter.class) {
                                instance = new ProcessHunter(hitList, callback);
                        }
                }
                
                return instance;
        }
        
        @Override
        public void processAdded(WantedProcessInfo process) 
        {
                this.wantedProcessess = hitList.getCurrentInfoList();
        }

        @Override
        public void processRemoved(WantedProcessInfo process) 
        {
                this.wantedProcessess = hitList.getCurrentInfoList();
        }

        @Override
        public boolean killProcessByPid(long pid) 
        {
                try {
                        return this.nativeControls.killProcess(new ProcessInfo("process", pid));
                } catch (Exception e) {
                        return false;
                }
        }
        
        private synchronized void logProcessKilled(ProcessInfo info, WantedProcessInfo wpi, Date date)
        {
                if (wpi.justKillOnce())
                        this.hitList.removeProcess(wpi);
                callback.onProcessKill(info, date);
        }
        
        private void shouldKillProcess(ProcessInfo info, WantedProcessInfo wpi, Date date)
        {
                if (wpi.isCaseSensative() && wpi.justEqualsName()) {
                        if (info.getProcessName().equals(wpi.getProcessName())) {
                                if (this.nativeControls.killProcess(info))
                                        logProcessKilled(info, wpi, date);
                        }
                } else if (wpi.isCaseSensative() && !wpi.justEqualsName()) {
                        if (info.getProcessName().contains(wpi.getProcessName())) {
                                if (this.nativeControls.killProcess(info))
                                        logProcessKilled(info, wpi, date);
                        }
                } else if (!wpi.isCaseSensative() && wpi.justEqualsName()) {
                        if (info.getProcessName().toLowerCase().equals(wpi.getProcessName().toLowerCase())) {
                                if (this.nativeControls.killProcess(info))
                                        logProcessKilled(info, wpi, date);
                        }
                } else {
                        if (info.getProcessName().toLowerCase().contains(wpi.getProcessName().toLowerCase())) {
                                if (this.nativeControls.killProcess(info))
                                        logProcessKilled(info, wpi, date);
                        }
                }
        }
        
        @Override
        public void start() 
        {
                if (running)
                        return;
                this.running = true;
                
                runningThread = new Thread(() -> {
                        LinkedList<ProcessInfo> processess;
                        Date date;
                        ProcessInfo info;
                        int i;
                        while (running) {
                                date = this.nativeControls.updateProcessList();
                                processess = this.nativeControls.getProcessList();
                                WantedProcessInfo[] cpy;
                                
                                synchronized (this.wantedProcessess) {
                                        cpy = new WantedProcessInfo[this.wantedProcessess.length];
                                        for (i = 0; i < this.wantedProcessess.length; i++)
                                                cpy[i] = this.wantedProcessess[i];
                                }
                                
                                
                                while (!processess.isEmpty()) {
                                        info = processess.remove();
                                        for (WantedProcessInfo wpi : cpy) 
                                                shouldKillProcess(info, wpi, date);
                                        
                                }
                        }
                        
                        this.running = false;
                });
                runningThread.start();
        }

        @Override
        public void stop() 
        {
                this.running = false;
                try {
                        runningThread.join();
                } catch (InterruptedException ex) {
                }
        }

        @Override
        public boolean isRunning() 
        {
                return this.running;
        }
        
}
