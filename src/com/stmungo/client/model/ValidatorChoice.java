package com.stmungo.client.model;

import java.io.Serializable;

public class ValidatorChoice implements Serializable{
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}