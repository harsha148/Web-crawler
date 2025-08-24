package com.eulerity.hackathon.imagefinder.crawler;

import com.eulerity.hackathon.imagefinder.htmlparsers.IHTMLParser;
import com.eulerity.hackathon.imagefinder.htmlparsers.LinkParser;
import com.eulerity.hackathon.imagefinder.cache.LocalCache;
import com.eulerity.hackathon.imagefinder.crawleddata.CrawlDataStorage;
import com.eulerity.hackathon.imagefinder.urls.VisitedUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

/**
 * class to spawn the thread that starts crawling webpages starting from the base URL (provided within the request)
 * @param <T>: type of data we are trying to extract from the webpage.
 */
public class CrawlSpawner<T> {

    // object to store the data extracted from the webpages
    private final CrawlDataStorage<T> crawlDataStorage;
    private final String baseUrl;
    private final IHTMLParser<T> dataParser;
    static final ExecutorService executorService = Executors.newFixedThreadPool(1000); // Multi-threading
    private static final LocalCache localCache = LocalCache.getInstance();
    private static final VisitedUrls visitedUrls = VisitedUrls.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlSpawner.class);

    /**
     * constructor to create an object of the class CrawlSpawner
     * @param baseUrl: base url provided within the request to extract data from
     * @param crawlDataStorage: object to store the extracted data
     * @param dataParser: parser object to parse data from a webpage. parser can be passed to parse the appropriate
     *                  data from the webpage.
     */
    public CrawlSpawner(String baseUrl, CrawlDataStorage<T> crawlDataStorage, IHTMLParser<T> dataParser) {
        this.baseUrl = baseUrl;
        this.crawlDataStorage = crawlDataStorage;
        this.dataParser = dataParser;
    }

    /**
     * Method to start crawling data from the base webpage and then parse data from the subpages
     * @return object containing the collection of data crawled from the webpage and its subpages.
     */
    public CrawlDataStorage<T> startCrawl() {
        // checking time taken to complete the crawling request
        long startTime = System.currentTimeMillis();
        LOGGER.info("Spawning web crawl from the base URL: {}", baseUrl);
        // clearing the collection of data from the visitedUrls objects, so that new crawl from a different webpage
        // does not get effected by the existing set of visited URLS.
        visitedUrls.clearVisitedUrls();
        // checking if our local cache contains data corresponding to the baseURL we are attempting to crawl
        if (localCache.isVisited(baseUrl)) {
            LOGGER.info("Local cache already contains data corresponding to the this base url: {}", baseUrl);
            return localCache.getData(baseUrl);
        }
        else{
            // creating a thread to start crawling from the base URL
            Future<?> baseFuture = executorService.submit(new CrawlTask<>(this.baseUrl, 0, this.dataParser,
                    new LinkParser(), this.crawlDataStorage));
            try {
                // waiting for the thread to complete crawling
                baseFuture.get();
            }catch (ExecutionException | InterruptedException e) {
                LOGGER.error("Crawl failed at base URL with exception: ", e);
            }
        }
        LOGGER.info("Completed crawling the base URL ({}) and its subpages", this.baseUrl);
        LOGGER.info("Collected a total of {} data points  for this base url", this.crawlDataStorage.size());
        localCache.addData(this.baseUrl, this.crawlDataStorage);
        long endTime = System.currentTimeMillis();
        LOGGER.info("Total time taken for crawling the base URL : {} is: {} ms.", this.baseUrl,endTime - startTime);
        return this.crawlDataStorage;
    }

}
