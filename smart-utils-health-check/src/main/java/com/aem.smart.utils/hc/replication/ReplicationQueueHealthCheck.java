package com.aem.smart.utils.hc.replication;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationQueue;
import java.util.List;
import java.util.Map;

import com.aem.smart.utils.hc.AbstractRunmodeAwareHealthCheck;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.util.FormattingResultLog;

@SlingHealthCheck(
        name = "Adobe Granite Replication Queue Health Check",
        label = "Adobe Granite Replication Queue Health Check",
        description = "This health check checks the replication queue.",
        tags = { "cq", "replication" }
)
public class ReplicationQueueHealthCheck extends AbstractRunmodeAwareHealthCheck {

    private static final int DEFAULT_NUMBER_OF_RETRIES_ALLOWED = 3;

    @Property(intValue = { DEFAULT_NUMBER_OF_RETRIES_ALLOWED }, label = "Number of Allowed Retries",
            description = "This is the number of allowed retries for an entry.")
    private static final String NUMBER_OF_RETRIES_ALLOWED = "numberOfRetriesAllowed";

    @Reference(policy = ReferencePolicy.DYNAMIC)
    private AgentManager agentManager;

    private int numberOfRetriesAllowed;

    @Override
    public void activate(Map<String, Object> properties) {
        super.activate(properties);

        this.numberOfRetriesAllowed = PropertiesUtil.toInteger(properties.get(NUMBER_OF_RETRIES_ALLOWED), 3);
        getLogger().info("Activated, numberOfRetriesAllowed = {}", this.numberOfRetriesAllowed);
    }

    @Override
    protected void execute(String siteName, FormattingResultLog resultLog) {

        int failures = 0;

        Map<String, Agent> agents = this.agentManager.getAgents();

        if (MapUtils.isEmpty(agents)) {
            resultLog.debug("No replication agents configured");
            return;
        }

        for (Map.Entry<String, Agent> agentEntry : agents.entrySet()) {
            String name = agentEntry.getKey();
            Agent agent = agentEntry.getValue();
            try {

                if (!agent.isEnabled()) {
                    resultLog.debug("Agent is disabled [{}]", name);
                    continue;
                }

                List<ReplicationQueue.Entry> entries = agent.getQueue().entries();

                if (CollectionUtils.isNotEmpty(entries)) {
                    ReplicationQueue.Entry topQueueEntry = entries.get(0);
                    if (topQueueEntry.getNumProcessed() <= this.numberOfRetriesAllowed) {
                        resultLog.debug("Agent: [{}], first item: [{}], number of retries: {}", name, topQueueEntry.getId(), topQueueEntry.getNumProcessed());
                    } else {
                        resultLog.warn("Agent: [{}], first item: [{}], number of retries: {}, expected number of retries <= {}",
                                       name, topQueueEntry.getId(), topQueueEntry.getNumProcessed(), this.numberOfRetriesAllowed);
                        failures++;
                    }
                } else {
                    resultLog.debug("No items in queue for agent [{}]", name);
                }

            } catch (Exception e) {
                resultLog.warn("Exception while inspecting replication agent [{}]: {}", name, e);
            }
        }

        if (failures > 0) {
            resultLog.info("[Click here to inspect the queue for configured replication agents] (/libs/granite/replication/content/admin.html)");
            resultLog.info("[Jump to Diagnosis page and see replication specific log entries] (/libs/granite/operations/config/diagnosis/logmessages.html)");
        }
    }

}
