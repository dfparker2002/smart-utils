package com.cq5.smart.utils.services.osgi.common;

import org.slf4j.Logger;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Andrii_Manuiev
 * Date: 20.05.13
 * Time: 17:03
 * The thread that tail a log file.
 */
public class TailThread implements Runnable {

    private BufferedReader bufferedReader;
    private PrintWriter writer;
    boolean execute = true;

    private Logger logger;

    public TailThread(File file, PrintWriter writer, Logger logger) throws FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(file));
        this.writer = writer;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            while (execute) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    writer.println(line);
                    writer.flush();
                } else {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                        execute = false;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading log file", e);
        }
    }

}
