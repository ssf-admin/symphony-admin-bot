package com.symphony.api.adminbot.api;

import com.symphony.api.adminbot.api.*;
import com.symphony.api.adminbot.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerBootstrapInfo;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import java.util.List;
import com.symphony.api.adminbot.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-07-08T23:51:55.095-07:00")
public abstract class V1ApiService {
    public abstract Response v1BootstrapPartnerPost(String sessionToken,String keyManagerToken,Partner partner,SecurityContext securityContext) throws NotFoundException;
    public abstract Response v1SendPartnerWelcomePost(String sessionToken,String keyManagerToken,PartnerSignUpForm signUpForm,SecurityContext securityContext) throws NotFoundException;
}
