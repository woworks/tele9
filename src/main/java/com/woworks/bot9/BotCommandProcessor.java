package com.woworks.bot9;

import com.woworks.scheduling.AdvertWatcherService;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;

@ApplicationScoped
public class BotCommandProcessor {

    AdvertWatcherService advertWatcherService;

    @Inject
    public BotCommandProcessor(AdvertWatcherService advertWatcherService) {
        this.advertWatcherService = advertWatcherService;
    }

    private enum Commands {
        HELP("/help"),
        WATCH("/watch"),
        UNWATCH("/unwatch"),
        HISTORY("/prices"),
        STOP("/stop");

        private String command;

        Commands(String command) {
            this.command = command;
        }

        public static String getHelp(String command) {
            String helpMessage;
            switch (command) {
                case "/help":
                    helpMessage = "/help - command displays help";
                    break;
                case "/watch":
                    helpMessage = "/watch [advert id] - watch price change for an advert id";
                    break;
                case "/unwatch":
                    helpMessage = "/unwatch [advert id] - stop watching price change for an advert id";
                    break;
                case "/prices":
                    helpMessage = "/prices - display adverts and their price history";
                    break;
                case "/stop":
                    helpMessage = "/stop - stop watching all the adverts";
                    break;
                default:
                    helpMessage = "bad command";
            }
            return helpMessage;
        }

        @Override
        public String toString() {
            return command;
        }
    }

    SendMessage process(Update update) {
        String messageText = update.getMessage().getText();
        String[] messageArray = messageText.split(" ");
        String command = messageArray[0];
        if ((messageArray.length == 1 &&
                (!Commands.HISTORY.toString().equals(command) || (!Commands.STOP.toString().equals(command)))) ||
                (messageArray.length > 2)) {
            return getHelpMessage(update.getMessage().getChatId());
        }

        String parameter = messageText.split(" ")[1];


        String replyMessage = "";
        switch (command) {
            case "/help":
                replyMessage = getHelp();
                break;
            case "/watch":
                replyMessage = getWatch(update.getMessage().getFrom().getId(), parameter);
                break;
            case "/unwatch":
                replyMessage = getHelp();
                break;
            case "/prices":
                replyMessage = getHelp();
                break;
            case "/stop":
                replyMessage = getHelp();
                break;
            default:
                replyMessage = getHelp();
        }

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setParseMode(ParseMode.HTML)
                .setText(replyMessage);
    }

    String getWatch(Integer userId, String parameter) {
        Long advertId = Long.parseLong(parameter);
        advertWatcherService.watchAdvert(new Long(userId), advertId);
        return " get watch command";
    }

    String getUnwatch(Long userId, String parameter) {
        Long advertId = Long.parseLong(parameter);
        advertWatcherService.unwatchAdvert(userId, advertId);
        return " get watch command";
    }

    static SendMessage getHelpMessage(Long chatId) {
        return new SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(getHelp());
    }

    static String getHelp() {
        StringBuilder reply = new StringBuilder();
        Arrays.asList(Commands.values()).forEach(com -> {
                    reply.append(Commands.getHelp(com.command) + "\n");
                }
        );
        return reply.toString();
    }
}
