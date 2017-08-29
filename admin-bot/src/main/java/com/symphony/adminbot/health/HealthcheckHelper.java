package com.symphony.adminbot.health;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 8/23/17.
 */
public class HealthcheckHelper {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private WebTarget podHealthCheckTarget;
  private WebTarget agentHealthCheckTarget;

  public HealthcheckHelper(String podUrl, String agentUrl) {
    WebTarget agentBaseTarget = ClientBuilder.newClient().target(agentUrl);
    WebTarget podBaseTarget = ClientBuilder.newClient().target(podUrl);

    podHealthCheckTarget = podBaseTarget.path("v1/podcert");
    agentHealthCheckTarget = agentBaseTarget.path("v1/healthcheck");
  }

  private Response checkConnectivity(WebTarget target) throws HealthCheckFailedException {
    try {
      Response response = target.request().get();

      if (response.getStatus() != 200) {
        throw new HealthCheckFailedException("The request to " + target.getUri()
            + " returned status code " + response.getStatus() + "'");
      }

      return response;
    } catch (ProcessingException e) {
      throw new HealthCheckFailedException("The URI " + target.getUri()
          + " couldn't be reached because of '" + e.getMessage() + "'");
    }
  }

  public void checkPodConnectivity() throws HealthCheckFailedException {
    checkConnectivity(podHealthCheckTarget).close();
  }

  public void checkAgentConnectivity() throws HealthCheckFailedException {
    Response response = checkConnectivity(agentHealthCheckTarget);
    JsonNode node = null;
    try {
      node = MAPPER.readTree((InputStream) response.getEntity());
    } catch (IOException e) {
      throw new HealthCheckFailedException("Failed to read response entity.");
    }

    String podError = node.get("podConnectivityError").asText();
    String agentError = node.get("keyManagerConnectivity").asText();

    if(StringUtils.isNotBlank(podError) && StringUtils.isNotBlank(agentError)) {
      throw new HealthCheckFailedException("The pod and the agent are currently having issues. \n"
          + "Pod health check error: " + podError + " \nAgent health check error: " + agentError);
    } else if(StringUtils.isNotBlank(podError)) {
      throw new HealthCheckFailedException("The pod is currently having issues. \n"
          + "Pod health check error: " + podError);
    } else if(StringUtils.isNotBlank(agentError)) {
      throw new HealthCheckFailedException("The agent is currently having issues. \n"
          + "Agent health check error: " + agentError);
    }

    response.close();
  }
}
