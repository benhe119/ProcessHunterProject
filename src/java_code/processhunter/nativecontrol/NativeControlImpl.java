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

package processhunter.nativecontrol;

import processhunter.util.ProcessInfo;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NativeControlImpl implements NativeControlSpec 
{
        static {
                System.loadLibrary("ProcessHunterLib");
        }
        
        private static final long ERROR = -1;
        private static final long NO_MORE = -2;
        
        private native boolean createProcessSnapshot();
        private native void destroyProcessSnapshot();
        
        private native long getNextProcessAddress();
        private native String getProcessName(long memoryAddress);
        private native long getPID(long memoryAddress);
        private native void freeProcessNativeInfo(long memoryAddress);
        
        private native boolean isProcessRunning(long pid);
        private native boolean killProcessByPid(long pid);
        
        private LinkedList<ProcessInfo> processList;
        
        private static boolean instanceCreated = false;
        
        private final Lock mutex;
        
        private NativeControlImpl()
        {
                if (instanceCreated)
                        throw new RuntimeException("instance running");
                instanceCreated = true;
                mutex = new ReentrantLock(true);
        }
        
        public synchronized static NativeControlSpec createInstance()
        {
                return new NativeControlImpl();
        }
        
        @Override
        public Date updateProcessList() 
        {
                LinkedList<ProcessInfo> processListLocal = new LinkedList<>();
                mutex.lock();
                Date date;
                        
                try {
                        if (!createProcessSnapshot())
                                return null;
                        
                        date = new Date();
                        
                        long addr;
                        while ((addr = getNextProcessAddress()) != ERROR) {
                                if (addr == NO_MORE)
                                        break;
                                
                                try {
                                        processListLocal.add(new ProcessInfo(getProcessName(addr), getPID(addr)));
                                } catch (Exception e) {
                                        
                                }
                                
                                freeProcessNativeInfo(addr);
                        }
                        
                        destroyProcessSnapshot();
                        this.processList = processListLocal;
                } finally {
                        mutex.unlock();
                }
                return date;
        }

        @Override
        public LinkedList<ProcessInfo> getProcessList() 
        {
                LinkedList<ProcessInfo> ret = new LinkedList<>();
                
                mutex.lock();
                try {
                        Iterator<ProcessInfo> it = this.processList.iterator();
                        while (it.hasNext())
                                ret.add(it.next());
                } finally {
                        mutex.unlock();
                }
                
                return ret;
        }

        @Override
        public synchronized boolean killProcess(ProcessInfo info) 
        {
                if (isProcessRunning(info.getPid()))
                        return killProcessByPid(info.getPid());
                else
                        return false;
        }
        
}
