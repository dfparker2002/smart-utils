README v${project.version}

### ABOUT THIS MODULE

This module contains various health checks for CQ/AEM environment untilizing Apache Sling Healthcheck library/framework. The purpose is to verify readiness of CQ/AEM environment (after redeployment) for any operations by performing quick checking of:
 - Validity of runmodes configurations
 - Initial content setup
 - Hybris connectivity check
 - Presence of mandatory attributes of specific pages/nodes
 - etc

### PREREQUISISTES

In order to make health checks working, the following bundles must be installed in OSGi Felix console manually:
 - org.apache.sling.hc.core         (version '1.2.2')
 - org.apache.sling.hc.support      (version '1.0.4')
 - org.apache.sling.hc.webconsole   (version '1.1.2')

### DEVELOPMENT NOTES
TBD