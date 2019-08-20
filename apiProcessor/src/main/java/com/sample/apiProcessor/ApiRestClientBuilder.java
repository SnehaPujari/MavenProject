package com.sample.apiProcessor;

/**
 * @author Sneha
 * This method returns implementation of the IApiRestClient
 */
public class ApiRestClientBuilder {
	
	private String host;

    public ApiRestClientBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public IApiRestClient build() {
        return new JerseyApiRestClient(this.host);
    }   
}
