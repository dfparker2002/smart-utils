package com.aem.smart.utils.hc.configuration;

import com.aem.smart.utils.hc.configuration.util.FglConfiguration;
import com.aem.smart.utils.hc.configuration.util.PropertiesLogger;
import com.aem.smart.utils.hc.configuration.util.ReplicationAgentsConfigurationsLogger;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Checks provided mandatory configurations.
 */
@SlingHealthCheck(
        name = "Mandatory Configuration Health Check",
        label = "Mandatory Configuration Health Check",
        description = "Checks Mandatory configurations to conform ones from runmodes",
        tags = { "configuration" }
)
public class MandatoryConfigurationsHealthCheck extends AbstractExtendedConfigurationsHealthCheck {
    //TODO: should be configured, without project names
    @Property(
            cardinality = Integer.MAX_VALUE,
            value = {
                    "com.fglsports.wcm.configuration.impl.WebsiteConfigurationHolderImpl",
                    "com.fglsports.wcm.site.rest.impl.HybrisRestConnectionConfigImpl",
                    "com.fglsports.wcm.hybris.sync.pull.impl.store.scheduler.StoreImporterJob",
                    "com.fglsports.wcm.hybris.sync.pull.impl.product.bundles.scheduler.ProductBundleImporterJob",
                    "com.fglsports.wcm.hybris.sync.pull.impl.product.bundles.ProductBundleImporter",
                    "com.fglsports.wcm.hybris.sync.pull.impl.store.scheduler.StoreImporterJob",
                    "com.fglsports.wcm.services.brand.BrandPageGenerationScheduler",
                    "com.fglsports.wcm.services.feed.powerreviews.products.PowerReviewsFeedExporter",
                    "com.fglsports.wcm.services.power.reviews.PowerReviewsConfiguration",
                    "com.fglsports.wcm.services.feed.recs.products.RECSProductFeedExporter",
                    "com.fglsports.wcm.services.inventory.availability.CodiAvailabilityStorageCacheFillerImpl",
                    "com.fglsports.wcm.services.inventory.availability.CodiAvailabilityStorageCacheFillScheduler",
                    "com.fglsports.wcm.services.inventory.availability.ProductAvailabilityServiceImpl",
                    "com.fglsports.wcm.services.searchpromote.SearchAndPromoteConfiguration",
                    "com.fglsports.wcm.services.searchpromote.index.SearchAndPromoteIndexRunnerImpl",
                    "com.fglsports.wcm.services.feed.searchpromote.products.ProductsFullFeedExporter",
                    "com.fglsports.wcm.services.feed.searchpromote.bundles.BundlesFullFeedExporter",
                    "com.fglsports.wcm.services.feed.searchpromote.banners.BannersFullFeedExporter",
                    "com.fglsports.wcm.services.feed.pla.products.PlaProductFeedExporter",
                    "com.fglsports.wcm.scene7.impl.Scene7ConfigWrapperImpl",
                    "com.fglsports.wcm.services.report.AllProductsReportScheduler",
                    "com.fglsports.wcm.services.report.NewProductsReportScheduler",
                    "com.fglsports.wcm.services.report.PublishedProductsReportScheduler",
                    "com.fglsports.wcm.services.report.EndDateProductsReportScheduler",
                    "com.fglsports.wcm.services.bizrate.BizrateConfiguration",
                    "com.day.cq.commons.impl.ExternalizerImpl",
                    "com.fglsports.wcm.rest.services.FacebookAuthProxyServlet",
                    "com.day.cq.mailer.DefaultMailService",
                    "com.fglsports.wcm.services.CanadaPostConfiguration",
                    "com.fglsports.wcm.services.profiler.ThreatMetrixProfilerConfiguration",
                    "com.fglsports.wcm.rollout.schedulers.RolloutEntireStoreScheduler"
            },
            label = "Configurations to check"
    )
    private static final String CONFIGURATIONS_TO_CHECK_PROPERTY = "configurations.to.check";

    @Reference
    private ReplicationAgentsConfigurationsLogger replicationAgentsConfigurationsLogger;

    private String[] configurationsToCheck = ArrayUtils.EMPTY_STRING_ARRAY;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);
        Objects.requireNonNull(replicationAgentsConfigurationsLogger, "No reference to ReplicationAgentsConfigurationsLoader");

        this.configurationsToCheck = PropertiesUtil.toStringArray(
                properties.get(CONFIGURATIONS_TO_CHECK_PROPERTY), ArrayUtils.EMPTY_STRING_ARRAY
        );
    }

    protected void execute(FormattingResultLog resultLog) {

        Multimap<String, FglConfiguration> runmodesConfigurationsMap = getRunmodeConfigurationsLoader()
                .loadConfigurations(Arrays.asList(configurationsToCheck), resultLog);

        Multimap<String, FglConfiguration> actualConfigurationsMap = getActualConfigurationsLoader()
                .loadConfigurations(Arrays.asList(configurationsToCheck), resultLog);

        for (String servicePid : configurationsToCheck) {
            Collection<FglConfiguration> actualProperties = actualConfigurationsMap.get(servicePid);
            Collection<FglConfiguration> runmodeProperties = runmodesConfigurationsMap.get(servicePid);
            Map<String, Collection<Object>> runmodePropertiesMap = toMultimap(runmodeProperties).asMap();

            PropertiesLogger propertiesLogger = new PropertiesLogger(servicePid);

            if (actualProperties.isEmpty()) {
                resultLog.warn(propertiesLogger.log());
                continue;
            }

            for (FglConfiguration actualConfiguration : actualProperties) {
                Map<String, Object> actualPropertiesMap = actualConfiguration.getProperties();
                checkMatches(actualPropertiesMap, runmodePropertiesMap, propertiesLogger);
            }

            resultLog.info(propertiesLogger.log());
        }

        replicationAgentsConfigurationsLogger.logProperties(resultLog);
    }

}
