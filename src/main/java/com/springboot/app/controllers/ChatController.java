package com.springboot.app.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.parser.Entity;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080" })
@Controller
public class ChatController {

	@GetMapping({ "/", "/index" })
	public String roomForm(Model model) {
		return "index";
	}

	@PostMapping("/index")
	public String chatForm(Model model) {
		return "redirect:/chat";
	}

	@GetMapping("/chat")
	public String chat(@RequestParam(value = "username") String username, @RequestParam(value = "room") String room,
			Model model) {
		model.addAttribute("room", room);
		model.addAttribute("username", username);
		return "chat";
	}

	@GetMapping(path = "/mensajes", produces=MediaType.APPLICATION_JSON_VALUE)	
	public ResponseEntity<Object> sayHello()
    {
         //Get data from service layer into entityList.
		System.out.println("ENTRA EN MESSAGES");
        //List<JSONObject> entities = new ArrayList<JSONObject>();
		//JSONObject entity = new JSONObject();
		//entity.put("aa", "bb");
		//JSONObject entity2 = new JSONObject();


		//Conectese con la base datos y busque todos los usuarios
		//Convierta los usuarios en json
		//RETORNE LOS USUARIOS A REACT

		

		HashMap<String, String> map = new HashMap<>();
		map.put("Usuario", "Eduard Jimenez");
		map.put("Usuario2", "Miguel Sanchez");
		map.put("Usuario3", "Cear Gonzalez");
		//entity2.put("mensaje", "hola");
		//entities.add(entity2); 
		//System.out.println("Entities: "+entities.toString());       
		//System.out.println("Entity: "+entity.toString());
		return ResponseEntity.status(HttpStatus.OK).body(map);
        //return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }
	/*
	public Map mensajes() {
		System.out.println("ENTRANDO EN MENSAJESSSSSSS");
		HashMap<String, String> map = new HashMap<>();
		map.put("key", "value");
		map.put("foo", "bar");
		map.put("aa", "bb");
		
		//String msg = '{' + "\"mensjes\"" + ":" + '1' + '}';
		//JSONObject msgJson = new JSONObject(msg);
		return map;
	}
*/
	@PostMapping("/ayuda_post")
	public String ayuda_react() {
		System.out.println("ENTRNADO EN POST DE REACT!!");
		return "";
	}

}
