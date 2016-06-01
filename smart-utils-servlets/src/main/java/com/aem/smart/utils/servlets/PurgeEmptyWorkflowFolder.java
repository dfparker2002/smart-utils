package com.aem.smart.utils.servlets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@SlingServlet(paths = PurgeEmptyWorkflowFolder.MAPPING_PATH, label = "Utils - purge empty workflow folders.")
public class PurgeEmptyWorkflowFolder extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeEmptyWorkflowFolder.class);

    static final String MAPPING_PATH = "/services/utils/purgeEmptyFolder";

    private static final String PATH = "/etc/workflow/instances";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException,
            IOException {
        final ResourceResolver resourceResolver = request.getResourceResolver();

        final Resource resource = resourceResolver.getResource(PATH);

        List<Resource> toDelete = Lists.newArrayList();

        if (resource == null) {
            response.getWriter().println("Fail to get path to instances.");
            LOG.error("Fail to retrieve path.");
        } else {
            final Iterator<Resource> iterator = resource.listChildren();
            response.getWriter().println("Start seeking empty folders.");
            while (iterator.hasNext()) {
                Resource folder = iterator.next();

                if (folder.isResourceType("sling:Folder") && Iterables.size(folder.getChildren()) == 0) {
                    response.getWriter().println("Found: " + folder.getPath());
                    toDelete.add(folder);
                }
            }
        }
        response.getWriter().println("Start deletion process.");
        for (Resource folderToDelete : toDelete) {
            resourceResolver.delete(folderToDelete);
        }
        response.getWriter().println("Try to persist changes.");
        resourceResolver.commit();
        resourceResolver.close();
    }
}
