package com.symphony.api.adminbot.api;

import com.symphony.api.adminbot.model.*;
import com.symphony.api.adminbot.api.V1ApiService;
import com.symphony.api.adminbot.api.factories.V1ApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerBootstrapInfo;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import java.util.List;
import com.symphony.api.adminbot.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;

@Path("/v1")

@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the v1 API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-07-08T23:51:55.095-07:00")
public class V1Api  {
   private final V1ApiService delegate = V1ApiServiceFactory.getV1Api();

    @POST
    @Path("/bootstrapPartner")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Bootstraps a partner.", notes = "If not already created - Creates and registers bot cert, creates bot user. Creates and registers app cert, creates app. Sends partner bootstrap package within symphony.", response = PartnerBootstrapInfo.class, tags={ "SignUp", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "", response = PartnerBootstrapInfo.class) })
    public Response v1BootstrapPartnerPost(@ApiParam(value = "" ,required=true)@HeaderParam("sessionToken") String sessionToken
,@ApiParam(value = "" ,required=true)@HeaderParam("keyManagerToken") String keyManagerToken
,@ApiParam(value = "" ,required=true) Partner partner
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.v1BootstrapPartnerPost(sessionToken,keyManagerToken,partner,securityContext);
    }
    @POST
    @Path("/sendPartnerWelcome")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Sends user welcome messages and emails.", notes = "Creates the users in the pod, as defined within the sign up form. Sends welcome emails with username and temporary password. Sends directional welcome message to users, for when they login.", response = void.class, tags={ "SignUp", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class) })
    public Response v1SendPartnerWelcomePost(@ApiParam(value = "" ,required=true)@HeaderParam("sessionToken") String sessionToken
,@ApiParam(value = "" ,required=true)@HeaderParam("keyManagerToken") String keyManagerToken
,@ApiParam(value = "" ) PartnerSignUpForm signUpForm
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.v1SendPartnerWelcomePost(sessionToken,keyManagerToken,signUpForm,securityContext);
    }
}
