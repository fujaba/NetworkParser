package de.uniks.networkparser.test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.petaf.JsonToken;
import de.uniks.networkparser.ext.petaf.LoginService;
import de.uniks.networkparser.ext.petaf.User;
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
		RESTServiceTask service = new RESTServiceTask(8080, map, uni);
		LoginService loginService = new LoginService().withUser(new User().with("admin", "12346"));
		JsonToken token = new JsonToken();
		token.withSecret("Top Secret");
//		.withAlgorytm(JsonToken.HS256)
		loginService.withJsonToken(token);
		service.withLoginService(loginService);
		
		
		Thread t2 = new Thread( service );
		t2.start();
	}
}
