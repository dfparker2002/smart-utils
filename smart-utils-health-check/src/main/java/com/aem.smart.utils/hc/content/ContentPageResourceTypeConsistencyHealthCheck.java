package com.aem.smart.utils.hc.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;
import org.apache.sling.jcr.api.SlingRepository;

import com.aem.smart.utils.commons.jcr.SessionHolder;
import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;

/**
 * Traverses through pages in '/content/&lt;website-name&gt;/en/' and verifies their 'sling:resourceType' against actual
 * pages under '/apps/&lt;website-name&gt;/pages/'.
 */
@SlingHealthCheck(name = "Content Pages Resource Type Consistency Health Check", label = "Content Pages Resource Type Consistency Health Check", description = "Content Pages Resource Type Consistency Health Check", tags = {
        "cq", "content", "consistency" })
public class ContentPageResourceTypeConsistencyHealthCheck extends AbstractRunmodeAwareHealthCheck {

    private static final String CONTENT_PAGES_QUERY = "SELECT * FROM [cq:PageContent] AS contentPage "
            + "WHERE ISDESCENDANTNODE(contentPage, '%s')";

    private static final String APPS_PAGES_QUERY = "SELECT * FROM [cq:Component] AS page "
            + "WHERE ISDESCENDANTNODE(page,'/apps/%s')";

    private static final String SLING_RESOURCE_TYPE = "sling:resourceType";
    private static final String APPS_PREFIX = "/apps/";

    @Reference
    private SlingRepository slingRepository;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(slingRepository, "No reference to SlingRepository");
    }

    @Override
    protected void execute(String siteName, FormattingResultLog resultLog) {

        try (SessionHolder holder = new SessionHolder(slingRepository)) {

            QueryManager queryManager = holder.getSession().getWorkspace().getQueryManager();

            Map<String, String> resourceTypesMap = new HashMap<>();
            resourceTypesMap.putAll(collectContentPagesResourceTypes("/content/" + siteName, queryManager));

            Set<String> sportchekPagesSet = collectSitePages(siteName, queryManager);

            if (sportchekPagesSet.isEmpty()) {
                resultLog.warn("Cannot find any app pages...");
            } else {
                validateResourceType(resourceTypesMap, sportchekPagesSet, resultLog);
            }

        } catch (RepositoryException ex) {
            getLogger().error("Failed to check content pages resource types consistency", ex);

        }
    }

    private Map<String, String> collectContentPagesResourceTypes(String pathToCheck, QueryManager queryManager)
            throws RepositoryException {

        Map<String, String> result = new HashMap<>();

        String queryStatement = String.format(CONTENT_PAGES_QUERY, pathToCheck);
        Query query = queryManager.createQuery(queryStatement, Query.JCR_SQL2);
        NodeIterator nodeIterator = query.execute().getNodes();

        while (nodeIterator.hasNext()) {
            final Node contentPageNode = nodeIterator.nextNode();
            if (contentPageNode.hasProperty(SLING_RESOURCE_TYPE)) {
                String resourceType = contentPageNode.getProperty(SLING_RESOURCE_TYPE).getValue().getString();
                result.put(contentPageNode.getPath(), resourceType);
            }
        }
        return result;
    }

    private Set<String> collectSitePages(String siteName, QueryManager queryManager) throws RepositoryException {
        Set<String> result = new HashSet<>();

        Query query = queryManager.createQuery(String.format(APPS_PAGES_QUERY, siteName), Query.JCR_SQL2);
        NodeIterator nodeIterator = query.execute().getNodes();
        while (nodeIterator.hasNext()) {
            result.add(nodeIterator.nextNode().getPath().replace(APPS_PREFIX, ""));
        }
        return result;
    }

    private void validateResourceType(Map<String, String> resourceTypesMap, Collection<String> pagesResourceTypes,
            FormattingResultLog resultLog) {

        for (Map.Entry<String, String> entry : resourceTypesMap.entrySet()) {
            String contentPagePath = entry.getKey();
            String resourceTypeToCheck = entry.getValue();

            if (!pagesResourceTypes.contains(resourceTypeToCheck)) {
                resultLog.warn("Invalid resource type '{}' was found for '{}'", resourceTypeToCheck, contentPagePath);
            }
        }
    }
}
