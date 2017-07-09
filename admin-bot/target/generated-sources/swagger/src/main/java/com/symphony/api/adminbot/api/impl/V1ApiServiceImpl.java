package com.symphony.api.adminbot.api.impl;

import com.symphony.api.adminbot.api.*;
import com.symphony.api.adminbot.model.*;

import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerBootstrapInfo;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import java.util.List;
import com.symphony.api.adminbot.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-07-08T23:51:55.095-07:00")
public class V1ApiServiceImpl extends V1ApiService {
    @Override
    public Response v1BootstrapPartnerPost(String sessionToken, String keyManagerToken, Partner partner, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response v1SendPartnerWelcomePost(String sessionToken, String keyManagerToken, PartnerSignUpForm signUpForm, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
