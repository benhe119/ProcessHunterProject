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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The daemons logger which will save logged info to a file.
 * 
 * @version 1.0
 * @since 2018-11-16
 * 
 * @author Fadi Nassereddine
 */
public class PHD_Log extends PrintWriter 
{
        public static final String LOG_FILENAME_BEG = "ph_daemon_log_";
        
        private static PrintWriter instance = null;
        
        private static File getLogFile() throws IOException
        {
                int counter = 0;
                String fn;
                File ret;
                
                for (;;) {
                        fn = String.format("%s%d.log", LOG_FILENAME_BEG, ++counter);
                        ret = new File(fn);
                        if (!ret.exists()) {
                                ret.createNewFile();
                                return ret;
                        }
                }
        }
        
        private PHD_Log() throws IOException
        {       
                super(new FileOutputStream(getLogFile()));
                Runtime.getRuntime().addShutdownHook(new Thread() { 
                        @Override
                        public void run() 
                        { 
                                synchronized (PHD_Log.class) {
                                        if (instance != null) 
                                                instance.close();
                                }
                        } 
                }); 
        }
        
        /**
         * Get a print writer instance to log information from daemon.
         * @return PrintWriter instance (only one instance is created).
         */
        public static PrintWriter getInstance()
        {
                if (instance == null) {
                        synchronized (PHD_Log.class) {
                                if (instance == null) {
                                        try {
                                                instance = new PHD_Log();
                                        } catch (IOException ex) {
                                                return null;
                                        }
                                }
                                        
                        }
                }
                
                return instance;
        }
}
