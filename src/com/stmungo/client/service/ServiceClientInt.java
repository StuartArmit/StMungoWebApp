package com.stmungo.client.service;

//Used simply to enforce method use in implementation
public interface ServiceClientInt {
	void getTranslatorMain(String text);

	void getValidatorMain(String vText);

	void getTranslatorRole(String text);

	void getValidatorRole(String vText);

	void getTranslatorProtocol(String text);

	void getValidatorProtocol(String vText);

	void getTranslatorChoice(String text);

	void getValidatorChoice(String vText);

	void getTranslatorTypestate(String text);

	void getValidatorTypestate(String vText);

}
