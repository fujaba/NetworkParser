package de.uniks.networkparser.test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.http.Configuration;
import de.uniks.networkparser.ext.http.JsonToken;
import de.uniks.networkparser.ext.http.LoginService;
import de.uniks.networkparser.ext.http.User;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class RestServiceLogin {

	public static void main(String[] args) {
		new RestServiceLogin().testLogin();
	}
	public void testLogin() {
		IdMap map = new IdMap();
		map.withCreator(new UniversityCreator());
		map.withCreator(new StudentCreator());
		map.withCreator(new RoomCreator());
		map.withTimeStamp(1);

		University uni = new University().withName("Uni Kassel");
		RESTServiceTask service = new RESTServiceTask().createServer(new Configuration().withPort(8080), map, uni);
		
		LoginService loginService = new LoginService().withUser(new User().with("admin", "12346"));
		loginService.withWriteCookie(true);
		JsonToken token = new JsonToken();
		token.withSecret("Top Secret").withExpiration(JsonToken.EXPIRATION_DAY);
//		.withAlgorytm(JsonToken.HS256)
		loginService.withJsonToken(token);
		service.withLoginService(loginService);
	}
}
