package com.eulerity.hackathon.imagefinder.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utils class that performs operations on URLs
 */
public class URLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(URLUtils.class);

    /**
     * Method to check two URLs are from the same domain
     * @param url1: first url
     * @param url2: second url
     * @return return true/false based on the URL comparison.
     */
    public static boolean isSameDomain(String url1, String url2) {
        try {
            URI baseURI = new URI(url1);
            URI targetURI = new URI(url2);
            return baseURI.getHost().equalsIgnoreCase(targetURI.getHost());
        } catch (URISyntaxException e) {
            return false; // Invalid URL format
        }
        catch(NullPointerException e){
            return false;
        }
        catch(Exception e){
            LOGGER.error("Error while comparing the domains of the URLs: {} and {}", url1, url2,e);
            return false;
        }
    }

    /**
     * Method to check the validity of the URL
     * @param url: url to check
     * @return returns true/false based on the validity of the URL
     */
    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        } catch(Exception e){
            LOGGER.error("Exception while checking validity of the URL: ",url,e);
            return false;
        }
    }

}
