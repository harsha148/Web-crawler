package com.imagefinder.htmlparsers;

import org.jsoup.nodes.Document;

import java.util.Collection;
/**
 * IHTMLParser defines a strategy interface for parsing HTML documents.
 * It follows the Strategy Pattern, allowing different parsing behaviors to be implemented
 * and swapped dynamically without modifying the core crawling logic.
 **/
public interface IHTMLParser<T> {

    public Collection<T> parseHTML(Document document, String url);
}
