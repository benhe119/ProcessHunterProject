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
import java.util.LinkedList;

/**
 * What the process hunter will need as native implementation to run.
 * 
 * @version 1.0
 * @since 2018-11-16
 * 
 * @author Fadi Nassereddine
 */
public interface NativeControlSpec 
{
        /**
         * Update the process list with currently running processes.
         * @return the date of the most up to date process list.
         */
        public Date updateProcessList();
        
        /**
         * Get the current process list.
         * @return a linked list of all processes running.
         */
        public LinkedList<ProcessInfo> getProcessList();
        
        /**
         * Kill a process based of ProcessInfo.
         * @param info the info of process to be terminated.
         * @return true if it was successfully terminated otherwise false.
         * @see ProcessInfo
         */
        public boolean killProcess(ProcessInfo info);
}
