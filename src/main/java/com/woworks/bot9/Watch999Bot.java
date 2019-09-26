package com.woworks.bot9;

import com.woworks.common.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Watch999Bot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger("Watch999Bot");

    BotCommandProcessor botCommandProcessor;

    Watch999Bot(BotCommandProcessor botCommandProcessor) {
        this.botCommandProcessor = botCommandProcessor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            LOG.info("Text From Bot: {}", update.getMessage().getText());
            LOG.info("Text From Bot: UserId: {}", update.getMessage().getFrom());
            SendMessage sendMessage = botCommandProcessor.process(update);
            try {
                execute(sendMessage); // Call method to send the message
            } catch (TelegramApiException e) {
                LOG.error("Could not execute telegram message", e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        String botName = ApplicationProperties.INSTANCE.getValue("org.telegram.bot.name");
        LOG.info("Bot Name = {}", botName);
        return botName;
    }

    @Override
    public String getBotToken() {
        String botToken = ApplicationProperties.INSTANCE.getValue("org.telegram.bot.token");
        LOG.info("Bot token = {}", botToken);
        return botToken;
    }
}
