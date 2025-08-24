package com.eulerity.hackathon.imagefinder.cache;

import com.eulerity.hackathon.imagefinder.crawleddata.CrawlDataStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton cache class to hold the data corresponding to a base URL, so that we do not need to crawl the
 * webpages we have already crawled.
 * We could have an expiration/TTL implementation for this cache, so that the service does not return
 * outdated data.
 */
public class LocalCache {
    private static LocalCache instance;
    private final Map<String, CrawlDataStorage> localCacheData;

    /**
     * private constructor to implement singleton behavior, as we only need a single object this class.
     */
    private LocalCache() {
        localCacheData = new HashMap<>();
    }

    /**
     * Method to return the single object of this class, by creating it if not already done
     * @return returns the single object of this class
     */
    public static LocalCache getInstance() {
        if (instance == null) {
            instance = new LocalCache();
        }
        return instance;
    }

    /**
     * Adding data to the cache.
     * @param url: url is the key which we have crawled
     * @param data: data is extracted by crawling the url
     */
    public void addData(String url, CrawlDataStorage data) {
        localCacheData.put(url, data);
    }

    /**
     * method to retrived extracted data from the cache
     * @param url: the webpage for which we want to retrieve data
     * @return returns the retrieved data which we stored within the cache
     */
    public CrawlDataStorage getData(String url) {
        return localCacheData.get(url);
    }

    public boolean isVisited(String url) {
        return localCacheData.containsKey(url);
    }
}
