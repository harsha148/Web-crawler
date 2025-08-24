package com.imagefinder.crawleddata;

import java.util.Collection;

/**
 * Interface for storing all kinds a data that can be extracted from a webpage
 * @param <T>: type of data we are attempting to extract
 */
public interface CrawlDataStorage<T> {

    /**
     * Method to store the data that we have currently extracted along with the data we have already stored/extracted
     * @param data: data to store
     */
    void storeData(T data);

    /**
     * Method to store a collection of data extraced
     * @param data: collection of data to store
     */
    void storeMultipleData(Collection<T> data);

    /**
     * Method to return the data stored as a collection object
     * @return: collection object
     */
    Collection<T> retrieveData();

    /**
     * Method to get the size of data extracted
     * @return: size of the data
     */
    int size();

}
