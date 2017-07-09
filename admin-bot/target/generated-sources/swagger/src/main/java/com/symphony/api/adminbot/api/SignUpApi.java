package com.symphony.api.adminbot.api;

import com.symphony.api.adminbot.client.ApiException;
import com.symphony.api.adminbot.client.ApiClient;
import com.symphony.api.adminbot.client.Configuration;
import com.symphony.api.adminbot.client.Pair;

import javax.ws.rs.core.GenericType;

import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerBootstrapInfo;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-07-08T23:51:55.809-07:00")
public class SignUpApi {
  private ApiClient apiClient;

  public SignUpApi() {
    this(Configuration.getDefaultApiClient());
  }

  public SignUpApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Bootstraps a partner.
   * If not already created - Creates and registers bot cert, creates bot user. Creates and registers app cert, creates app. Sends partner bootstrap package within symphony.
   * @param sessionToken  (required)
   * @param keyManagerToken  (required)
   * @param partner  (required)
   * @return PartnerBootstrapInfo
   * @throws ApiException if fails to make API call
   */
  public PartnerBootstrapInfo v1BootstrapPartnerPost(String sessionToken, String keyManagerToken, Partner partner) throws ApiException {
    Object localVarPostBody = partner;
    
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new ApiException(400, "Missing the required parameter 'sessionToken' when calling v1BootstrapPartnerPost");
    }
    
    // verify the required parameter 'keyManagerToken' is set
    if (keyManagerToken == null) {
      throw new ApiException(400, "Missing the required parameter 'keyManagerToken' when calling v1BootstrapPartnerPost");
    }
    
    // verify the required parameter 'partner' is set
    if (partner == null) {
      throw new ApiException(400, "Missing the required parameter 'partner' when calling v1BootstrapPartnerPost");
    }
    
    // create path and map variables
    String localVarPath = "/v1/bootstrapPartner".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    if (sessionToken != null)
      localVarHeaderParams.put("sessionToken", apiClient.parameterToString(sessionToken));
if (keyManagerToken != null)
      localVarHeaderParams.put("keyManagerToken", apiClient.parameterToString(keyManagerToken));

    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<PartnerBootstrapInfo> localVarReturnType = new GenericType<PartnerBootstrapInfo>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * Sends user welcome messages and emails.
   * Creates the users in the pod, as defined within the sign up form. Sends welcome emails with username and temporary password. Sends directional welcome message to users, for when they login.
   * @param sessionToken  (required)
   * @param keyManagerToken  (required)
   * @param signUpForm  (optional)
   * @throws ApiException if fails to make API call
   */
  public void v1SendPartnerWelcomePost(String sessionToken, String keyManagerToken, PartnerSignUpForm signUpForm) throws ApiException {
    Object localVarPostBody = signUpForm;
    
    // verify the required parameter 'sessionToken' is set
    if (sessionToken == null) {
      throw new ApiException(400, "Missing the required parameter 'sessionToken' when calling v1SendPartnerWelcomePost");
    }
    
    // verify the required parameter 'keyManagerToken' is set
    if (keyManagerToken == null) {
      throw new ApiException(400, "Missing the required parameter 'keyManagerToken' when calling v1SendPartnerWelcomePost");
    }
    
    // create path and map variables
    String localVarPath = "/v1/sendPartnerWelcome".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    if (sessionToken != null)
      localVarHeaderParams.put("sessionToken", apiClient.parameterToString(sessionToken));
if (keyManagerToken != null)
      localVarHeaderParams.put("keyManagerToken", apiClient.parameterToString(keyManagerToken));

    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };


    apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }
}
