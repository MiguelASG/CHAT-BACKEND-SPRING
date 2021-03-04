package com.springboot.app.endpoints;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.springboot.app.entities.Usuario;

import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

@Controller
@ServerEndpoint("/chatService")
public class ChatEndPoint {

	private static final Logger logger = Logger.getLogger(ChatEndPoint.class.getName());

	static Queue<Session> sessions = new ConcurrentLinkedQueue<>();

	private Session ownSession = null;

	static HashMap<String, List<Usuario>> rooms = new HashMap<>();

	private Usuario usuario;

	/*
	 * Cosas por mejorar = Crear inyecciones porque esta muy cargada la clase, y debería solo enviar mensajes
	 * Que la(s) otra(s) se encarguen de validar, adicionar, eliminar, formatear, etc...
	 */
	
	
	public void send(String message) {

		try {

			JSONObject msgJson = new JSONObject(message);
			String room = msgJson.get("room").toString();			
			Usuario usuario;
			msgJson = addTime(msgJson);
			
			if (rooms.containsKey(room)) {

				addUser(room, msgJson);

			} else {
				usuario = createUser(msgJson);
				List<Usuario> usuarios = new ArrayList<Usuario>();
				usuarios.add(usuario);
				rooms.put(room, usuarios);
			}

			validateBotAnswer(msgJson);			
			sendMessage(room, msgJson.toString());

		} catch (JSONException e) {
			logger.log(Level.SEVERE, null, "An error was found JSON.");
		}
	}
	
	
	private JSONObject addTime(JSONObject msgJson) {
		LocalDateTime fecha = LocalDateTime.now();	      
		DateTimeFormatter isoHora = DateTimeFormatter.ISO_LOCAL_TIME;
        String time = fecha.format(isoHora).toString();
        
		msgJson.append("time", time);
		
		return msgJson;
	}

	
	private void validateBotAnswer(JSONObject msgJson) {
		if (msgJson.get("tipo").toString().equals("ChatBot")) {
			if (msgJson.get("message").toString().equals("has joined.")) {

				sendUserList(msgJson);
				
			} else if (msgJson.get("message").toString().equals("has left.")) {
				
				Usuario newUser = findUser(msgJson.get("room").toString(),msgJson);
				
				rooms.get(msgJson.get("room").toString()).remove(newUser);
				
				sendUserList(msgJson);
			}
		}
	}

	private void sendMessage(String room, String message) {

		rooms.get(room).forEach(user -> {
			try {

				user.getSession().getBasicRemote().sendText(message);						        
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void addUser(String room, JSONObject msgJson) {		
		
		if (findUser(room,msgJson) == null) {
			usuario = createUser(msgJson);
			rooms.get(room).add(createUser(msgJson));
		}
	}
	
	private Usuario findUser(String room, JSONObject msgJson) {
		return rooms.get(room).stream()
		.filter(user -> user.getUsername().equals(msgJson.get("username").toString()))
		.findFirst().orElse(null);
	}

	private Usuario createUser(JSONObject msgJson) {
		return new Usuario(msgJson.get("username").toString(), msgJson.get("room").toString(), ownSession);
	}

	private void sendUserList(JSONObject msgJson) {
		List<String> usuarios = new ArrayList<String>();

		rooms.get(msgJson.get("room").toString()).forEach(user -> {
			usuarios.add("\"" + user.getUsername().toString() + "\"");
		});
		// ;
		String msg = '{' + "\"userList\"" + ":" + usuarios.toString() + '}';
		
		sendMessage(msgJson.get("room").toString(), msg);
		

	}

	@OnMessage
	public void message(String message, Session session) {
		
		this.send(message);
	}

	@OnOpen
	public void openConnection(Session session) {
		
		sessions.add(session);
		ownSession = session;
		logger.log(Level.INFO, "Connection opened.");
		try {
			session.getBasicRemote().sendText("Connection established.");

		} catch (IOException e) {
			logger.log(Level.SEVERE, null, e);
		}

	}

	@OnClose
	public void closeConnection(Session session) {
		sessions.remove(session);

		logger.log(Level.INFO, "Connection closed.");
	}

	@OnError
	public void error(Session session, Throwable t) {
		sessions.remove(session);
		logger.log(Level.INFO, t.toString());
		logger.log(Level.INFO, "Connection error.");
	}

}
