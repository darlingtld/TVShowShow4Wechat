package lingda.tang.engine;

import lingda.tang.pojo.BTLink;
import lingda.tang.pojo.Show;
import lingda.tang.pojo.Source;
import lingda.tang.util.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darlingtld on 2015/1/21.
 */
@Component
public class BttiantangProvider extends SourceProvider {
    private static final Logger logger = LoggerFactory.getLogger(BttiantangProvider.class);
    private final String DOMAIN = "http://www.bttiantang.com";
    private final String SEARCH_URL = "s.php?q=%s&sitesearch=www.bttiantang.com&domains=bttiantang.com&hl=zh-CN&ie=UTF-8&oe=UTF-8";

    @Override
    public List<Show> searchShows(String keyword) {
        List<Show> showList = new ArrayList<Show>();
        try {
            Document doc = this.searchShowFromSourceProvider(String.format(DOMAIN + "/" + SEARCH_URL, URLEncoder.encode(keyword, "utf-8")), null);
            Elements elements = doc.getElementsByClass("item");
            for (Element element : elements) {
                try {
                    Element e = element.getElementsByTag("a").first();
                    String showLink = DOMAIN + e.attr("href");
                    String showName = e.text();
                    List<BTLink> btLinks = getBTLinks(showLink);
                    Show show = new Show(showName);
                    show.setBtLinkList(btLinks);
                    show.setSource(Source.BTTIANTANG);
                    showList.add(show);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return showList;
    }

    private List<BTLink> getBTLinks(String showLink) {
        List<BTLink> btLinks = new ArrayList<>();
        Document doc = Utils.connect(showLink, null);
        Elements elements = doc.getElementsByClass("tinfo");
        for (Element element : elements) {
            Element e = element.getElementsByTag("a").first();
            BTLink btLink = new BTLink();
            btLink.setDescription(e.getElementsByClass("torrent").text());
            btLink.setUrl(DOMAIN + e.attr("href"));
            btLinks.add(btLink);
        }
        return btLinks;
    }

}
