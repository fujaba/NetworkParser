package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.ModelHistory;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyFileSystem;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxySQL;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.ItemCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class PetaFTest {

	@Test
	public void testSpace() {
		University university = new University();
		Student createStudents = university.createStudents();
		createStudents.setName("Albert");

		Space space=new Space();
		IdMap map = UniversityCreator.createIdMap("42");
		space.withCreator(map);
		space.createModel(university, "build/ModelFile.json").startModelDistribution();

		createStudents.setName("Stefan");
		university.createStudents().setName("Alex");
		space.close();
	}

	@Test
	public void testMessage() {
		ConnectMessage connectMsg = ConnectMessage.create();
		Space space=new Space();
		space.startServer(5000);
		space.sendMessage(connectMsg, false);
		Assert.assertNotNull(space.convertMessage(connectMsg));

		space.close();
	}

	@Test
	public void testSQL() {
		University university = new University();
		Student createStudents = university.createStudents();

		Space space=new Space();
		IdMap map = UniversityCreator.createIdMap("42");
		space.withCreator(map);
		space.with(new NodeProxySQL().withDriver("jdbc:sqlite:build/petaf.db"));
		space.createModel(university);

		createStudents.setName("Albert");
	}


//	@Test
	public void testServer() throws InterruptedException {
		NodeProxyTCP server = NodeProxyTCP.createServer(5000);
		Assert.assertTrue(server.start());
		server.start();
		server.close();
		
//		Space space=new Space();
//		space.with(NodeProxyLocal.create(e -> {
//			return true;
//		}));
//		Thread.sleep(5000);
	}

	@Test
	public void testModelChange() {
		// DataModel
		University university = new University();
		Student stefan = university.createStudents();

		// Serialization
		Space space=new Space().withCreator(UniversityCreator.createIdMap("42"));
		space.createModel(university, "build/change.json");
//		space.createServer(500);
//
//		space.connectToPeer("141.51.123.55", 500);

		space.startModelDistribution(false);
		// Change Model
		stefan.setName("Stefan");
		Student alex = university.createStudents();
		alex.setName("Alex");


		ModelHistory history = space.getHistory();
		Assert.assertNotNull(history);
	}


	// With Name
	@Test
	public void testModelChangeName() {
		// DataModel
		University university = new University();
		Student stefan = university.createStudents();

		// Serialization
		Space space=new Space().withCreator(UniversityCreator.createIdMap("42"));
		space.withName("build/Albert", university);

		// Change Model
		stefan.setName("Stefan");
		Student alex = university.createStudents();
		alex.setName("Alex");


		ModelHistory history = space.getHistory();
		Assert.assertNotNull(history);
	}


	@Test
	public void test() {
		Space space = new Space();
		NodeProxy proxy = space.getOrCreateProxy("141.51.116.1", 5000);
		space.getOrCreateProxy("141.51.116.1", 5010);

		ConnectMessage message=new ConnectMessage().withAddToReceived(proxy);

		String convertMessage = space.convertMessage(message);

		ByteBuffer buffer = new ByteBuffer();
		buffer.with(convertMessage);

		ConnectMessage newMessage = (ConnectMessage) space.getMap().decode(buffer);
		Assert.assertEquals(newMessage.getReceiver(), proxy);
	}

	@Test
	public void Serialization() {
		Space space = new Space();

		ConnectMessage connect = ConnectMessage.create();
		JsonObject json = new JsonObject();
		json.with("Key", "42");
		connect.withMessage(json);
		Assert.assertEquals("{\"class\":\"ConnectMessage\",\"msg\":{\"Key\":\"42\"}}", space.convertMessage(connect));

	}

	@Test
	public void testBackup() {
		Space space = new Space().withName("Albert");
		space.withPath(".");

		space.withCreator(new GroupAccountCreator(), new ItemCreator());

		NodeProxyFileSystem fsProxy = new NodeProxyFileSystem("build/Albert.json");
		fsProxy.enableGitFilter();
		fsProxy.withFullModell(true);
		space.with(fsProxy);

		GroupAccount groupAccount = new GroupAccount();
		space.createModel(groupAccount, "build/changes.json");
//		fsProxy.load(groupAccount);

		groupAccount.setName("Albert");
//		fsProxy.withFullModell(false);
//
		Item item1 = groupAccount.createItem().withDescription("Beer").withValue(2.49);
		Assert.assertNotNull(item1);


		Space space2 = new Space();

		space2.withCreator(GroupAccountCreator.createIdMap("42"));

		NodeProxyFileSystem fsProxy2 = new NodeProxyFileSystem("build/changes.json");

		space2.with(fsProxy2);

		GroupAccount ga2 = new GroupAccount();
		fsProxy2.load(ga2);



		FileBuffer.deleteFile(fsProxy.getFileName());
		FileBuffer.deleteFile(fsProxy2.getFileName());
		Assert.assertEquals(groupAccount.getName(), ga2.getName());
	}


	@Test
	public void testFilter() {
		ConnectMessage connectMessage = new ConnectMessage();
		Space space=new Space();
		space.getMessageFilter().withFormat((byte)4); // Filter.FORMAT_SHORTCLASS
		Assert.assertEquals("{\"class\":\"ConnectMessage\"}", space.convertMessage(connectMessage));
	}
}
