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

package processhunter;

import java.util.Date;
import processhunter.core.ProcessHitList;
import processhunter.core.ProcessHunter;
import processhunter.core.ProcessHunterControls;
import processhunter.core.ProcessKilledCallback;
import processhunter.core.WantedProcessInfo;
import processhunter.util.ProcessHunterException;
import processhunter.util.ProcessInfo;

public class Main
{
        private static void help()
        {
                System.out.println("Usage: [flag] [name]...");
                System.out.println("for each process repeat flag and name");
                System.out.println("flag: start with -p\nappend n if should equal process name\nappend s if case sensative\nappend k if kill once");
                System.out.println("Examples:\n-p [process name]\n-pnsk [process name]\n-pns [process name]\n");
                System.out.println("\nNOTE: appends chars to flag does not need to be in any order but must start with -p and if only -p name must equal and will be case sensative\n");
        }
        
        public static void main(String[] args) throws ProcessHunterException 
        {
                if (args.length == 1) {
                        if (args[0].equals("-help")) 
                                help();
                        else 
                                System.out.printf("%s unknown\n\'-help\' for help\n", args[0]);
                        
                        System.exit(0);
                }
                
                RealMain m = new RealMain(args);
        }
        
        private static class RealMain implements ProcessKilledCallback
        {
                public RealMain(String[] args) throws ProcessHunterException
                {
                        
                        if (args.length < 2 || args.length % 2 != 0) {
                                System.out.println("Invalid parameters\n\'-help\' for help");
                                System.exit(1);
                        }

                        int i;
                        String flag;
                        String procName;
                        WantedProcessInfo wpi;
                        ProcessHitList list = ProcessHitList.getInstance();

                        for (i = 0; i < args.length; i += 2) {
                                flag = args[i];
                                procName = args[i + 1];
                                if (!flag.startsWith("-p"))
                                        continue;
                                wpi = new WantedProcessInfo(procName, flag.contains("n"), flag.contains("s"), flag.contains("k"));
                                System.out.printf("process watchdog created: %s\n", wpi.toString());
                                list.addProcess(wpi);
                        }
                        ProcessHunterControls hunter = ProcessHunter.getInstance(list, this);
                        hunter.start();
                        
                        while (hunter.isRunning()) {
                                try {
                                        Thread.sleep(100);
                                } catch (InterruptedException ex) {
                                }
                        }
                }

                @Override
                public void onProcessKill(ProcessInfo info, Date date) 
                {
                        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
                        System.out.printf("Process Killed [name: %s ||| pid: %d]\nSnapshot date: %s\n", info.getProcessName(), info.getPid(), date.toString());
                        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
                }
        }
}