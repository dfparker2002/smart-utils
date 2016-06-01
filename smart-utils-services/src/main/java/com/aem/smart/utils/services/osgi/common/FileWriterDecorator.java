package com.aem.smart.utils.services.osgi.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.smart.utils.services.osgi.smartlogger.TailThread;

/**
 * The type File writer decorator.
 * Decorator for Print Writer. Can output a text file line by line.
 */
public class FileWriterDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWriterDecorator.class);

    private final PrintWriter writer;

    /**
     * Instantiates a new File writer decorator.
     *
     * @param writer the writer
     */
    public FileWriterDecorator(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Print file.
     *
     * @param file   the file
     * @param filter the filter
     * @throws IOException the io exception
     */
    public void printFile(File file, LogFilter filter) throws IOException {
        TailThread tailThread = new TailThread(file, writer, LOGGER, filter);
        tailThread.run();
    }
}
