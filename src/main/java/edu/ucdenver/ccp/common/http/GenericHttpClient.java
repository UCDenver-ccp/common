package edu.ucdenver.ccp.common.http;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

/**
 * This is a generic implementation of HTTP requests using Apache HttpClient 4.0.
 * Derive a class from this one that has members, getters and setters for each parameter
 * you want to pass to your service. Implement makeParamString() (see OBAService.java
 * for an example) to compile the parameters. Call getResponse() to get the returned string.
 * @author roederc
 *
 */
public abstract class GenericHttpClient {

	int returnStatus;
	String protocol; 				
	String host; 			
	String path;  	
	boolean isGetRequest; // true for Get, false for POST
	
	private static Logger logger = Logger.getLogger(GenericHttpClient.class);
	HttpClient client=null;
	
	private static final int NUM_TRIES=3;
	private static final int HTTP_SUCCESS = org.apache.http.HttpStatus.SC_OK;
	private static final int HTTP_FAILURE = org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
	
	public GenericHttpClient(String protocol, String host, String path){
		this.protocol = protocol;
		this.host = host;
		this.path = path;
		
		
	}
	
	abstract protected String makeParamString() throws URISyntaxException;
	
	/**
	 * Hits the RESTful service and returns an InputStream to access it.
	 * The status code is available from the getReturnStatus() function;
	 * A negative value means an exception was thrown and the call never completed.
	 * Be sure and shutdown the connection in a finally block after using this.
	 * 
	 * TODO the retry code has not been tested against a locally controllable server,
	 * so not all scenarios may play out perfectly.
	 * 
	 * @return InputStream of page content.
	 */
	public InputStream getPostResponse()  throws IOException, URISyntaxException {
		client = new DefaultHttpClient();
		org.apache.http.conn.ClientConnectionManager connMgr = client.getConnectionManager();
		returnStatus = -1;
		// no params in URI for post request, they go in the entity below
		URI uri = URIUtils.createURI(protocol, host, 80, path, "", null);
		logger.info("URI is: " + uri.toString());
		
		HttpPost httpPost = new HttpPost(uri);
		String paramString = makeParamString();
		StringEntity se = new StringEntity(paramString);
		logger.info("paramString is: " + paramString);
		httpPost.setEntity(se);
		int tryCount = 0;
		InputStream is = null;
		HttpEntity entity=null;
		while ((returnStatus != HTTP_SUCCESS || entity== null ) && tryCount < GenericHttpClient.NUM_TRIES ) {
			tryCount++;
			try {
				// set this to a failure status, in case we throw on the first few lines.
				returnStatus = HTTP_FAILURE;
				entity=null;
				HttpResponse httpResponse = client.execute(httpPost);
				returnStatus = httpResponse.getStatusLine().getStatusCode();
				entity = httpResponse.getEntity();
				

				if (returnStatus != HTTP_SUCCESS) {
					logger.warn("Bad Status, retry # " + tryCount + " http status: " + returnStatus);
					logger.warn("URI is: " + uri.toString());
				}
				if (entity == null) {
					logger.warn("Http response entity was null.");
					logger.info("URI is: " + uri.toString());
				}
				if (returnStatus != HTTP_SUCCESS || entity == null) {
					// slow down
					Thread.sleep(1000);
					// reset the connection
					client = new DefaultHttpClient();
					connMgr = client.getConnectionManager();
					httpResponse = client.execute(httpPost);
					returnStatus = httpResponse.getStatusLine().getStatusCode();
					entity = httpResponse.getEntity();
				}
			}
			catch (IllegalStateException x) {
				logger.warn("illegal state exception in GenericHttpClient:" + x);
				//x.printStackTrace();
				logger.warn("try count:" + tryCount);
				
			}
			catch (java.net.NoRouteToHostException x) {
				logger.warn("no route to host in GenericHttpClient:" + x);
				//x.printStackTrace();
				logger.warn("try count:" + tryCount);
				
			}
			catch (Exception x) {
				logger.warn("some other exception in GenericHttpClient:" + x);
				//x.printStackTrace();
				logger.warn("try count:" + tryCount);
			}
		}
		if (returnStatus == HTTP_SUCCESS && entity != null){
			is = entity.getContent();
			logger.debug("returning a good result...." + tryCount );
			if (tryCount > 1) {
				logger.info(" returning a good result on try: " + tryCount);
			}
		}
		else {
			logger.error("getPostResponse failed after " + tryCount + " tries." +
					" Param string was: " + paramString + " status was; " + returnStatus);
			if (is == null) {
				logger.error(" input stream is null");
			}
			return null;
		}
    	return is;
	}
	
	/**
	 *  Hits the RESTful service and returns the content as a string.
	 * The status code is available from the getReturnStatus() function;
	 * A negative value means an exception was thrown and the call never completed.
	 * @return page content in a string.
	 * 
	 * TODO: this shuts things down and may not be appropriate in all circumstances.
	 */
    public String getPostResponseString () {    

		StringBuffer contentBuffer = new StringBuffer();
		String retval = "";
		
		InputStream is = null;
    	try {
        	is = getPostResponse();
        	if (is != null) {
	        	//java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is,"UTF-8"));
	        	java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
	        	String s = null;
	        	while ( (s = reader.readLine())  != null) {
	        		contentBuffer.append(s + "\n");
	        	}
	        	////// TODO  ///   is.close();
				logger.debug("Status is: " + returnStatus);
				logger.debug("Content is: " + contentBuffer.toString());
	    		retval =  contentBuffer.toString();
        	}
        	else {
        		logger.error("got null from getPostREsponse.");
        	}
		}
		catch (IOException x) {
			if (returnStatus > -1) returnStatus = -1;
			logger.error("ERROR in OBAService.getResponse()" + x);
			x.printStackTrace();
		}
		catch (URISyntaxException x) {
			if (returnStatus > -1) returnStatus = -1;
			logger.error("ERROR in OBAService.getResponse()" + x);	
			x.printStackTrace();
		}
		finally {
			try {
				if (is != null) { is.close(); }
			}
			catch (IOException x) {
				logger.error("error when closing stream: " + x);
				x.printStackTrace();
			}
			if (client != null) {
				if (client.getConnectionManager() !=null) {
					client.getConnectionManager().shutdown(); 	
				}
			}	
		}
	
		return retval;
	}
    
    public void shutdown() {
    	client.getConnectionManager().shutdown();
    }
	
	public InputStream getGetResponse() throws IOException, URISyntaxException {
		HttpClient client = new DefaultHttpClient();
		returnStatus = -1;
		String paramString = makeParamString();
		URI uri = URIUtils.createURI(protocol, host, 80, path, paramString, null);
		logger.debug("URI is: " + uri.toString());
		logger.debug("Param string is: \"" + paramString + "\"");
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse httpResponse = client.execute(httpGet);
		returnStatus = httpResponse.getStatusLine().getStatusCode();
    	HttpEntity entity = httpResponse.getEntity();
    	return  entity.getContent();
	}
	
	public String getGetResponseString() {  
		InputStream is = null;
		try {
    		StringBuffer contentBuffer = new StringBuffer();
    		returnStatus = -1;   	
    		is = getGetResponse();
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
			String s = null;
			while ( (s = reader.readLine())  != null) {
				contentBuffer.append(s);
			}
			is.close();
			logger.debug("Status is: " + returnStatus);
			logger.debug("Content is: " + contentBuffer.toString());
			return contentBuffer.toString();
		}
		catch (java.io.IOException x) {
			if (returnStatus > -1) returnStatus = -1;
			logger.error("ERROR in OBAService.getGetResponse()" + x);
			x.printStackTrace();
		}
		catch (URISyntaxException x) {
			if (returnStatus > -1) returnStatus = -1;
			logger.error("ERROR in OBAService.getGetResponse()" + x);	
			x.printStackTrace();
		}
		finally {
			try {
				is.close();
			}
			catch (IOException x) {
				logger.error("ioexception when closing stream" + x);
				x.printStackTrace();
			}
			if (client != null) {
				if (client.getConnectionManager() !=null) {
					client.getConnectionManager().shutdown(); 	
				}
			}
		}
		return "";
	}
	
	public int getReturnStatus() {
		return returnStatus;
	}
	
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isGetRequest() {
		return isGetRequest;
	}

	public void setGetRequest(boolean isGetRequest) {
		this.isGetRequest = isGetRequest;
	}

	protected String convertToCSV(List<String> list) {
		StringBuffer sb = new StringBuffer();
		if (list.size() < 1) {
			return "";
		}
		for (String s : list) {
			sb.append(s);
			sb.append(",");
		}
		
		return sb.substring(0, sb.length() -1);
	}
	protected String convertToCSV(String[] list) {
		if (list == null) { return ""; }
		StringBuffer sb = new StringBuffer();
		if (list.length < 1) {
			return "";
		}
		for (String s : list) {
			sb.append(s);
			sb.append(",");
		}
		
		return sb.substring(0, sb.length() -1);
	}
	

	
}
