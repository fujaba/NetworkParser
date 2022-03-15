package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.io.BufferedByteInputStream;
import de.uniks.networkparser.ext.io.StringOutputStream;
import de.uniks.networkparser.ext.io.ZipContainer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;

public class ZipTest {

	@Test
	public void testZipFile() throws IOException {
		GroupAccount account= new GroupAccount();
		account.createPersons().withName("Albert");
		account.createPersons().withName("Tobi");

		IdMap map= new IdMap();
		map.with(new PersonCreator());
		map.with(new GroupAccountCreator());

		JsonObject jsonObject = map.toJsonObject(account);
		int len = jsonObject.toString().length();
		StringOutputStream stream=new StringOutputStream();
		ZipContainer zip = new ZipContainer();
		zip.encode(jsonObject, stream, true);

		BufferedByteInputStream inputStream = BufferedByteInputStream.create(stream);
		BaseItem readData = zip.decode(inputStream);

		assertEquals(len, readData.toString().length());
	}
}
