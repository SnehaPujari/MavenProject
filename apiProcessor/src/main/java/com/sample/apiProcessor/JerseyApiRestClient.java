package com.sample.apiProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.apiProcessor.model.ApiData;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

/**
 * @author Sneha
 * This class handles communication to the REST API endpoints by implementing IApiRestClient
 */
public class JerseyApiRestClient implements IApiRestClient {
	
	 private static final String REST_ENDPOINT = "/data/";
	 private static final String REST_GET_ALL_ENDPOINT = REST_ENDPOINT + "all";
	 
	 private final WebTarget webTarget;

	 /**
	 * Constructor initializes the webTarget to communicate with REST API endpoint
	 * @param host - Base address of the REST API endpoint
	 */
	public JerseyApiRestClient(String host) {	    	
        ClientConfig clientConfig = new ClientConfig()
            .property(ClientProperties.READ_TIMEOUT, 30000)
            .property(ClientProperties.CONNECT_TIMEOUT, 5000);

        webTarget = ClientBuilder
            .newClient(clientConfig)
            .target(host);
	}
	    
	@Override
	public List<ApiData> getAll() throws JsonProcessingException, IOException {
		Response response = webTarget
	            .path(REST_GET_ALL_ENDPOINT)
	            .request()
	            .get();
				            	
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		
		return mapper.reader().withRootName("apiData")
			      .forType(new TypeReference<List<ApiData>>() {})
			      .readValue(response.readEntity(String.class));	   
	}
	
	@Override
	public ApiData get(int id) throws JsonProcessingException, IOException {	
		Response response = webTarget
	            .path(REST_ENDPOINT + id)
	            .request()
	            .get();
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		
		return mapper.reader()
			      .forType(new TypeReference<ApiData>() {})
			      .readValue(response.readEntity(String.class));	   
	}
	
	@Override
	public int put(ApiData apiData) {
		if (apiData == null) {
            throw new RuntimeException("ApiData entity should not be null");
        }		
        return webTarget
            .path(REST_ENDPOINT + apiData.getId() + "/" + apiData.getDescription())
            .request()
            .put(Entity.json(apiData))
            .getStatus();
	}	
}
