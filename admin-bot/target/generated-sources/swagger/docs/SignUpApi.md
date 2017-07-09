# SignUpApi

All URIs are relative to *http://localhost/adminbot*

Method | HTTP request | Description
------------- | ------------- | -------------
[**v1BootstrapPartnerPost**](SignUpApi.md#v1BootstrapPartnerPost) | **POST** /v1/bootstrapPartner | Bootstraps a partner.
[**v1SendPartnerWelcomePost**](SignUpApi.md#v1SendPartnerWelcomePost) | **POST** /v1/sendPartnerWelcome | Sends user welcome messages and emails.


<a name="v1BootstrapPartnerPost"></a>
# **v1BootstrapPartnerPost**
> PartnerBootstrapInfo v1BootstrapPartnerPost(sessionToken, keyManagerToken, partner)

Bootstraps a partner.

If not already created - Creates and registers bot cert, creates bot user. Creates and registers app cert, creates app. Sends partner bootstrap package within symphony.

### Example
```java
// Import classes:
//import com.symphony.api.adminbot.client.ApiException;
//import com.symphony.api.adminbot.api.SignUpApi;


SignUpApi apiInstance = new SignUpApi();
String sessionToken = "sessionToken_example"; // String | 
String keyManagerToken = "keyManagerToken_example"; // String | 
Partner partner = new Partner(); // Partner | 
try {
    PartnerBootstrapInfo result = apiInstance.v1BootstrapPartnerPost(sessionToken, keyManagerToken, partner);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling SignUpApi#v1BootstrapPartnerPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sessionToken** | **String**|  |
 **keyManagerToken** | **String**|  |
 **partner** | [**Partner**](Partner.md)|  |

### Return type

[**PartnerBootstrapInfo**](PartnerBootstrapInfo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="v1SendPartnerWelcomePost"></a>
# **v1SendPartnerWelcomePost**
> v1SendPartnerWelcomePost(sessionToken, keyManagerToken, signUpForm)

Sends user welcome messages and emails.

Creates the users in the pod, as defined within the sign up form. Sends welcome emails with username and temporary password. Sends directional welcome message to users, for when they login.

### Example
```java
// Import classes:
//import com.symphony.api.adminbot.client.ApiException;
//import com.symphony.api.adminbot.api.SignUpApi;


SignUpApi apiInstance = new SignUpApi();
String sessionToken = "sessionToken_example"; // String | 
String keyManagerToken = "keyManagerToken_example"; // String | 
PartnerSignUpForm signUpForm = new PartnerSignUpForm(); // PartnerSignUpForm | 
try {
    apiInstance.v1SendPartnerWelcomePost(sessionToken, keyManagerToken, signUpForm);
} catch (ApiException e) {
    System.err.println("Exception when calling SignUpApi#v1SendPartnerWelcomePost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sessionToken** | **String**|  |
 **keyManagerToken** | **String**|  |
 **signUpForm** | [**PartnerSignUpForm**](PartnerSignUpForm.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

