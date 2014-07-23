package com.sohu.sce.autotest.services;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7164542917838289524L;
	
	public ServiceException(String msg,int errorCode) {
		super(msg);
		this.errorCode = errorCode;
	}
	
	public ServiceException(String msg) {
		super(msg);
	}
	
	public ServiceException() {
		super();
	}
	
	private int errorCode = 10500;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
