package com.eulerity.hackathon.imagefinder.htmlparsers;
import com.eulerity.hackathon.imagefinder.models.Category;
import com.eulerity.hackathon.imagefinder.models.Image;
import com.eulerity.hackathon.imagefinder.utils.URLUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser class to parse image data from the webpages
 */
public class ImageParser implements IHTMLParser<Image>{

    // regex to identify parameters such as /resize:fill:128*128, out of the image URL
    // this regex is used to identify and remove such patterns as the resize parameters leads to
    // duplicate images been displayed. Cleaning the iamge URL in such avoids duplicate images.
    private static final Pattern RESIZE_PATTERN = Pattern.compile("resize:fill:\\d+:\\d+/");
    // regex to identify logo images based on the url/alt text of the image containing keywords as mentioned below.
    private static final Pattern LOGO_PATTERN = Pattern.compile(
            "(?i).*?(logo|brand|icon|symbol|company|badge|emblem|mark|trademark).*?");
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageParser.class);

    /**
     * Method to clean the url by removing the resize:fill parameter within the url
     * @param url: url of the webpage
     * @return returns the cleaned URL of the image
     */
    private String cleanURL(String url) {
        return RESIZE_PATTERN.matcher(url).replaceAll("");
    }

    /**
     * Method to parse the document
     * @param document: document of the HTML page parsed by Jsoup
     * @param url: url of the webpage to parse
     * @return returns a collection of Image data extracted from the document object
     */
    @Override
    public Collection<Image> parseHTML(Document document, String url) {
        Set<Image> images = new HashSet<>();
        try{
            Elements imgElements = document.select("img[src]");
            for (Element img : imgElements) {
                String src = img.absUrl("src");
                src = cleanURL(src);
                if (!src.isEmpty() && URLUtils.isValidURL(src)) {
                    if (isLogoImage(src,img.attr("alt"))){
                        LOGGER.debug("Identified logo image from the URL: {} and alt text: {}",
                                        src, img.attr("alt"));
                        images.add(new Image(src, Category.LOGO));
                    }
                    else{
                        images.add(new Image(src, Category.GENERAL));
                    }
                }
            }
        }
        catch(Exception e){
            LOGGER.error("Error occurred while parsing images from the document of URL: {}",url,e);
        }
        return images;
    }


     /**
     * Checks if an image is likely a logo based on its URL or alt text.
     * @param imageUrl The URL of the image.
     * @param altText The alt text of the image (can be empty or null).
     * @return True if the image is likely a logo, false otherwise.
     */
    public static boolean isLogoImage(String imageUrl, String altText) {
        if (imageUrl == null) imageUrl = "";
        if (altText == null) altText = "";
        Matcher matcher1 = LOGO_PATTERN.matcher(imageUrl);
        Matcher matcher2 = LOGO_PATTERN.matcher(altText);
        boolean urlMatches = matcher1.find();
        boolean altMatches = matcher2.find();
        return urlMatches || altMatches;
    }

}
