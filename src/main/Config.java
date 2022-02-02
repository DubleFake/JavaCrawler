package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Properties;

public class Config {

	/*
	 * This method loads a preset configuration into the passed object. Configuration file
	 * always has to be in the directory of the JAR executable and has to be named
	 * "crawler.config". In case if configuration file will be missing or corrupted
	 * it will be generated.
	 * 
	 * @param p object, where all loaded configuration parameters will be stored.
	 * 
	 * */
	
	public static void loadConfig(Properties p) {
		
		
		String fileName = "crawler.config";
		if(Files.exists(new File("crawler.config").toPath()) && !isCorrupted()) {
		try (FileInputStream fis = new FileInputStream(fileName)) {
		    p.load(fis);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		}else
			generateConfig(p);
		
	}
	
	/*
	 * This method generates a preset configuration file in case if it was not found.
	 * 
	 * @param p object, which will be forwarded to loadConfig function after a proper
	 * 			configuration file is created.
	 * 
	 * */
	
	public static void generateConfig(Properties p) {
		
		try (PrintWriter writer = new PrintWriter("crawler.config")) {
		      writer.write("crawler.depth=8\r\n"
		      		+ "crawler.maxVisited=10000\r\n"
		      		+ "crawler.root=https://en.wikipedia.org/wiki/Elon_Musk\r\n"
		      		+ "crawler.targets=Tesla,Musk,Gigafactory,Elon Musk\r\n"
		      		+ "crawler.log=true\r\n"
		      		+ "export.path=export.csv");
		      writer.close();
		      loadConfig(p);
		    } catch (FileNotFoundException ex) {
		     ex.printStackTrace();
		    }
		
	}
	
	/*
	 * This method checks if configuration file is corrupted or deformed.
	 * 
	 * @return if file is corrupted returned value will be TRUE, if the 
	 * 		   configuration file is intact and is usable - FALSE.
	 * 
	 * */
	
	private static boolean isCorrupted() {
		
		Properties prop = new Properties();
        InputStream input = null;
        boolean corrupted = false;

        try {
        	
            input = new FileInputStream("crawler.config");

            prop.load(input);

            corrupted = prop.getProperty("crawler.depth") == null
                    || prop.getProperty("crawler.maxVisited") == null
                    || prop.getProperty("crawler.root") == null
                    || prop.getProperty("crawler.targets") == null
                    || prop.getProperty("crawler.log") == null
                    || prop.getProperty("export.path") == null;

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return corrupted;

		
	}
	
}
