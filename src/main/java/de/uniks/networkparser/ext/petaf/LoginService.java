package de.uniks.networkparser.ext.petaf;

import java.net.Socket;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class LoginService implements Condition<SimpleEvent> {
	private SimpleList<User> users;
	private JsonToken tokener;

	public HTMLEntity getLogin() {
		HTMLEntity entity = new HTMLEntity();
		entity.createTag("h1", "Login");
		XMLEntity formTag = entity.createTag("form")
				.withKeyValue("action", "/auth").withKeyValue("method", "post")
				.withKeyValue("enctype", "application/json");
		formTag.createChild("input", "name", "username");
		formTag.createChild("input", "name", "password", "type", "password");
		formTag.createChild("input", "type", "submit", "value", "Login");
		return entity;
	}

	public void writeCookie(Socket socket, String token) {
//		writer.println("HTTP/1.0 200 Ok");
//		writer.println("Set-Cookie key=value Max-Age=8640");
//		writer.println("Content-Type text/html");
	}

	@Override
	public boolean update(SimpleEvent value) {
		if (value == null || value.getSource() instanceof HTTPRequest == false) {
			return false;
		}
		HTTPRequest request = (HTTPRequest) value.getSource();
		request.readHeader();

		User user = null;
		String userName = null ;
		if(request.getContent()!= null && tokener != null) {
			SimpleKeyValueList<String, String> params = request.parseForm();
			userName = params.get("username");
			String password = params.get("password");
			// Validate Data
			user = validateUser(userName, password);
		}
		if (user != null) {
			String refreshToken = EntityUtil.randomString(64);
			JsonToken generator  = tokener.create();
			generator.withSubject(userName);
			String token = HTTPRequest.BEARER+" "+generator.getToken();
			user.addToken(HTTPRequest.HTTP_AUTHENTIFICATION, token);
			user.addToken(HTTPRequest.HTTP_REFRESH, refreshToken);
			request.write("Login ok");
		} else {
			HTMLEntity login = this.getLogin();
			request.write(login);
			return false;
		}
		return true;
	}

	public LoginService withUser(User user) {
		if( this.users == null) {
			this.users = new SimpleList<User>();
		}
		this.users.add(user);
		return this;
	}
	
	public LoginService withJsonToken(JsonToken tokener) {
		this.tokener = tokener;
		return this;
	}
	
	public User validateUser(String... values) {
		if(values == null || values.length<1) {
			return null;
		}
		if(this.users == null) {
			return null;
		}
		if(values.length ==  1) {
			// Authenfication Key
			for(int i=0;i<this.users.size();i++ ) {
				User user = this.users.get(i);
				if(user.contains(HTTPRequest.HTTP_AUTHENTIFICATION, values[0])) {
					return user;
				}
				if(user.contains(HTTPRequest.HTTP_REFRESH, values[0])) {
					return user;
				}
			}
		}else if(values.length == 2) {
			// Validate Username/Password
			String userName = values[0];
			String password = values[1];
			if(userName == null || password == null) {
				return null;
			}
			for(int i=0;i<this.users.size();i++ ) {
				User user = this.users.get(i);
				if(userName.equalsIgnoreCase(user.getName()) && password.equals(user.getPassword())) {
					return user;
				}
			}
			
		}
		return null;
	}
}
