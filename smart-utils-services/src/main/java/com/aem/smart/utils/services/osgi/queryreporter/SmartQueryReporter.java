package com.aem.smart.utils.services.osgi.queryreporter;import org.apache.felix.scr.annotations.sling.SlingServlet;import org.apache.sling.api.servlets.SlingAllMethodsServlet;/** * The type Smart query reporter. * Find a nodes by query. Output node as a flat list which can be saved in file. */@SlingServlet(paths = "/services/smart-utils/queryreporter")public class SmartQueryReporter extends SlingAllMethodsServlet {    //TODO : all of them :)}