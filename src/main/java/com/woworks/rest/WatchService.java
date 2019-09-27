package com.woworks.rest;

import com.woworks.client9.model.AdvertHistory;
import com.woworks.scheduling.AdvertWatcherException;
import com.woworks.scheduling.AdvertWatcherService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

@Path("/watcher")
public class WatchService {

    AdvertWatcherService advertWatcherService;

    @Inject
    public WatchService(AdvertWatcherService advertWatcherService) {
        this.advertWatcherService = advertWatcherService;
    }

    @GET
    @Path("/users/{id}/unwatch/{advertId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void unwatch(@PathParam("id") Long userId, @PathParam("advertId") Long advertId) throws AdvertWatcherException {
        advertWatcherService.unwatchAdvert(userId, advertId);
    }

    @GET
    @Path("/users/{id}/watch/{advertId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AdvertHistory> watch(@PathParam("id") Long userId, @PathParam("advertId") Long advertId) {
        return advertWatcherService.watchAdvert(userId, advertId);
    }

    @GET
    @Path("/users/{id}/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Long> list(@PathParam("id") Long userId) {
        return advertWatcherService.getUserAdvertIds(userId);
    }

    @GET
    @Path("/users/{id}/history")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AdvertHistory> history(@PathParam("id") Long userId) {
        return advertWatcherService.getUserAdvertsHistory(userId);
    }

}
