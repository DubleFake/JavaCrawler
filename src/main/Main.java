package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
	
	private static String[] toHit; // Array of words/phrases to 'hit' on the page.
	private static List<String> results = new ArrayList<>(); // List of visited pages and 'hit' results prepared to be exported.
	private static Properties prop = new Properties(); // Properties, which will be loaded from the configuration file.
	
	/*
	 * Main method, which starts the program and executes core instructions of the program.
	 * 
	 * @param args additional arguments passed from the command line upon JAR file execution.
	 * 
	 * */
	
	public static void main(String args[]) {
	        
		Config.loadConfig(prop);
		Logger.setProperties(prop);
		Logger.log("Loaded configuration successfuly.");
		loadTargets();
		Logger.log("Loaded targets successfuly.");
		Logger.log("Starting to crawl...");
		crawl(0, Integer.parseInt(prop.getProperty("crawler.depth")), prop.getProperty("crawler.root"), new ArrayList<String>(), Integer.parseInt(prop.getProperty("crawler.maxVisited")));
		exportToCsv();
		Logger.log("Finished crawling, exiting.");
		
	    }
	
	/*
	 * This method parses through the links derived from the main seed in accordance with
	 * maximum depth and maximum visited links. 
	 * 
	 * @param depth 	 variable, that keeps track of current depth.
	 * @param MAX_DEPTH  variable, that dictates maximum depth.
	 * @param url 		 URL of a web site, which will be currently crawled through.
	 * @param visited 	 list of visited web sites
	 * @param maxVisited variable, that dictates the maximum amount of web sites, that
	 * 					 will be visited.
	 * 
	 * */
	
	private static void crawl (int depth,int MAX_DEPTH, String url, ArrayList<String> visited, int maxVisited) {
	    if(depth < MAX_DEPTH && visited.size() < maxVisited) {
	        Document doc = request(url, visited);
	        if (doc!= null) {
	        	String hits = findMatches(doc);
	        	results.add("\"" + url + "\"" + "," + depth + "," + hits);
	        	depth++;
	            for (Element link : doc.select("a[href]")) {
	                 String nextLink = link.absUrl("href");
	                 if(visited.contains(nextLink) == false && !nextLink.contains("#")) {
	                     crawl(depth, MAX_DEPTH, nextLink, visited, maxVisited);
	                 }
	            }
	        }
	    }
	}
	
	
	/*
	 * This method checks if a proper connection was established and if so, loads web site and adds it to the visited list.
	 * 
	 * @param url 	  URL of a web site, which will be currently connected to.
	 * @param visited list of already visited web sites.
	 * 
	 * @return 		  HTML document of a web site.
	 * 
	 * */
	
	private static Document request(String url, ArrayList<String> visited) {
	    try {
	        Connection con = Jsoup.connect(url);
	        Document doc = con.get();
	        if(con.response().statusCode() == 200) {
	            visited.add(url);
	            return doc;
	        }
	        return null;
	    } catch (IOException ex) {
	        return null;
	    }
	}
	
	/*
	 * This method looks for mentions or 'hits' of predefined strings in a HTML document of a web site.
	 * 
	 * @param  HTML document of a web site.
	 * 
	 * @return String of integers, which correspond to the matches found on the web site
	 * */
	
	private static String findMatches(Document doc) {
		
		String result = "";
		int total = 0;
		
		
		for(String s : toHit) {
			
			Matcher m = Pattern.compile("\\b" + s +"\\b").matcher(doc.body().text());

			int matches = 0;
			while(m.find()) {
			    matches++;
			    total++;
			}
			
			result = result + matches + ",";
		}
		
		result = result + total;
		
		return result;
		
	}
	
	/*
	 * This method exports obtained data to a .CSV file
	 * 
	 * */
	
	private static void exportToCsv() {
		
		Logger.log("Attempting to export results");
		
		 try (FileWriter fw = new FileWriter("export.csv")) { 
		      fw.append("URL,Depth,");
		      
		      for(String s : toHit) {
		    	  
		    	  fw.append(s + ",");
		    	  
		      }
		      fw.append("Total");
		      fw.append("\n");

		      for(String s : results) {
		    	  
		    	  fw.append(s + "\n");
		    	  
		      }

		      fw.close();
		      Logger.log("Export successful.");
		      
		    } catch (IOException ex) {
		     ex.printStackTrace();
		    }
		
	}
	
	/*
	 * This method loads string to find or 'hit' on web sites.
	 * 
	 * */
	
	private static void loadTargets() {
		
		toHit = prop.getProperty("crawler.targets").split(",");
		
	}
		
	}

