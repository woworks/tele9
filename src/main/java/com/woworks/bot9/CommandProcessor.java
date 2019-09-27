package com.woworks.bot9;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Artiom Slastin
 */
public interface CommandProcessor {
    void setBot(Watch999Bot watch999Bot);

    public enum Commands {
        HELP("/help"),
        WATCH("/watch"),
        UNWATCH("/unwatch"),
        HISTORY("/prices"),
        STOP("/stop");

        private String command;

        Commands(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
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

    SendMessage process(Update update);
}
