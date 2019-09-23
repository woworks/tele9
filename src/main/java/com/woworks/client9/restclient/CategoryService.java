package com.woworks.client9.restclient;

import com.woworks.client9.model.Advert;
import com.woworks.client9.model.Categories;
import com.woworks.client9.model.Category;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Set;

@Path("")
@ClientHeaderParam(name = "Authorization", value = "{generateAuthHeader}")
@RegisterRestClient
public interface CategoryService extends BasicService {
    @GET
    @Path("/name/{name}")
    @Produces("application/json")
    Set<Category> getByName(@PathParam("name") String name);

    @GET
    @Path("/categories")
    @Produces("application/json")
    Categories getCategories();


    @GET
    @Path("/adverts/{id}")
    @ClientHeaderParam(name = "Authorization", value = "{generateAuthHeader}")
    @Produces("application/json")
    Advert getAdvert(@PathParam("id") String id);


}
