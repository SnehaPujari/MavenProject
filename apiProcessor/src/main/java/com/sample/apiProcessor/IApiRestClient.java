package com.sample.apiProcessor;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sample.apiProcessor.model.ApiData;

/**
 * @author Sneha
 * This interface define methods to be implemented for communicating with REST API endpoints
 */
public interface IApiRestClient {	
	/**
	 * This method should get all the data from REST API endpoint
	 * @return List of ApiData objects
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	List<ApiData> getAll() throws JsonProcessingException, IOException;
	
    /**
     * This method should get data for the provided id
     * @param id - ApiData Id
     * @return ApiData object
     * @throws JsonProcessingException
     * @throws IOException
     */
    ApiData get(int id) throws JsonProcessingException, IOException;
    
    /**
     * This method updates the description
     * @param apiData - ApiData object with desired description to be updated
     * @return HTTP status code
     */
    int put(ApiData apiData);
}
