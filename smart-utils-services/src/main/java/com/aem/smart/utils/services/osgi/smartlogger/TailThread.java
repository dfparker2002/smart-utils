package com.aem.smart.utils.services.osgi.smartlogger;

import com.aem.smart.utils.services.osgi.common.LogFilter;
import org.slf4j.Logger;

import java.io.*;

/**
 * The type Tail thread.
 * The thread that tail a log file.
 */
public class TailThread implements Runnable {

    private BufferedReader bufferedReader;
    private PrintWriter writer;
    private LogFilter filter;

    private boolean execute = true;

    private Logger logger;

    /**
     * Instantiates a new Tail thread.
     *
     * @param file   the file
     * @param writer the writer
     * @param logger the logger
     * @param filter the filter
     * @throws FileNotFoundException the file not found exception
     */
    public TailThread(File file, PrintWriter writer, Logger logger, LogFilter filter) throws FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(file));
        this.writer = writer;
        this.logger = logger;
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            while (execute) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    filter.apply(writer, line);
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
