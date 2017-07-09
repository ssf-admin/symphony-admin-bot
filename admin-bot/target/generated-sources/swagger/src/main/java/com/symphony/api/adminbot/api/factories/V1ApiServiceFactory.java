package com.symphony.api.adminbot.api.factories;

import com.symphony.api.adminbot.api.V1ApiService;
import com.symphony.api.adminbot.api.impl.V1ApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-07-08T23:51:55.095-07:00")
public class V1ApiServiceFactory {
    private static V1ApiService service = new V1ApiServiceImpl();

    public static V1ApiService getV1Api(){
        return service;
    }

    public static void setService(V1ApiService service){
        V1ApiServiceFactory.service = service;
    }
}