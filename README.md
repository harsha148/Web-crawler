# Image Web Crawler

### Functionality
- Built a web crawler that can find all images on the web page(s) that it crawls.
- Crawling sub-pages (identified by extracting the <a/> elements within the document) to find more images.
- Implemented multi-threading to crawl multiple pages simultaneously.
- Implemented checks to make sure that only pages belonging the given url are crawled.
- Implemented functionality to keep track of visited URLs so that the same URL is not crawled again.
- Implemented a local cache that keeps data extracted from a webpage, so that the same data can be returned in case the application receives a request with the same url without actually crawling the webpage again.
- Implemented keyword based approach to identify potential logos and differentiating them from generic images based on the image url and metadata.
- Implemented a delay between crawls to the same domain pages to avoid overwhelming the webpage.
- Improved the UI to display the different categories of images separately.
- Implemented logging to record information/errors from the application to a log file.

### Homepage
![Homepage](https://github.com/harsha148/Web-crawler/blob/main/images/homepage.png)

### Extracted Images
![Crawled Images](https://github.com/harsha148/Web-crawler/blob/main/images/Images.png)


### Design principles and implementation choices
- Extensibility to new image categories: The code is designed to be easily extended for detecting new categories of images beyond just general images and logos.The Image class associates images with a Category enum, making it straightforward to introduce new categories without modifying existing logic significantly. The only piece of additional code needed to add more categorization is the logic to classify images to the new categories (use this logic to identify the images belonging to the category and set the category field of Image object accordingly and additionally add a type of return data that implements the interface CrawlDataStorage ). There would be no changes required within the frontend as well. This ensures minimal code changes when adapting the system to new requirements.
- Use of Strategy Pattern for HTML Parsing: The IHTMLParser interface is designed following the Strategy Pattern, allowing the parsing logic to be decoupled from the core crawling functionality. Instead of tightly coupling the crawler with a specific parsing mechanism, the implementation allows for flexible content extraction by defining different parsing strategies. If the need arises to extend/modify the code to extract new types of content, it can be done simply by creating a new class that implements the IHTMLInterface and injecting an object of this class to the CrawlTask. This makes the code very extensible and maintainable. 
- Singleton Pattern for VisitedUrls and LocalCache: The VisitedUrls and LocalCache classes are implemented as Singletons to ensure that all crawling threads share a single, consistent record of visited URLs and cached data. VisitedUrls class uses concurrent data structure to ensure that the access to this data is synchronized and prevent race conditions which could lead to redundant web page crawling.

### Future Improvments
- Adding Junits to make easily test the functionality of the code.
- Add algorithms to use trained models and categorize images with higher precision and more finer categories.
- Adaptive crawling to dynamically introduce delay between crawls based on the responsiveness of a webpage.
- Additional functionality to extract only the images that are relevant to user's query/request


## Running the Project
Here we will detail how to setup and run this project so you may get started, as well as the requirements needed to do so.

### Requirements
Before beginning, make sure you have the following installed and ready to use
- Maven 3.5 or higher
- Java 8
  - Exact version, **NOT** Java 9+ - the build will fail with a newer version of Java

### Setup
To start, open a terminal window and navigate to wherever you unzipped to the root directory `imagefinder`. To build the project, run the command:

>`mvn package`

If all goes well you should see some lines that end with "BUILD SUCCESS". When you build your project, maven should build it in the `target` directory. To clear this, you may run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

You should see a line at the bottom that says "Started Jetty Server". Now, if you enter `localhost:8080` into your browser, you should see the `index.html` welcome page! If all has gone well to this point, you're ready to begin!

