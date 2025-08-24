package com.imagefinder.htmlparsers;

import com.imagefinder.urls.VisitedUrls;
import com.imagefinder.utils.URLUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to parse subpage data i.e hyperlinks within a webpage
 */
public class LinkParser implements IHTMLParser<String>{

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkParser.class);

    private final VisitedUrls visitedUrls;

    /**
     * visitedUrls object of the singleton class to check if the subpage is already crawled
     */
    public LinkParser() {
        this.visitedUrls = VisitedUrls.getInstance();
    }

    /**
     * Method to parseHTML and get the subpage URL within the document
     * @param document: document object extracted using Jsoup
     * @param url: url of the webpage we are parsing
     * @return returns a collection of the subpage URLs within the webpage
     */
    @Override
    public Collection<String> parseHTML(Document document, String url) {
        Set<String> subPages = new HashSet<>();
        try{
            Elements hyperlinks = document.select("a[href]");
            for (Element hyperlink : hyperlinks) {
                String subPageURL = hyperlink.absUrl("href");//
                if(!subPageURL.isEmpty() && !this.visitedUrls.isVisited(subPageURL)
                        && URLUtils.isSameDomain(subPageURL, url) && URLUtils.isValidURL(subPageURL)) {
                    subPages.add(subPageURL);
                }
            }
        }
        catch(Exception e){
            LOGGER.error("Error while parsing URL to extract hyperlinks from the URL: {}",url,e);
        }

        return subPages;
    }
}
