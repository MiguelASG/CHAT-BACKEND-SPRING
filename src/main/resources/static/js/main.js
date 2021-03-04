function ChatServiceURL() {
	var host = window.location.host;
	var url = 'wss://' + (host) + '/chatService';
	console.log("URL: ",url);
	//return 'ws://localhost:8080/chatService';
	return url;
}



class WSChatChanel {
	constructor(URL, callback){
		
		this.URL = URL;
		this.wsocket = new WebSocket(URL);
		this.wsocket.onopen = (evt) => this.onOpen(evt);
		this.wsocket.onmessage = (evt) => this.onMessage(evt);
		this.wsocket.onError = (evt) => this.onError(evt);
		this.received = callback; // Esto me envia a la clase o lugar donde me
									// llamaron
	}
	
	onOpen(evt){
		console.log("On Open: ",evt);	
	}
	
	onError(evt){
		console.log("On Error: ",evt);
	}
	
	
	onMessage(evt){
		console.log("On Message: ",evt);
		if(evt.data != "Connection established."){
			this.received(evt.data);
		}
	}
	
	sendToServer(tipo,user,room,message){
		let msg = `{ "tipo": "${tipo}", "room": "${room}" , "username": "${user}" , "message": "${message}" }`;		
		this.wsocket.send(msg);		
		
	}
}


const username = document.getElementById('username').innerText;
const chatRoom = document.getElementById('room-name').innerText;
const users = document.getElementById('users');
const sendButton = document.getElementById('send');
const chatForm = document.getElementById('chat-form');
const chatMessages = document.querySelector('.chat-messages'); 


let comunnicationWS = new WSChatChanel (ChatServiceURL(),
		(msg) => {			
			var mensaje = JSON.parse(msg);						
			// console.log("El JSON: ",mensaje);
			if(mensaje.tipo == "userMessage"){
				userMessage(mensaje);	
			}else if(mensaje.userList){
				showUserList(mensaje.userList);
			} else{
				botMessage(mensaje);	
			}
			
			
		});


function showUserList(userList){	
	users.innerHTML = `${userList.map(usr => `<li>${usr}</li>`).join('')}`;
}

function botMessage(mensaje){
	const div = document.createElement('div');
	div.classList.add('message');
	div.innerHTML = `<p class="meta">${mensaje.tipo} <span> ${mensaje.time}</span></p>
		
		<p class="text">${mensaje.username}  ${mensaje.message}</p>
	`;
	chatMessages.appendChild(div);
	chatMessages.scrollTop = chatMessages.scrollHeight;
}

function userMessage(mensaje){
	
	const div = document.createElement('div');
	div.classList.add('message');
	div.innerHTML = `<p class="meta">${mensaje.username} <span> ${mensaje.time}</span></p>
		
		<p class="text">${mensaje.message}</p>
	`;
	chatMessages.appendChild(div);
	chatMessages.scrollTop = chatMessages.scrollHeight;
}



chatForm.addEventListener('submit', (e) => {
	e.preventDefault(); // Para que no se actualice la pagina
	const msg = e.target.elements.msg.value;	
	
	comunnicationWS.sendToServer("userMessage",username,chatRoom,msg);
	e.target.elements.msg.value='';
	e.target.elements.msg.focus();
	
});


comunnicationWS.onOpen = () => comunnicationWS.sendToServer("ChatBot",username,chatRoom,"has joined.");

// Funciona bien pero no imrpime ni muestra alerts.
window.addEventListener('beforeunload',() => {	
	comunnicationWS.sendToServer("ChatBot",username,chatRoom,"has left.");
});