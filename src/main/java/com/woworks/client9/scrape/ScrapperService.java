package com.woworks.client9.scrape;

import com.woworks.client9.model.Advert;
import com.woworks.client9.model.Price;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

@ApplicationScoped
public class ScrapperService {

    @ConfigProperty(name = "md.999.url")
    String url;

    @ConfigProperty(name = "md.999.lang")
    String lang;


    public Advert getAdvert(String id) {
        Document doc = null;
        try {
            doc = Jsoup.connect(advertUrl(id)).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getAdvertInfo(id, doc);
    }

    private String advertUrl(String id){
        return String.format("%s/%s/%s", url, lang, id);
    }


    private Advert getAdvertInfo(String id, Document doc) {
        String title = doc.select("meta[property=og:title]").attr("content");
        String description = doc.select("div[itemprop=description]").first().html();
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
