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
import java.io.FileNotFoundException;
import java.util.Scanner;
import processhunter.core.ProcessHitList;
import processhunter.core.WantedProcessInfo;

public class ConfigParser 
{
        private Scanner configScanner;
        private ProcessHitList hitList;
        private boolean finshed;
        
        public ConfigParser(ProcessHitList hitList, File file)
        {
                if (hitList == null)
                        throw new NullPointerException();
                else
                        this.hitList = hitList;
                
                try {
                        configScanner = new Scanner(file);
                } catch (FileNotFoundException ex) {
                        throw new RuntimeException("File not found");
                }
                finshed = false;
        }
        
        public long parse()
        {
                long ret = 0;
                if (finshed)
                        throw new RuntimeException("Parsing already finshed");
                
                String line;
                String[] parts;
                int count = 0;
                try {
                        while (configScanner.hasNextLine()) {
                                line = configScanner.nextLine();
                                count++;
                                if (line.startsWith("#") || line.isEmpty())
                                        continue;

                                parts = line.split("=");

                                if (parts.length != 2)
                                        throw new RuntimeException(String.format("Invalid line at %d must be -p[flags]=name\n", count));

                                parts[0] = parts[0].trim();
                                parts[1] = parts[1].trim();
                                
                                if (parts[0].equals("timer")) {
                                        try {
                                                if (parts[1].equals("inf"))
                                                        ret = -1;
                                                else
                                                        ret = Long.parseLong(parts[1]);
                                        } catch (NumberFormatException ex) {
                                                throw new Exception("Invalid timer");
                                        }
                                } else {
                                
                                        if (!parts[0].startsWith("-p"))
                                                throw new RuntimeException(String.format("Invalid line at %d command must start with -p\n", count));

                                        hitList.addProcess(new WantedProcessInfo(parts[1], parts[0].contains("n"), parts[0].contains("s"), parts[0].contains("k")));
                                }
                        }
                } catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage());
                } finally {
                        configScanner.close();
                        finshed = true;
                }
                
                if (ret == 0)
                        throw new RuntimeException("No timer specified");
                else
                        return ret;
        }
}
