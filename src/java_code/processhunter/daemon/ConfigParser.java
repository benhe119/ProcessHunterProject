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

public class ConfigParser 
{
        private Scanner configScanner;
        private ProcessHitList hitList;
        
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
        }
        
        public void parse()
        {
                
        }
}
