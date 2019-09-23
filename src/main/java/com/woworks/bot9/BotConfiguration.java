package com.woworks.bot9;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;


@ApplicationScoped
public class BotConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger("BotConfiguration");

/*    void onStart(@Observes StartupEvent ev) {
        LOG.info("The application is starting...");
        ApiContextInitializer.init();
        LOG.info("ApiContextInitializer was initialized...");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new Watch999Bot());
            LOG.info("Watch999Bot was registered...");
        } catch (TelegramApiException e) {
            LOG.error("Could not register bot", e);
        }
    }*/

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("The application is stopping...");
    }
}
