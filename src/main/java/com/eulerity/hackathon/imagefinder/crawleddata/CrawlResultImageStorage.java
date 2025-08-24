package com.eulerity.hackathon.imagefinder.crawleddata;

import com.eulerity.hackathon.imagefinder.models.Image;
import java.util.Collection;

/**
 * class to store the data of Image type as a Collection of the data
 * This class will be used to store data when we parse Image data from webpages
 */
public class CrawlResultImageStorage implements CrawlDataStorage<Image> {

    // Collection of image data to store the extracted data
    private Collection<Image> data;

    /**
     * constructor to intialize the storage object
     * @param data: base collection of data
     */
    public CrawlResultImageStorage(Collection<Image> data) {
        this.data = data;
    }

    /**
     * Method to store data i.e Image data extracted from the webpage.
     * @param imgData: data to store
     */
    @Override
    public void storeData(Image imgData) {
        this.data.add(imgData);
    }

    /**
     * Method to store collection of data i.e Image data extracted from the webpage.
     * @param imgDataCollection: collection of data to store
     */
    @Override
    public void storeMultipleData(Collection<Image> imgDataCollection) {
        this.data.addAll(imgDataCollection);
    }

    /**
     * Method to retrieve collection of image data extracted so far.
     * @return a collection of image data
     */
    @Override
    public Collection<Image> retrieveData() {
        return this.data;
    }

    /**
     * Method to return the amount of image data we have extracted.
     * @return: size of the data extracted.
     */
    @Override
    public int size() {
        return this.data.size();
    }


}
