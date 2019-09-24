package com.woworks.bot9;

import com.woworks.client9.model.Advert;
import com.woworks.client9.model.Category;
import com.woworks.client9.restclient.CategoryService;
import com.woworks.client9.scrape.ScrapperService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/hello")
public class HelloService {

    ScrapperService scrapperService;

    @Inject
    @RestClient
    CategoryService categoryService;

    @Inject
    public HelloService(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Category> categories() {
        return categoryService.getCategories().getCategories();
    }

    @GET
    @Path("/adverts-api/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Advert advertApi(@PathParam("id") String id) {
        return categoryService.getAdvert(id);
    }


    @GET
    @Path("/adverts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Advert advert(@PathParam("id") Long id) {
        return scrapperService.getAdvert(id);
    }

}
