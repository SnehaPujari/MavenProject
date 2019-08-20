package com.sample.apiProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sample.apiProcessor.model.ApiData;

/**
 * @author Sneha
 * This method calls the REST API endpoint to implement the business logic
 */
public class DataProcessorEngine {
	private static final String PENDING = "PENDING";
	private static final String COMPLETE = "COMPLETE";
	private static Logger logger = LoggerFactory.getLogger(DataProcessorEngine.class);
	
	private IApiRestClient client;
	
	/**
	 * Reads the REST API endpoint host name and port number from config file
	 * @return REST API endpoint base URL
	 */
	private String getHostFromConfig() {
		Properties prop = new Properties();
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("apiConfig.properties");			
		    prop.load(is);
		}
		catch(Exception ex) {}
		return String.format("http://%1$s:%2$s",prop.getProperty("API.hostname"), prop.getProperty("API.portnumber") );		
	}
	
	/**
	 * initializes the client which will be used to communicate with REST API endpoint
	 */
	public DataProcessorEngine() {
		this.client = new ApiRestClientBuilder()
	         .setHost(this.getHostFromConfig())
	         .build();
	}
	
	/**
	 * Checks if description is valid to be marked as complete
	 * @param apiData - ApiData object
	 * @return True or False
	 */
	private Boolean isEligibleDescription(ApiData apiData) {
		return apiData.getDescription().equalsIgnoreCase(PENDING);
	}
	
	
	/**
	 * Gets API data and iterates through each data elements
	 * If any description is eligible to be marked as complete, it updates the description
	 * Checks if description is updated correctly, and then logs it to file
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public void processRequests() throws JsonProcessingException, IOException {
		List<ApiData> apiData = this.client.getAll();
		
		for(ApiData data : apiData) {
			if(isEligibleDescription(data)) {
				
				//Mark description as complete
				data.setDescription(COMPLETE);
				int status = this.client.put(data);
				
				//Check if put was successful (200 - Status OK)
		    	if(status == 200) {         
		    		
		    		//Get data and check if description got marked as complete
		    		ApiData updatedData = this.client.get(data.getId());		    		
		            if(updatedData.getDescription().equalsIgnoreCase(COMPLETE)) {	
		            	// Log the data
		            	 logger.info(String.format("Data id %1$s and Description is %2$s"
		            			 , updatedData.getId()
		            			 , updatedData.getDescription()));
		            }
		    	}
		    }
		}
	}
}
