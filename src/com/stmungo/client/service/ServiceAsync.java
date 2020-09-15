package com.stmungo.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

//Using Remote Procedure Calls to make Asyncronous callbacks between 
//server and client side ensures the browser doensn't hang due to multithreading
public interface ServiceAsync {
	void getTranslatorMain(String text, AsyncCallback callback);

	void getValidatorMain(String vText, AsyncCallback callback);

	void getTranslatorRole(String text, AsyncCallback callback);

	void getValidatorRole(String vText, AsyncCallback callback);

	void getTranslatorProtocol(String text, AsyncCallback callback);

	void getValidatorProtocol(String vText, AsyncCallback callback);

	void getTranslatorChoice(String text, AsyncCallback callback);

	void getValidatorChoice(String vText, AsyncCallback callback);

	void getTranslatorTypestate(String text, AsyncCallback callback);

	void getValidatorTypestate(String vText, AsyncCallback callback);

}