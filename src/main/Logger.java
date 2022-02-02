package main;

import java.util.Properties;

public class Logger {

	private static Properties props = new Properties(); // Object to store loaded properties
	
	/*
	 * Method to set loaded properties
	 * 
	 * @param props properties, that will be set.
	 * 
	 * */
	
	public static void setProperties(Properties props) {
		
		Logger.props = props;
		
	}
	
	/*
	 * Method to print out message into the console IF properties dictate so.
	 * 
	 * @param s message to be printed.
	 * 
	 * */
	
	public static void log(String s) {
		
		if(props.getProperty("crawler.log").toLowerCase().equals("true"))
		System.out.println(s);
		
	}
	
}
