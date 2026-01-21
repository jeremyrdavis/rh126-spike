package com.redhat.demos.redhatone2026;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class IndexResource {

    @GET
    public Response index() {
        return Response.seeOther(URI.create("/app.html")).build();
    }
}
