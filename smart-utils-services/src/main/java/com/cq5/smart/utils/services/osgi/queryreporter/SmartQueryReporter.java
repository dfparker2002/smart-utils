package com.cq5.smart.utils.services.osgi.queryreporter;import org.apache.felix.scr.annotations.sling.SlingServlet;import org.apache.sling.api.servlets.SlingAllMethodsServlet;/** * User: Andrii_Manuiev * Date: 22.05.13 * Find a nodes by query. Output node as a flat list which can be saved in file. */@SlingServlet(metatype = false, paths = "/services/smart-utils/queryreporter")public class SmartQueryReporter extends SlingAllMethodsServlet {}