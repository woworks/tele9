package com.woworks.bot9;

import com.woworks.client9.model.AdvertHistory;
import com.woworks.scheduling.AdvertWatcherService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/watcher")
public class WatchService {

    AdvertWatcherService advertWatcherService;

    @Inject
    public WatchService(AdvertWatcherService advertWatcherService) {
        this.advertWatcherService = advertWatcherService;
    }


    @GET
    @Path("/user/{id}/unwatch/{advertId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void unwatch(@PathParam("id") Long userId, @PathParam("advertId") Long advertId) {
        advertWatcherService.unwatchAdvert(userId, advertId);
    }

    @GET
    @Path("/user/{id}/watch/{advertId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void watch(@PathParam("id") Long userId, @PathParam("advertId") Long advertId) {
        advertWatcherService.watchAdvert(userId, advertId);
    }

    @GET
    @Path("/user/{id}/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Long> list(@PathParam("id") Long userId) {
        return advertWatcherService.getUserAdvertIds(userId);
    }

    @GET
    @Path("/user/{id}/history")
    @Produces(MediaType.APPLICATION_JSON)
    public AdvertHistory history(@PathParam("id") Long userId) {
        return advertWatcherService.getUserAdvertsHistory(userId);
    }

}