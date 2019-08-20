package com.sample.apiProcessor;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.sample.apiProcessor.DataProcessorEngine;
import com.sample.apiProcessor.JerseyApiRestClient;
import com.sample.apiProcessor.model.ApiData;

/**
 * @author Sneha
 * This class is used to unit test JerseyApiRestClient and DataProcessorEngine class methods
 */
public class JersyAPIRestClientTest {
	private static final int WIREMOCK_PORT = 8080;
    
	private static final UrlPattern TEST_GETALL_URL = urlMatching(".*/data/all");
	private static final UrlPattern TEST_GET_URL = urlMatching(".*/data/[0-9]*");
	private static final UrlPattern TEST_PUT_URL = urlMatching(".*/data/[0-9]*/.*");
	
	private static final String DATA_LIST_STRING = "{ \"apiData\": [ { \"id\": \"123\", \"description\": \"CANCELED\" }, { \"id\": \"456\", \"description\": \"COMPLETE\" }, { \"id\": \"789\", \"description\": \"PENDING\" }, { \"id\": \"101\", \"description\": \"COMPLETE\" } ] }";
	private static final String DATA_STRING = "{ \"ApiData\" : { \"id\": \"456\", \"description\": \"PENDING\" }}";
	private static final String DATA_COMPLETED_STRING = "{ \"ApiData\" : { \"id\": \"456\", \"description\": \"COMPLETE\" }}";
	
    @ClassRule
    public static final WireMockClassRule WIREMOCK_RULE = new WireMockClassRule(WIREMOCK_PORT);

    private JerseyApiRestClient clientUnderTest;

    @Before
    public void setUp() throws Exception {
        clientUnderTest = new JerseyApiRestClient("http://localhost:" + WIREMOCK_PORT);
    }

    /**
     * Test JerseyApiRestClient::getAll() by mocking GET request to send valid JSON body
     */
    @Test
    public void testGetAll_WithBody() {
        stubFor(get(TEST_GETALL_URL).willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
            .withBody(DATA_LIST_STRING)));

        List<ApiData> apiData = null;
        
        try {
        	apiData = clientUnderTest.getAll();
        }
        catch(Exception ex) {
        	assertFalse("Unexcepted " +ex.getClass().toString() ,true);
        }
        
        assertNotNull(apiData);
        assertEquals(4, apiData.size());
        
        ArrayList<ApiData> expected = new ArrayList<ApiData>();
        ApiData t1 = new ApiData(); t1.setId(123); t1.setDescription("CANCELED");
        expected.add(t1);
        ApiData t2 = new ApiData(); t2.setId(456); t2.setDescription("COMPLETE");
        expected.add(t2);
        ApiData t3 = new ApiData(); t3.setId(789); t3.setDescription("PENDING");
        expected.add(t3);
        ApiData t4 = new ApiData(); t4.setId(101); t4.setDescription("COMPLETE");
        expected.add(t4);
        
        
        
        for(int i=0;i<apiData.size();i++) {
        	assertEquals(expected.get(i).getId(), apiData.get(i).getId());
        	assertEquals(expected.get(i).getDescription(), apiData.get(i).getDescription());
        }
    }
        
    /**
     * Test JerseyApiRestClient::get() by mocking GET request to send valid JSON body
     */
    @Test
    public void testGet_WithBody() {
        stubFor(get(TEST_GET_URL).willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
            .withBody(DATA_STRING)));

        ApiData apiData = null;
        
        try {
        	apiData = clientUnderTest.get(456);
        }
        catch(Exception ex) {
        	assertFalse("Unexcepted " +ex.getClass().toString() ,true);
        }
        
        assertNotNull(apiData);
        
        assertEquals(456, apiData.getId());
        assertEquals("PENDING", apiData.getDescription());
    }
    
    /**
     * Negative test for JerseyApiRestClient::get() and JerseyApiRestClient::getAll() by mocking GET request to send blank body
     */
    @Test(expected = JsonProcessingException.class)
    public void testGetMethods_WithoutBody() throws JsonProcessingException, IOException {
    	stubFor(get(TEST_GETALL_URL).willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .withBody("")));
        stubFor(get(TEST_GET_URL).willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
            .withBody("")));

        List<ApiData> apiData = null;        
        apiData = clientUnderTest.getAll();        
        assertNull(apiData);       
        
        ApiData data = null;
        data = clientUnderTest.get(456);        
        assertNull(data);       
    }
    
    /**
     * Test for JerseyApiRestClient::put() by mocking PUT request to return 200 status
     */
    @Test
    public void testPut_WithSuccess() {
        stubFor(put(TEST_PUT_URL).willReturn(status(200)));

        ApiData apiData = new ApiData();
        apiData.setId(456);
        apiData.setDescription("COMPLETE");
        
        assertEquals(200, clientUnderTest.put(apiData));
    }
    
    /**
     * Negative test for JerseyApiRestClient::put() by mocking PUT request to return 400 status
     */
    @Test
    public void testPut_WithFailure() {
        stubFor(put(TEST_PUT_URL).willReturn(status(400)));

        ApiData apiData = new ApiData();
        apiData.setId(456);
        apiData.setDescription("COMPLETE");
        
        assertEquals(400, clientUnderTest.put(apiData));
    }
    
    /**
     * Test for DataProcessorEngine::processRequests() by mocking GET and PUT request to send valid JSON body and return 200 status respectively     
     */
    @Test
    public void testDataProcessorEngine_WithSuccess() throws JsonProcessingException, IOException {
    	stubFor(get(TEST_GETALL_URL).willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .withBody(DATA_LIST_STRING)));
    	
    	stubFor(get(TEST_GET_URL).willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .withBody(DATA_COMPLETED_STRING)));

    	stubFor(put(TEST_PUT_URL).willReturn(status(200)));
    	
    	DataProcessorEngine dataProcessorEngine = new DataProcessorEngine();
    	dataProcessorEngine.processRequests();
    }
}
