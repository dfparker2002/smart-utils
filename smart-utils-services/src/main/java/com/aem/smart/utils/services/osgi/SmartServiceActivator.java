package com.aem.smart.utils.services.osgi;

import javax.servlet.Servlet;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.smart.utils.services.osgi.smartlogger.SmartLogView;

public class SmartServiceActivator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(SmartServiceActivator.class);

    private SimpleWebConsolePlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        LOG.info("Start service - logger watcher");

        context.registerService(Servlet.class.getName(), new SmartLogView(), SmartLogView.getParams());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
