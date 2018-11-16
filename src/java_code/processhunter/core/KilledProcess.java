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
import processhunter.util.ProcessInfo;

/**
 * Container for a killed process info by the process hunter.
 * 
 * @version 1.0
 * @since 2018-11-16
 * 
 * @author Fadi Nassereddine
 */
public class KilledProcess 
{
        private final Date date;
        private final ProcessInfo info;
        
        /**
         * Add the date and process info to the instance.
         * 
         * @param date the date the process was terminated.
         * @param info the process info.
         * @see ProcessInfo
         */
        public KilledProcess(Date date, ProcessInfo info)
        {
                if (date == null || info == null)
                        throw new NullPointerException();
                
                this.date = date;
                this.info = info;
        }
        
        /**
         * Get the date process was terminated.
         * 
         * @return Date class of process termination.
         */
        public Date getDate() 
        {
                return date;
        }
        
        /**
         * Get the process info of the process terminated.
         * @see ProcessInfo
         * 
         * @return ProcessInfo object of the terminated process. 
         */
        public ProcessInfo getInfo() 
        {
                return info;
        }
}
