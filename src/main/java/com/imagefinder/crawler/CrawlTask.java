package com.imagefinder.crawler;
import com.imagefinder.htmlparsers.IHTMLParser;
import com.imagefinder.crawleddata.CrawlDataStorage;
import com.imagefinder.urls.VisitedUrls;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Class implementing Runnable interface that can be used to create threads for crawling a webpage based on the URL.
 * @param <T> Type of data we are attempting to extract from the webpage
 */
public class CrawlTask<T> implements Runnable {

    // Depth limit for the subpage tree traversal
    private static final int DEPTH_LIMIT = 2;
    // Politeness delay in milliseconds before hitting the webpage again to prevent overwhelming the webpage
    private static final int RECRAWL_POLITENESS_DELAY = 100;
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlTask.class);
    private CrawlDataStorage<T> crawlDataStorage;
    private final String url;
    private final int currentDepth;
    private final IHTMLParser<T> dataParser;
    private final IHTMLParser<String> linkParser;
    // Singleton object of the VisitedUrls to maintain the set of visited URLs in the current crawl and prevent
    // repetition
    private static final VisitedUrls visitedUrls = VisitedUrls.getInstance();


    /**
     * Constructor for the CrawlTask class
     * @param url: url that the current object has to crawl
     * @param depthLevel: the current depth level in the subpage tree traversal
     * @param dataParser: dataParser object to extract data from the HTML document
     * @param linkParser: linkParser object to extract hyperlinks from the HTML document
     * @param crawlDataStorage: object to store the data extracted from the HTML document
     */
    public CrawlTask(String url, int depthLevel, IHTMLParser<T> dataParser, IHTMLParser<String> linkParser,
                     CrawlDataStorage<T> crawlDataStorage) {
        this.url = url;
        this.currentDepth = depthLevel;
        this.dataParser = dataParser;
        this.linkParser = linkParser;
        this.crawlDataStorage = crawlDataStorage;
    }

    /**
     * Method to scrape the webpage @ url using Jsoup
     * @return the document object extracted using Jsoup
     */
    public Document extractDocumentByUrl() {
        Document urlDocument = null;
        try{
            // Using Jsoup to scrape the webpage
            urlDocument = Jsoup.connect(this.url).get();
        } catch(Exception e){
            LOGGER.error("Error occured while scraping the url:{}", this.url,e);
        }
        return urlDocument;
    }

    /**
     * Method to use the dataparser object and extract data from the document object
     * @param document: document object extracted from the url using Jsoup
     */
    public void extractData(Document document) {
        Collection<T> subPageData = this.dataParser.parseHTML(document,this.url);
        crawlDataStorage.storeMultipleData(subPageData);
    }

    /**
     * Method to extract urls of the subpages within the current webpage
     * @param document: document object extracted from the webpage @ url using Jsoup
     * @return a collection of urls that are subpages/hyperlinks of the current webpage
     */
    public Collection<String> extractSubPages(Document document) {
        return this.linkParser.parseHTML(document,this.url);
    }

    /**
     * Method to crawl the current webpage @ this.url and then crawl the subpages.
     */
    @Override
    public void run() {
        // checking if the url has been crawled already
        if (visitedUrls.isVisited(this.url) || this.currentDepth>=DEPTH_LIMIT){
            return;
        }
        LOGGER.debug("Running crawl for the page: {}", this.url);
        visitedUrls.addVisited(this.url);
        // extracting data using Jsoup
        Document urlDocument = extractDocumentByUrl();
        if (urlDocument == null) {
            return;
        }
        extractData(urlDocument);
        // checking if the depth of the tree traversal is less than 2: limiting the subpages tree traversal to a
        // depth of DEPTH_LIMIT
        if (this.currentDepth<DEPTH_LIMIT) {
            crawlSubPages(urlDocument);
        }
    }

    /**
     * Method to extract URLs of the subpages within the current document object
     * @param urlDocument: object of Document extracted from the URL using Jsoup
     */
    private void crawlSubPages(Document urlDocument) {
        Collection<String> subPages = extractSubPages(urlDocument);
        // Creating a list of futures, which will be used to wait for the spawned thread to complete
        List<Future> subPageFutures = new ArrayList<>();
        // Creating threads for crawling subpages of the current thread
        for (String subPage : subPages) {
            if (!visitedUrls.isVisited(subPage)){
                subPageFutures.add(CrawlSpawner.executorService.submit(new CrawlTask(subPage,
                        this.currentDepth + 1,this.dataParser,this.linkParser,this.crawlDataStorage)));
                try {
                    Thread.sleep(RECRAWL_POLITENESS_DELAY);
                } catch (InterruptedException e) {
                    LOGGER.error("Error occurred while waiting for thread to sleep for politeness delay", e);
                }
            }
        }
        // TODO: Add JUnits
        // TODO: Add Comments to code extensively
        // TODO: Add writeup at the end of the index.html (What you have implemented, design thought process and further work
        // waiting for the subthreads to complete before closing the current thread
        for (int i=0; i<subPageFutures.size(); i++) {
            try {
                subPageFutures.get(i).get();
            } catch (ExecutionException | InterruptedException e) {
                LOGGER.error("Error occured while waiting for thread " + Thread.currentThread().getId(), e);
            }
        }
    }

}
