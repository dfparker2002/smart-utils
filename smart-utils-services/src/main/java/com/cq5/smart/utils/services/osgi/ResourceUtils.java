package com.cq5.smart.utils.services.osgi;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

    private ResourceUtils() {
    }

    public static String readTemplateFile(Class clazz, String templateFile) {

        InputStream templateStream = clazz.getResourceAsStream(templateFile);
        if (templateStream != null) {
            try {
                return IOUtils.toString(templateStream, "UTF-8");
            } catch (IOException e) {
                LOGGER.info("readTemplateFile: File '%s' not found through class {}", templateFile, clazz);
            } finally {
                IOUtils.closeQuietly(templateStream);
            }
        }
        return StringUtils.EMPTY;
    }
}
