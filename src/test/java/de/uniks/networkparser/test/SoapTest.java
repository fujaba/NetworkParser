package de.uniks.networkparser.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.event.SoapObject;
import de.uniks.networkparser.event.util.SoapCreator;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.xml.XMLEntity;

public class SoapTest {

	@Test
	public void testSoap() throws IOException{
		SoapObject item = new SoapObject().withNamespace("soap");
		item.withBody(
				XMLEntity.TAG("GetMatchByMatchID")
					.withKeyValue("xmlns", "http://msiggi.de/Sportsdata/Webservices")
					.withChild(XMLEntity.TAG("MatchID").withValueItem("28682"))
				);

		String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +item.toString();
		String reference = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetMatchByMatchID xmlns=\"http://msiggi.de/Sportsdata/Webservices\"><MatchID>28682</MatchID></GetMatchByMatchID></soap:Body></soap:Envelope>";
		Assert.assertEquals(reference, body);

		// Send
//		URL u = new URL("http://www.openligadb.de/Webservices/Sportsdata.asmx");
//		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//		//Adjust connection
//		conn.setDoOutput(true);
//		conn.setDoInput(true);
//		conn.setRequestMethod("POST");
//		conn.setRequestProperty("Host", "www.openligadb.de");
//		conn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
//		conn.setRequestProperty( "Content-Length", "" +body.length());
//		conn.setRequestProperty("SOAPAction" , "http://msiggi.de/Sportsdata/Webservices/GetMatchByMatchID");
//		OutputStreamWriter soapRequestWriter = new OutputStreamWriter(conn.getOutputStream());
//
//		soapRequestWriter.write(body);
//		soapRequestWriter.flush();
//
//		// GEt
//		BufferedReader soapRequestReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		StringBuilder answer = new StringBuilder();
//		String line;
//		while ((line = soapRequestReader.readLine()) != null) {
//			answer.append(line);
//		}
//		soapRequestWriter.close();
//		soapRequestReader.close();
//		conn.disconnect();
	}
	@Test
	public void testSoapDesizalizaion() throws IOException{
		StringBuilder answer = new StringBuilder();
		answer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><GetMatchByMatchIDResponse xmlns=\"http://msiggi.de/Sportsdata/Webservices\"><GetMatchByMatchIDResult><matchID>28682</matchID><matchDateTime>2014-07-04T18:00:00</matchDateTime><TimeZoneID>W. Europe Standard Time</TimeZoneID><matchDateTimeUTC>2014-07-04T16:00:00Z</matchDateTimeUTC><groupID>14808</groupID><groupOrderID>3</groupOrderID><groupName>Viertelfinale</groupName><leagueID>676</leagueID><leagueName>Fussball Weltmeisterschaft 2014</leagueName><leagueSaison>2014</leagueSaison><leagueShortcut>WM-2014</leagueShortcut><nameTeam1>Frankreich</nameTeam1><nameTeam2>Deutschland</nameTeam2><idTeam1>144</idTeam1><idTeam2>139</idTeam2><iconUrlTeam1>http://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Flag_of_France.svg/20px-Flag_of_France.svg.png</iconUrlTeam1><iconUrlTeam2>http://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/Flag_of_Germany.svg/20px-Flag_of_Germany.svg.png</iconUrlTeam2><pointsTeam1>0</pointsTeam1><pointsTeam2>1</pointsTeam2><lastUpdate>2014-07-04T20:17:06.473</lastUpdate><matchIsFinished>true</matchIsFinished><matchResults><matchResult><resultName>Halbzeitergebnis</resultName><pointsTeam1>0</pointsTeam1><pointsTeam2>1</pointsTeam2><resultOrderID>1</resultOrderID><resultTypeName>Halbzeit</resultTypeName><resultTypeId>1</resultTypeId></matchResult><matchResult><resultName>Endergebnis</resultName><pointsTeam1>0</pointsTeam1><pointsTeam2>1</pointsTeam2><resultOrderID>2</resultOrderID><resultTypeName>nach Nachspielzeit</resultTypeName><resultTypeId>3</resultTypeId></matchResult></matchResults><goals><Goal><goalID>28817</goalID><goalMachID>28682</goalMachID><goalScoreTeam1>0</goalScoreTeam1><goalScoreTeam2>1</goalScoreTeam2><goalMatchMinute>12</goalMatchMinute><goalGetterID>14307</goalGetterID><goalGetterName>Mats Hummels</goalGetterName><goalPenalty xsi:nil=\"true\" /><goalOwnGoal xsi:nil=\"true\" /><goalOvertime xsi:nil=\"true\" /><goalComment /></Goal></goals><location><locationID>915</locationID><locationCity>Rio de Janeiro</locationCity><locationStadium>Maracan</locationStadium></location><NumberOfViewers xsi:nil=\"true\" /></GetMatchByMatchIDResult></GetMatchByMatchIDResponse></soap:Body></soap:Envelope>");
		// Deserialization
		IdMap map= new IdMap();
		map.with(new SoapCreator().withNamespace("soap"));

		SoapObject soapAnswer = (SoapObject) map.decode(answer.toString());

		Assert.assertNotNull(soapAnswer.getBody());

		EntityList entity = soapAnswer.getBody().getChildren().get(0).getChildren().get(0);
		Assert.assertEquals(25, ((XMLEntity)entity).getChildren().size() );

	}
}
