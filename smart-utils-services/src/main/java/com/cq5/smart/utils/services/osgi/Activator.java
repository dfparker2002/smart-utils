package com.cq5.smart.utils.services.osgi;

import com.cq5.smart.utils.services.osgi.smartlogger.SmartLogView;
import com.squeakysand.osgi.framework.BasicBundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

public class Activator extends BasicBundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    public Activator() {
		super(LOG);
	}

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);

        context.registerService(Servlet.class.getName(),
                new SmartLogView(),
                SmartLogView.getParams());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }
}
