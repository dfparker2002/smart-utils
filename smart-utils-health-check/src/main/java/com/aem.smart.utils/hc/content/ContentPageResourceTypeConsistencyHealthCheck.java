package com.aem.smart.utils.hc.content;

import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;
import org.apache.commons.lang.ArrayUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Traverses through pages in '/content/&lt;website-name&gt;/en/' and verifies their 'sling:resourceType' against actual
 * pages under '/apps/sportchek/pages/'.
 */
@SlingHealthCheck(
        name = "Content Pages Resource Type Consistency Health Check",
        label = "Content Pages Resource Type Consistency Health Check",
        description = "Content Pages Resource Type Consistency Health Check",
        tags = { "cq", "content", "consistency" }
)
public class ContentPageResourceTypeConsistencyHealthCheck extends AbstractRunmodeAwareHealthCheck {

    private static final String CONTENT_PAGES_QUERY = "SELECT * FROM [cq:PageContent] AS contentPage "
            + "WHERE ISDESCENDANTNODE(contentPage, '%s')";

    private static final String SPORTCHEK_PAGES_QUERY = "SELECT * FROM [cq:Component] AS page " +
            "WHERE ISDESCENDANTNODE(page,'/apps/sportchek/pages')";

    private static final String SLING_RESOURCE_TYPE = "sling:resourceType";
    private static final String APPS_PREFIX = "/apps/";
    private static final String SPORTCHEK_RESOURCE_TYPE_PREFIX = "sportchek";

    @Property(
            cardinality = Integer.MAX_VALUE,
            value = { "/content/atmosphere/en/" },
            label = "Content pages path to check"
    )
    private static final String PATHS_TO_CHECK_PROPERTY = "paths.to.check";

    @Reference
    private SlingRepository slingRepository;

    private String[] pathsToCheck = ArrayUtils.EMPTY_STRING_ARRAY;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        Objects.requireNonNull(slingRepository, "No reference to SlingRepository");

        this.pathsToCheck = PropertiesUtil.toStringArray(
                properties.get(PATHS_TO_CHECK_PROPERTY), ArrayUtils.EMPTY_STRING_ARRAY
        );
    }

    @Override
    protected void execute(final FormattingResultLog resultLog) {

        Session session = null;
        try {
            session = slingRepository.loginAdministrative(null);
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            Map<String, String> resourceTypesMap = new HashMap<>();
            for (String pathToCheck : pathsToCheck) {
                resourceTypesMap.putAll(collectContentPagesSportchekResourceTypes(pathToCheck, queryManager));
            }

            Set<String> sportchekPagesSet = collectSportchekPages(queryManager);

            if (sportchekPagesSet.isEmpty()) {
                resultLog.warn("Cannot find any sportchek pages...");
            } else {
                validateResourceType(resourceTypesMap, sportchekPagesSet, resultLog);
            }

        } catch (RepositoryException ex) {
            getLogger().error("Failed to check content pages resource types consistency", ex);

        } finally {
            if (null != session && session.isLive()) {
                session.logout();
            }
        }
    }

    private Map<String, String> collectContentPagesSportchekResourceTypes(String pathToCheck, QueryManager queryManager)
            throws RepositoryException {

        Map<String, String> result = new HashMap<>();

        String queryStatement = String.format(CONTENT_PAGES_QUERY, pathToCheck);
        Query query = queryManager.createQuery(queryStatement, Query.JCR_SQL2);
        NodeIterator nodeIterator = query.execute().getNodes();

        while (nodeIterator.hasNext()) {
            final Node contentPageNode = nodeIterator.nextNode();
            if (contentPageNode.hasProperty(SLING_RESOURCE_TYPE)) {
                String resourceType = contentPageNode.getProperty(SLING_RESOURCE_TYPE).getValue().getString();
                if (resourceType.startsWith(SPORTCHEK_RESOURCE_TYPE_PREFIX)) {
                    result.put(contentPageNode.getPath(), resourceType);
                }
            }
        }

        return result;
    }

    private Set<String> collectSportchekPages(QueryManager queryManager) throws RepositoryException {
        Set<String> result = new TreeSet<>();

        Query query = queryManager.createQuery(SPORTCHEK_PAGES_QUERY, Query.JCR_SQL2);
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
