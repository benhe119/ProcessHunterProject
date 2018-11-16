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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Container for a killed process info by the process hunter.
 * 
 * @version 1.0
 * @since 2018-11-16
 * 
 * @author Fadi Nassereddine
 */
public class ProcessHitList 
{
        private static ProcessHitList instance = null;
        
        private final Queue<HitListListener> hitListListenerQ;
        private final Queue<WantedProcessInfo> processWantedQ;
        private final Lock mutex;
        
        private ProcessHitList()
        {
                hitListListenerQ = new LinkedList<>();
                processWantedQ = new LinkedList<>();
                mutex = new ReentrantLock(true);
        }
        
        /**
         * Get the process instance of ProcessHitList.
         * 
         * @return The handle to the process hit list.
         */
        public static ProcessHitList getInstance()
        {
                if (instance == null) {
                        synchronized (ProcessHitList.class) {
                                if (instance == null)
                                        instance = new ProcessHitList();
                        }
                }
                
                return instance;
        }
        
        /**
         * Register a HitListListener to the hit list to be notified about data
         * added or removed from the hit list.
         * 
         * @see HitListListener
         * 
         * @param listener the listener handle.
         * @return true if it was added otherwise false.
         */
        public synchronized boolean registerListener(HitListListener listener)
        {
                Iterator<HitListListener> it = hitListListenerQ.iterator();
                
                while (it.hasNext()) {
                        if (it.next().equals(listener)) 
                                return false;
                }
                
                hitListListenerQ.add(listener);
                return true;
        }
        
        private synchronized void onProcessAdd(WantedProcessInfo info)
        {
                Iterator<HitListListener> it = hitListListenerQ.iterator();
                while (it.hasNext())
                        it.next().processAdded(info);
                        
        }
        
        private synchronized void onProcessRemove(WantedProcessInfo info)
        {
                Iterator<HitListListener> it = hitListListenerQ.iterator();
                while (it.hasNext())
                        it.next().processRemoved(info);
        }
        
        public synchronized boolean addProcess(WantedProcessInfo info)
        {
                boolean ret = true;
                while (!mutex.tryLock());
                
                try {
                        Iterator<WantedProcessInfo> it = processWantedQ.iterator();
                        while(it.hasNext()) {
                                if (it.next().getProcessName().equals(info.getProcessName())) {
                                        ret = false;
                                        break;
                                }
                                
                        }
                        
                        if (ret) {
                                processWantedQ.add(info);
                                this.onProcessAdd(info);
                        }
                } finally {
                                mutex.unlock();
                }
                return ret;
        }
        
        /**
         * Remove a process from the hit list.
         * 
         * @param info the process identification to be removed.
         * @return true if info passed was found and removed, otherwise false.
         */
        public synchronized boolean removeProcess(WantedProcessInfo info)
        {
                boolean ret = false;
                WantedProcessInfo wpi;
                int i;
                while (!mutex.tryLock());
                try {
                        
                        for (i = 0; i < processWantedQ.size(); i++) {
                                wpi = processWantedQ.remove();
                                if (wpi.getProcessName().equals(info.getProcessName())) {
                                        ret = true;
                                        this.onProcessRemove(wpi);
                                } else {
                                        processWantedQ.add(wpi);
                                }
                        }
                } finally {
                        mutex.unlock();
                }
                
                return ret;
        }
        
        /**
         * Get an array of all processes in the hit list.
         * 
         * @return an array of all processes in the hit list. 
         */
        public synchronized WantedProcessInfo[] getCurrentInfoList()
        {
                int i;
                WantedProcessInfo wpi;
                WantedProcessInfo[] ret;
                while (!mutex.tryLock());
                try {
                        ret = new WantedProcessInfo[processWantedQ.size()];
                        
                        for (i = 0; i < processWantedQ.size(); i++) {
                                wpi = processWantedQ.remove();
                                ret[i] = wpi;
                                processWantedQ.add(wpi);
                        }
                } finally {
                        mutex.unlock();
                }
                return ret;
        }
}
