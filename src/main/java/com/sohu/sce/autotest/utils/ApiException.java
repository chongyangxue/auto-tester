/**
 * 
 */
package com.sohu.sce.autotest.utils;

import java.util.Map;

/**
 * @author zhouhe
 * @since 下午5:15:56
 */
public class ApiException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 149328166228976040L;
	
	public ApiException() {
		super();
	}
	
	public ApiException(String msg,int errorCode) {
		super(msg);
		this.errorCode = errorCode;
	}
	
	public ApiException(Map<String,Object> errorMap) {
		super((String)errorMap.get("message"));
		this.errorCode = (Integer)errorMap.get("code");
	}

	private int errorCode = 10500;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
