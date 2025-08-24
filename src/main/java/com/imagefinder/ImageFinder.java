package com.imagefinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imagefinder.crawleddata.CrawlDataStorage;
import com.imagefinder.crawleddata.CrawlResultImageStorage;
import com.imagefinder.crawler.CrawlSpawner;
import com.imagefinder.htmlparsers.ImageParser;
import com.imagefinder.models.Image;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
    name = "ImageFinder",
    urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet{
	private static final long serialVersionUID = 1L;
	protected static final Gson GSON = new GsonBuilder().create();
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageFinder.class);


	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Received request for crawling website with request: {} ",req.toString());
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		String path = req.getServletPath();
		String url = req.getParameter("url");
		if (url == null || url.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL parameter is missing.");
			return;
		}
		try {
			CrawlSpawner<Image> crawler = new CrawlSpawner<>(url,
															new CrawlResultImageStorage(new HashSet<>()),
															new ImageParser());
			CrawlDataStorage<Image> crawledData = crawler.startCrawl();
			JSONArray jsonArray = new JSONArray(crawledData.retrieveData());
			PrintWriter out = resp.getWriter();
			LOGGER.info("Completed processing the request and returning the response as: {}",jsonArray.toString());
			out.print(jsonArray);
			out.flush();
		} catch (Exception e) {
			LOGGER.error("Internal Server Error! Error Code 500: Exception occured while processing the request: " +
							"{} with exception:",req.toString(),e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request.");
		}
	}
}
