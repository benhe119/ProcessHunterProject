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

/**
 * Container for a wanted process and how to identify it.
 * 
 * @version 1.0
 * @since 2018-11-16
 * 
 * @author Fadi Nassereddine
 */
public class WantedProcessInfo 
{
        private boolean equalsName;
        private boolean caseSensitive;
        private boolean killOnce;
        
        private String processName;
        
        /**
         * Created a new wanted process.
         * 
         * @param processName the string for identification.
         * @param equalsName if the strings should match.
         * @param caseSensitive if comparison should be case sensitive.
         * @param killOnce if the wanted process should be killed only on first 
         * detect.
         */
        public WantedProcessInfo(String processName, boolean equalsName, boolean caseSensitive, boolean killOnce)
        {
                if (processName == null)
                        throw new NullPointerException();
                
                this.equalsName = equalsName;
                this.caseSensitive = caseSensitive;
                this.killOnce = killOnce;
                
                this.processName = processName;
        }
        
        /**
         * Check if identification should equal names.
         * @return true if names must match otherwise false.
         */
        public boolean justEqualsName() 
        {
                return equalsName;
        }

        /**
         * Check if identification should be case sensitive.
         * @return true if comparison should be case sensitive otherwise false.
         */
        public boolean isCaseSensitive() 
        {
                return caseSensitive;
        }

        /**
         * If the process should only be killed on first detect.
         * @return true if it should be killed once otherwise false.
         */
        public boolean justKillOnce() 
        {
                return killOnce;
        }
        
        /**
         * Get the process identification name.
         * @return the process identification name.
         */
        public String getProcessName() 
        {
                return processName;
        }
        
        @Override
        public String toString()
        {
                StringBuilder sb = new StringBuilder();
                sb.append("-p");
                if (this.equalsName)
                        sb.append('n');
                if (this.caseSensitive)
                        sb.append('s');
                if (this.killOnce)
                        sb.append('k');
                sb.append(' ');
                sb.append(this.processName);
                return sb.toString();
        }
}
