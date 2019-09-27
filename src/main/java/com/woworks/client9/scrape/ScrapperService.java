package com.woworks.client9.scrape;

import com.woworks.client9.model.Advert;
import com.woworks.client9.model.Price;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

@ApplicationScoped
public class ScrapperService {

    private static final Logger LOG = LoggerFactory.getLogger("ScrapperService");

    @ConfigProperty(name = "md.999.url")
    String url;

    @ConfigProperty(name = "md.999.lang")
    String lang;


    public Advert getAdvert(Long advertId) {
        Document doc = null;
        try {
            doc = Jsoup.connect(advertUrl(advertId)).get();
        } catch (HttpStatusException e) {
            LOG.error("There is no advert with this id = '{}'; error code = '{}'", advertId, e.getStatusCode());
            return null;
        } catch (IOException e) {
            LOG.error("Could not get the advert", e);
        }
        return getAdvertInfo(advertId, doc);
    }

    private String advertUrl(Long id){
        return String.format("%s/%s/%s", url, lang, id);
    }


    private Advert getAdvertInfo(Long advertId, Document doc) {
        String title = doc.select("meta[property=og:title]").attr("content");
        String description = null;
        Element descriptionElement = doc.select("div[itemprop=description]").first();
        if (descriptionElement != null) {
            description = descriptionElement.html();
        }
        String priceCurrency = doc.select("span[itemprop=currency]").attr("content");



        Advert advert = new Advert();
        advert.setId(advertId);
        advert.setTitle(title);
        advert.setBody(description);
        try {
            Double priceValue = Double.parseDouble(doc.select("span[itemprop=price]").first().html().replace(" ", ""));
            advert.setPrice(new Price(priceCurrency, priceValue));
        } catch (NumberFormatException | NullPointerException e) {
            LOG.warn("No price found");
            advert.setPrice(Price.noPrice());
        }
        return advert;
    }


}
