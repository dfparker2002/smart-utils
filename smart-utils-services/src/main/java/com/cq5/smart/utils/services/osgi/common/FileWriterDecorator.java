package com.cq5.smart.utils.services.osgi.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: Andrii_Manuiev
 * Date: 20.05.13
 * Time: 15:51
 * Decorator for Print Writer. Can output a text file line by line.
 */
public class FileWriterDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWriterDecorator.class);

    private final PrintWriter writer;

    private Charset utf8 = Charset.forName("UTF-8");

    public FileWriterDecorator(PrintWriter writer) {
        this.writer = writer;
    }

    public void print(String text) {
        writer.println(text);
    }

    public void printFile(File file) throws IOException {
        TailThread tailThread = new TailThread(file, writer, LOGGER);
        tailThread.run();
    }
}
