package com.sample.apiProcessor;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Sneha
 * 
 */
public class Main 
{	
    /**
     * Calls DataProcessorEngine::processRequests() to simulate the update process
     * @param args
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static void main( String[] args ) throws JsonProcessingException, IOException
    {    
    	DataProcessorEngine dataProcessorEngine = new DataProcessorEngine();    	
    	dataProcessorEngine.processRequests();    	
    }
}
