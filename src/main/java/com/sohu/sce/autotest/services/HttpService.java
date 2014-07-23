package com.sohu.sce.autotest.services;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.Header;

import com.sohu.sce.autotest.utils.ApiException;



/**
 * Copyright @ 2013 sohu.com Co.Ltd All right reserved. 功能描述：业务逻辑操作接口，rest服务
 * 
 * @author pautcher
 * @since 2013-4-1
 */
public interface HttpService {

	final class HttpResult {
		private int code;
		private String result;
		private Header[] headers;

		public HttpResult(int code, String result) {
			this.code = code;
			this.result = result;
		}
		
		public HttpResult(int code, String result, Header[] headers) {
			this.code = code;
			this.result = result;
			this.headers = headers;
		}
		
		public Header[] getHeaders(){
			return headers;
		}

		public int getCode() {
			return code;
		}

		public String getResult() {
			return result;
		}
	}

	final class HttpStreamResult {
		private int code;
		private InputStream is;

		public HttpStreamResult(int code, InputStream is) {
			this.code = code;
			this.is = is;
		}

		public int getCode() {
			return code;
		}

		public InputStream getIs() {
			return is;
		}
	}

	/**
	 * 
	 * @param url
	 * @param headers			headers
	 * @param params			URL参数
	 * @param timeoutInMs		最大等待时间，毫秒
	 * @param httpsFlag			true: https请求；false:普通http请求
	 * @return
	 * @throws ApiException
	 */
	HttpResult syncHttpGet(String url, Map<String, String> headers, Map<String, String> params, 
			int timeoutInMs, boolean httpsFlag)	throws ApiException;
	
	/**
	 * 
	 * @param url
	 * @param headers			headers
	 * @param params			URL参数
	 * @param timeoutInMs		最大等待时间，毫秒
	 * @return
	 * @throws ApiException
	 */
	HttpResult syncHttpGet(String url, Map<String, String> headers, Map<String, String> params, 
			int timeoutInMs) throws ApiException;

	/**
	 * 
	 * @param url
	 * @param headers			headers
	 * @param params			URL参数
	 * @param body				body
	 * @param timeout			最大等待时间，毫秒
	 * @param httpsFlag			TRUE: https请求；FALSE:普通http请求
	 * @return
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params,
			Map<String, String> body, int timeout, boolean httpsFlag);
	
	/**
	 * 
	 * @param url
	 * @param headers			headers
	 * @param params			URL参数
	 * @param body				body
	 * @param timeout			最大等待时间，毫秒
	 * @return
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> body, int timeout);
	
	HttpResult syncHttpDelete(String url, Map<String, String> headers, Map<String, String> params, int timeout);

	HttpResult syncHttpPut(String url, Map<String, String> headers, Map<String, String> params,
			Map<String, String> body, int timeout);

}
