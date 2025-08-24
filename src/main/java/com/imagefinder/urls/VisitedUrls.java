package com.imagefinder.urls;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * VisitedUrls is implemented as a Singleton to ensure that all threads share the same
 * set of visited URLs during web crawling by provided a global access of the visited URLs
 * to avoid duplicate crawling. This results in efficient memory usage compared to an approach where
 * we maintain multiple instances of the visited URLs object and passing them as parameters
 */
public class VisitedUrls {
    private static VisitedUrls instance;
    private final Set<String> hashset;

    private VisitedUrls() {
        hashset = ConcurrentHashMap.newKeySet();
    }

    public static VisitedUrls getInstance() {
        if (instance == null) {
            instance = new VisitedUrls();
        }
        return instance;
    }

    public void addVisited(String url) {
        hashset.add(url);
    }

    public boolean isVisited(String url) {
        return hashset.contains(url);
    }

    public boolean isNoURLVisited() {
        return hashset.isEmpty();
    }

    public void clearVisitedUrls() {
        hashset.clear();
    }
}
