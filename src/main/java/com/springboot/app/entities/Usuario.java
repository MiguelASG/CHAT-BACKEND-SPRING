package com.springboot.app.entities;

import java.util.List;

import javax.websocket.Session;

public class Usuario {

	private String room;
	
	private String username;
	
	private List<String> messages;
	
	private Session session;

	
	public Usuario(String username, String room, Session session) {
		this.username = username;
		this.room = room;		
		this.session = session;
	}
	
	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}


	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	
	
}
