package com.aem.smart.utils.services.osgi.components;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.iterator.NodeIteratorAdapter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.smart.utils.commons.jcr.SessionHolder;

/**
 * The type Repository query reporter.
 */
@SlingServlet(paths = RepositoryQueryReporter.MAPPING_PATH)
public class RepositoryQueryReporter extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryQueryReporter.class);

    static final String MAPPING_PATH = "/services/smart-utils/repository-query-reporter";

    private static final String CONTENT_TYPE = "application/json";
    private static final String PAGE_ENCODING = "UTF-8";

    private static final String QUERY_PARAM = "query";
    private static final String QUERY_TYPE_PARAM = "queryType";

    @Reference
    private SlingRepository repository;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(PAGE_ENCODING);

        String queryText = request.getParameter(QUERY_PARAM);
        String queryType = request.getParameter(QUERY_TYPE_PARAM);

        try (SessionHolder holder = new SessionHolder(repository)) {

            LOGGER.info("the query is {} ", queryText);
            LOGGER.info("the query type is {} ", queryType);

            NodeIterator result = executeQuery(holder.getSession(), queryText, queryType);

            PrintWriter responseWriter = response.getWriter();

            LOGGER.info("the result is {} ", result.getSize());

            while (result.hasNext()) {
                Node next = result.nextNode();
                responseWriter.println(next.getPath());
            }
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private NodeIterator executeQuery(Session session, String queryText, String queryType) throws RepositoryException {

        QueryManager queryManager = session.getWorkspace().getQueryManager();

        if (StringUtils.isEmpty(queryText) || isWrongType(queryType)) {
            LOGGER.info("The something wrong with condition");
            return new NodeIteratorAdapter(Collections.EMPTY_LIST);
        }

        final Query query = queryManager.createQuery(queryText, queryType);
        QueryResult queryResult = query.execute();

        return queryResult.getNodes();
    }

    @SuppressWarnings("deprecation")
    private boolean isWrongType(String queryType) {
        return !Query.XPATH.equals(queryType) && !Query.SQL.equals(queryType) && !Query.JCR_SQL2.equals(queryType)
                && !Query.JCR_JQOM.equals(queryType);
    }
}
