package com.aem.smart.utils.hc;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 *
 */
@Component(componentAbstract = true)
@Properties({
        @Property(name = HealthCheck.NAME, label = "Health Check Name",
                description = "Name of this Health Check service."),
        @Property(name = HealthCheck.TAGS, unbounded = PropertyUnbounded.ARRAY, label = "Health Check tags",
                description = "List of tags for this Health Check service, used to select subsets of Health Check services for execution"),
        @Property(name = HealthCheck.MBEAN_NAME, label = "MBean Name",
                description = "Name of the MBean to create for this Health Check.")
})
abstract class AbstractConnectivityHealthCheck extends AbstractRunmodeAwareHealthCheck {

    /**
     * Return url to be checked.
     */
    public abstract String getUrlToCheck();

    /**
     * Returns simple message to be used in {@link org.apache.sling.hc.util.FormattingResultLog} log.
     */
    public abstract String getHealthCheckName();

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        getLogger().debug("{} activated", getClass().getSimpleName());
    }

    @Override
    protected void execute(FormattingResultLog resultLog) {

        try {
            String url = getUrlToCheck();

            if (url.contains("localhost")) {
                resultLog.warn("{} host points to localhost", getHealthCheckName());
                return;
            }

            CloseableHttpClient httpClient = createHttpClient(isHttps(url));

            resultLog.debug("About to check URL '{}'", url);

            CloseableHttpResponse execute = httpClient.execute(new HttpGet(url));

            int statusCode = execute.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                resultLog.info("{} connection is SUCCESS, received http status '{}'", getHealthCheckName(), statusCode);
            } else {
                resultLog.warn("{} connection is FAIL, received http status '{}'", getHealthCheckName(), statusCode);
            }

        } catch (Exception ex) {
            resultLog.healthCheckError("Failed to check connection to " + getHealthCheckName());
            getLogger().error("Failed to check connection to " + getHealthCheckName(), ex);
        }
    }

    private CloseableHttpClient createHttpClient(boolean isHttps) {
        if (isHttps) {
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), new AllowAllHostnameVerifier());
                return HttpClients.custom().setSSLSocketFactory(sslsf).build();

            } catch(NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex){
                getLogger().error("Failed to create HttpClient", ex);
            }
        }

        return HttpClients.createDefault();
    }

    private static boolean isHttps(String url) {
        return StringUtils.isNotBlank(url) && url.startsWith("https://");
    }
}
