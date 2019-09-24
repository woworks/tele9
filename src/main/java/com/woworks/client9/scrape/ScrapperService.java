package com.woworks.client9.scrape;

import com.woworks.client9.model.Advert;
import com.woworks.client9.model.Price;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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


    public Advert getAdvert(Long id) {
        Document doc = null;
        try {
            doc = Jsoup.connect(advertUrl(id)).get();
        } catch (IOException e) {
            LOG.error("Could not get the advert", e);
        }
        return getAdvertInfo(id, doc);
    }

    private String advertUrl(Long id){
        return String.format("%s/%s/%s", url, lang, id);
    }


    private Advert getAdvertInfo(Long id, Document doc) {
        String title = doc.select("meta[property=og:title]").attr("content");
        String description = null;
        Element descriptionElement = doc.select("div[itemprop=description]").first();
        if (descriptionElement != null) {
            description = descriptionElement.html();
        }
        String priceCurrency = doc.select("span[itemprop=currency]").attr("content");
        Double priceValue = Double.parseDouble(doc.select("span[itemprop=price]").first().html().replace(" ", ""));
        Advert advert = new Advert();
        advert.setId(id);
        advert.setTitle(title);
        advert.setBody(description);
        advert.setPrice(new Price(priceCurrency, priceValue));
        return advert;
    }


}
