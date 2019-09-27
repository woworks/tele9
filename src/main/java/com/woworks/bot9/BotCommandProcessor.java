package com.woworks.bot9;

import com.woworks.client9.model.AdvertHistory;
import com.woworks.scheduling.AdvertWatcherException;
import com.woworks.scheduling.AdvertWatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class BotCommandProcessor implements CommandProcessor {

    private static final Logger LOG = LoggerFactory.getLogger("BotCommandProcessor");

    AdvertWatcherService advertWatcherService;

    @Inject
    public BotCommandProcessor(AdvertWatcherService advertWatcherService) {
        this.advertWatcherService = advertWatcherService;
    }

    @Override
    public void setBot(Watch999Bot watch999Bot) {
        this.advertWatcherService.setBot(watch999Bot);
    }

    @Override
    public SendMessage process(Update update) {
        String messageText = update.getMessage().getText();
        String[] messageArray = messageText.split(" ");
        String command = messageArray[0];

        if ((messageArray.length == 1 &&
                (!Commands.HISTORY.toString().equals(command) &&
                        (!Commands.STOP.toString().equals(command)) &&
                        (!Commands.HELP.toString().equals(command))
                )) ||
                (messageArray.length > 2)) {
            return getBadCommand(update.getMessage().getChatId(), messageText);
        }

        String parameter = messageArray.length == 1 ? "" : messageText.split(" ")[1];

        SendMessage replyMessage;
        switch (command) {
            case "/help":
                replyMessage = getHelpMessage(update.getMessage().getChatId());
                break;
            case "/watch":
                replyMessage = getWatchMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId(), parameter);
                break;
            case "/unwatch":
                replyMessage = getUnwatchMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId(), parameter);
                break;
            case "/prices":
                replyMessage = getPricesMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId());
                break;
            case "/stop":
                replyMessage = getStopMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId());
                break;
            default:
                replyMessage = getBadCommand(update.getMessage().getChatId(), command);
        }

        return replyMessage;
    }

    private SendMessage getPricesMessage(Long chatId, long userId) {
        List<AdvertHistory> watchHistoryList = advertWatcherService.getUserAdvertsHistory(userId);
        String watchHistoryListFormatted = watchHistoryList.isEmpty() ? "No prices yet" : getWatchHistoryListFormatted(watchHistoryList);
        return new SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(watchHistoryListFormatted);
    }

    private SendMessage getStopMessage(long chatId, long userId) {
        LOG.info("StopMessage:: chatId: '{}', userId: '{}'", chatId, userId);
        advertWatcherService.stopWatch(userId);

        String stopMessage = "You will be no more subscribed to any advert price change";
        return new SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(stopMessage);
    }

    SendMessage getWatchMessage(Long chatId, Integer userId, String parameter) {
        Long advertId;
        try {
            advertId = Long.parseLong(parameter);
            advertWatcherService.getAdvert(advertId);
        } catch (ExecutionException | NumberFormatException e) {
            return new SendMessage()
                    .setChatId(chatId)
                    .setParseMode(ParseMode.HTML)
                    .setText("There is no advert with this Advert Id: " + parameter);
        }

        List<AdvertHistory> watchHistoryList = advertWatcherService.watchAdvert(userId, advertId, chatId);
        String watchHistoryListFormatted = getWatchHistoryListFormatted(watchHistoryList);
        return new SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(watchHistoryListFormatted);
    }

    private SendMessage getUnwatchMessage(Long chatId, Integer userId, String parameter) {
        Long advertId;
        try {
            advertId = Long.parseLong(parameter);
            advertWatcherService.unwatchAdvert(userId, advertId);

        } catch (NumberFormatException | AdvertWatcherException e) {
            return new SendMessage()
                    .setChatId(chatId)
                    .setParseMode(ParseMode.HTML)
                    .setText("There is no advert with this Advert Id: " + parameter);
        }


        List<AdvertHistory> watchHistoryList = advertWatcherService.getUserAdvertsHistory(userId);
        String watchHistoryListFormatted = getWatchHistoryListFormatted(watchHistoryList);
        if (watchHistoryListFormatted.isEmpty()) {
            watchHistoryListFormatted = String.format("Removed '%s' from watch", parameter);
        }
        return new SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(watchHistoryListFormatted);
    }

    public static String getWatchHistoryListFormatted(List<AdvertHistory> watchHistoryList) {
        StringBuilder result = new StringBuilder();
        watchHistoryList.forEach(advertHistory -> {
            result.append(String.format("<a href=\"https://999.md/ru/%s\">%s</a> - %s \n",
                    advertHistory.getAdvert().getId(),
                    advertHistory.getAdvert().getId(),
                    advertHistory.getAdvert().getTitle()));

            result.append("<pre>");
            result.append("         Watch History List         \n");
            result.append("--------------+---------------------+\n");
            result.append("  Price       +        Date         |\n");
            result.append("--------------+---------------------+\n");
            advertHistory.getPriceHistory().forEach(priceChange ->
                    result.append(String.format(" %11s  | %18s |\n",
                    priceChange.getPrice().toPrint(),
                    priceChange.getDateTime().format(DateTimeFormatter.ofPattern("YYYY/MM/dd HH:mm:ss")))));
            result.append("--------------+---------------------+\n");
            result.append("</pre>");

        });

        return result.toString();
    }

    private static SendMessage getHelpMessage(Long chatId) {
        return new SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(getHelp());
    }

    private static SendMessage getBadCommand(Long chatId, String message) {
        return new SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(getBadCommand(message));
    }

    private static String getBadCommand(String message) {
        return String.format("Incorrect command: <i>'%s'</i>\n Please use the following commands: \n %s", message, getHelp());
    }

    static String getHelp() {
        StringBuilder reply = new StringBuilder();
        Arrays.asList(Commands.values()).forEach(
                com -> reply.append(Commands.getHelp(com.getCommand()) + "\n")
        );
        reply.append("/watch 61587874");
        return reply.toString();
    }
}
