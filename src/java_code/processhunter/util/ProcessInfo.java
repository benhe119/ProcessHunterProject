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

package processhunter.util;

/**
 * Container for process info gotten from native controls.
 * 
 * @version 1.0
 * @since 2018-11-16
 * 
 * @author Fadi Nassereddine
 */
public class ProcessInfo 
{
        private String processName;
        private long pid;
        
        /**
         * Create an object with the process name and its PID.
         * 
         * @param processName the process name.
         * @param pid the process ID.
         */
        public ProcessInfo(String processName, long pid)
        {
                if (processName == null)
                        throw new NullPointerException();
                if (pid < 0)
                        throw new IllegalStateException("Improper pid passed");
                
                this.processName = processName;
                this.pid = pid;
        }
        
        /**
         * Get the process name.
         * @return the process name.
         */
        public String getProcessName() 
        {
                return processName;
        }
        
        /**
         * Get the process ID.
         * @return the process ID.
         */
        public long getPid() 
        {
                return pid;
        }
}
