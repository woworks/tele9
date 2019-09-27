# Tele9

Telegram Bot that watches advert price changes on 999.md advert board

## Tele9 Bot Commands

``` 
/help - command displays help
```
```
/watch [advert id] - watch price change for an advert id
```
```
/unwatch [advert id] - stop watching price change for an advert id
```
```
/prices - display adverts and their price history
```
```
/stop - stop watching all the adverts
```

### Prerequisites

1. Register at 999.md. Follow instructions on https://999.md/api page to get the authentication token to use 999.md API 
999.md api can be used to work only with own created adverts.
Specify this token in application.properties file - for **md.999.auth.token** propery
2. Follow https://core.telegram.org/bots tutorial to get telegram token for your bot. 
Obtained token specify in application.properties file - for **org.telegram.bot.token** propery

## Deployment

Package project to jar.
Run: java -jar tele9-1.0-SNAPSHOT-runner.jar

## Built With

* [Quarkus](https://quarkus.io/) - The Framework used (featues used: cdi, rest-client, resteasy, resteasy-jsonb, scheduler)
* [Guava](https://github.com/google/guava) - Guava: Google Core Libraries for Java (used: LoadingCache - a Cache built with an attached CacheLoader)
* [Jsoup](https://jsoup.org/) - Java HTML Parser (used to parse 999.md advert pages to get the price)
* [telegrambots](https://github.com/rubenlagus/TelegramBots) - library to create Telegram Bots in Java

