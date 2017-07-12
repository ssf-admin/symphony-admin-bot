package com.symphony.api.multipart;

import com.symphony.api.multipart.model.Request;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.symphonyoss.symphony.pod.invoker.ApiException;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 7/11/17.
 *
 * CREATED FOR TESTING PURPOSES
 */
public class MultiPartClient<T> {
  private final String baseUrl;
  private final Client podClient;

  /**
   * Jersey-based client made to be used on MultiPart data requests (not supported by swagger
   * client), and also to allow some specific operations that SymphonyClient doesn't have
   * implemented (setting custom headers, etc.)
   * @param baseUrl
   */
  public MultiPartClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.podClient = createPodClient();
  }

  /**
   * Creates a Jersey client for calling endpoints
   */
  private Client createPodClient() {
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.register(MultiPartFeature.class);

    // Connect and read timeouts in milliseconds
    clientConfig.property(ClientProperties.READ_TIMEOUT, 10000);
    clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 10000);

    return ClientBuilder.newBuilder().withConfig(clientConfig).build();
  }

  public T doGet(Request request) throws ApiException {
    Invocation.Builder builder = buildApiRequest(request);

    Response response = builder.get();

    return getResponse(request, response);
  }

  public T doPost(Request request) throws ApiException {
    Invocation.Builder builder = buildApiRequest(request);

    Response response = builder.post(request.getPayload());

    return getResponse(request, response);
  }

  public T doPut(Request request) throws ApiException {
    Invocation.Builder builder = buildApiRequest(request);

    Response response = builder.put(request.getPayload());

    return getResponse(request, response);
  }

  public T doDelete(Request request) throws ApiException {
    Invocation.Builder builder = buildApiRequest(request);

    Response response = builder.delete();

    return getResponse(request, response);
  }

  private Invocation.Builder buildApiRequest(Request request) {
    WebTarget target = podClient.target(this.baseUrl).path(request.getPath());
    target = buildParamsFromMap(target, request.getParams());

    Invocation.Builder builder = target.request(request.getMediaType());
    buildHeadersFromMap(builder, request.getHeaders());
    buildCookiesFromMap(builder, request.getCookies());

    return builder;
  }

  private T getResponse(Request request, Response response) throws ApiException {
    if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
      return (T) response.readEntity(request.getReturnObjectType());
    } else {
      String stringError = response.readEntity(String.class);
      throw new ApiException(response.getStatusInfo().getStatusCode(), "Error: " + stringError);
    }

  }

  private WebTarget buildParamsFromMap(WebTarget target, MultivaluedMap<String, String> params) {
    WebTarget queryTarget = target;
    if (params != null) {
      for (Map.Entry<String, List<String>> entry : params.entrySet()) {
        List<String> paramList = entry.getValue();
        String csvParamList = StringUtils.join(paramList, ',');
        queryTarget = queryTarget.queryParam(entry.getKey(), csvParamList);
      }
    }

    return queryTarget;
  }

  private void buildCookiesFromMap(Invocation.Builder builder, Map<String, String> cookies) {
    if (cookies != null) {
      for (Map.Entry<String, String> entry : cookies.entrySet()) {
        builder.cookie(entry.getKey(), entry.getValue());
      }
    }
  }

  private void buildHeadersFromMap(Invocation.Builder builder, Map<String, String> headers) {
    if (headers != null) {
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        builder.header(entry.getKey(), entry.getValue());
      }
    }
  }
}
