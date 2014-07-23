package com.sohu.sce.autotest.services.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.sohu.sce.autotest.services.HttpService;

/**
 * rest for app master requests
 * 
 * @author pautcher
 * 
 */

public class HttpServiceImpl implements HttpService {

	Logger log = Logger.getLogger(HttpServiceImpl.class);

	private ExecutorService threadPool;

	private CloseableHttpClient getHttpsClient() {
		try {

			TrustManager[] trustManagers = new TrustManager[1];
			trustManagers[0] = new DefaultTrustManager();

			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustManagers, new SecureRandom());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			return httpclient;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	private class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[0];
		}
	}
	
	@Override
	public HttpResult syncHttpPost(String url, Map<String, String> headers,
			Map<String, String> body, int timeout) {
		return syncHttpPost(url, headers, null, body, timeout, false);
	}
	
	@Override
	public HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params,
			Map<String, String> body, int timeout, boolean httpsFlag) {
		Throwable cause = null;
		try {
			return makePost(url, headers, makeStrBody(params), makeStrBody(body), timeout, httpsFlag);
		} catch (Exception e) {
			cause = e.getCause();
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败"
				: cause.getMessage());
	}


	protected HttpResult makePost(final String url, final Map<String, String> headers, final String params,
			final String body, final int timeout, final boolean httpsFlag) throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client;
				if(httpsFlag){
					client = getHttpsClient();
				}else{
					client = HttpClients.createDefault();
				}
				try {

					StringBuffer sb = new StringBuffer();
					final String urlConnection;
					if (StringUtils.isNotBlank(params)) {
						urlConnection = sb.append(url).append("?").append(params).toString();
					} else {
						urlConnection = sb.append(url).toString();
					}

					HttpPost post = new HttpPost(urlConnection);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							post.addHeader(header.getKey(), header.getValue());
						}
					}

					StringEntity entity = new StringEntity(body, Consts.UTF_8);
					entity.setContentType("application/x-www-form-urlencoded");

					post.setEntity(entity);

					log.info("url:" + urlConnection + ",body:" + body);

					CloseableHttpResponse res = client.execute(post);
					try {
						int code = res.getStatusLine().getStatusCode();
						String resEntity = EntityUtils.toString(res.getEntity());
						HttpResult result = new HttpResult(code, resEntity, res.getAllHeaders());
						return result;
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}


	protected String makeStrBody(Map<String, String> body) {
		if (body == null || body.isEmpty()) {
			return URLEncodedUtils.format(new ArrayList<NameValuePair>(), Consts.UTF_8);
		}
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : body.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return URLEncodedUtils.format(list, "utf-8");
	}

	protected List<NameValuePair> makeFormParams(Map<String, String> body) {
		if (body == null || body.isEmpty()) {
			return new ArrayList<NameValuePair>();
		}
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : body.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return list;

	}

	@Override
	public HttpResult syncHttpGet(String url, Map<String, String> headers, 
			Map<String, String> params, int timeoutInMs) {
		return syncHttpGet(url, headers, params, timeoutInMs, false);
	}
	
	/**
	 * 下面是 http get请求
	 */
	@Override
	public HttpResult syncHttpGet(String url, Map<String, String> headers, 
			Map<String, String> params, int timeoutInMs, boolean httpsFlag) {
		Throwable cause = null;
		try {
			return makeGet(url, headers, makeParams(params), timeoutInMs, httpsFlag);
		} catch (Exception e) {
			cause = e.getCause();
			log.error(e.getMessage(), e);
			if (cause instanceof TimeoutException) {
				return new HttpResult(504, "timeout");
			}
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败"
				: cause.getMessage());
	}


	protected HttpResult makeGet(String url, final Map<String, String> headers, 
			final String params, final int timeout, final boolean httpsFlag)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		StringBuffer sb = new StringBuffer();
		final String urlConnection;
		if (StringUtils.isNotBlank(params)) {
			urlConnection = sb.append(url).append("?").append(params).toString();
		} else {
			urlConnection = sb.append(url).toString();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client;
				if(httpsFlag){
					client = getHttpsClient();
				}else{
					client = HttpClients.createDefault();
				}
				try {

					HttpGet get = new HttpGet(urlConnection);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							get.addHeader(header.getKey(), header.getValue());
						}
					}

					log.info("url:" + urlConnection);

					CloseableHttpResponse res = client.execute(get);
					try {
						int code = res.getStatusLine().getStatusCode();
						String entity = EntityUtils.toString(res.getEntity(), Consts.UTF_8);
						Header[] headers = res.getAllHeaders();
						return new HttpResult(code, entity, headers);
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});
		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			ex.printStackTrace();
			Throwable cause = ex.getCause();
			if (cause instanceof TimeoutException) {
				return new HttpResult(504, null);
			}
			return new HttpResult(500, null);
		}
	}

	protected String makeParams(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return StringUtils.EMPTY;
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			try {
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * HTTP DELETE METHOD
	 */
	@Override
	public HttpResult syncHttpDelete(String url, Map<String, String> headers, Map<String, String> params, int timeout) {
		Throwable cause = null;
		try {
			return makeDelete(url, headers, makeStrBody(params), timeout);
		} catch (Exception e) {
			cause = e.getCause();
			log.error(e.getMessage(), e);
			if (cause instanceof TimeoutException) {
				return new HttpResult(504, null);
			}
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败"
				: cause.getMessage());
	}

	protected HttpResult makeDelete(final String url, final Map<String, String> headers, final String params,
			final int timeout) throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = HttpClients.createDefault();
				try {
					HttpDelete delete = new HttpDelete(url + "?" + params);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							delete.addHeader(header.getKey(), header.getValue());
						}
					}

					log.info("url:" + url + "?" + params);

					CloseableHttpResponse res = client.execute(delete);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(
								res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			Throwable cause = ex.getCause();
			if (cause instanceof TimeoutException) {
				return new HttpResult(504, null);
			}
			return new HttpResult(500, null);
		}
	}

	/**
	 * HTTP PUT METHOD
	 */
	@Override
	public HttpResult syncHttpPut(String url, Map<String, String> headers, Map<String, String> params,
			Map<String, String> body, int timeout) {
		Throwable cause = null;
		try {
			return makePut(url, headers, makeStrBody(params), makeFormParams(body), timeout);
		} catch (Exception e) {
			cause = e.getCause();
			log.error(e.getMessage(), e);
			if (cause instanceof TimeoutException) {
				return new HttpResult(504, null);
			}
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败"
				: cause.getMessage());
	}

	protected HttpResult makePut(final String url, final Map<String, String> headers, final String params,
			final List<NameValuePair> body, final int timeout) throws InterruptedException, ExecutionException,
			TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = HttpClients.createDefault();
				try {

					HttpPut put = new HttpPut(url + "?" + params);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							put.addHeader(header.getKey(), header.getValue());
						}
					}

					// StringEntity entity = new StringEntity(body,
					// Consts.UTF_8);
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(body, Consts.UTF_8);

					put.setEntity(entity);

					log.info("url:" + url + "?" + params + ",body:" + body);

					CloseableHttpResponse res = client.execute(put);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(
								res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			Throwable cause = ex.getCause();
			if (cause instanceof TimeoutException) {
				return new HttpResult(504, null);
			}
			return new HttpResult(500, null);
		}
	}

	protected synchronized void makeThreadPool() {
		Properties sysProps = System.getProperties();
		String poolSizeStr = sysProps.getProperty("http.threadpool.size");
		int poolSize = 50;
		if (!StringUtils.isBlank(poolSizeStr)) {
			poolSize = Integer.parseInt(poolSizeStr);
		}

		this.threadPool = Executors.newFixedThreadPool(poolSize);
	}

}