package de.uniks.networkparser.ext.petaf.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;
import de.uniks.networkparser.json.JsonObject;

//TODO add functionality
public class NodeProxyGoogleCloud extends NodeProxy{
//	private Connection connection;
	private String API_KEY;
	private String senderId;
	@Override
	public int compareTo(NodeProxy o) {
		return 0;
	}

	@Override
	public String getKey() {
		return API_KEY;
	}

	@Override
	public boolean close() {
		return true;
	}

	@Override
	protected boolean initProxy() {
		withType(NodeProxyType.OUT);
		return true;
	}

	@Override
	public boolean isSendable() {
		return API_KEY != null;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new NodeProxyGoogleCloud();
	}
	
//	        if (args.length < 1 || args.length > 2 || args[0] == null) {
//	            System.err.println("usage: ./gradlew run -Pmsg=\"MESSAGE\" [-Pto=\"DEVICE_TOKEN\"]");
//	            System.err.println("");
//	            System.err.println("Specify a test message to broadcast via GCM. If a device's GCM registration token is\n" +
//	                    "specified, the message will only be sent to that device. Otherwise, the message \n" +
//	                    "will be sent to all devices subscribed to the \"global\" topic.");
//	            System.err.println("");
//	            System.err.println("Example (Broadcast):\n" +
//	                    "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\"\n" +
//	                    "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\"");
//	            System.err.println("");
//	            System.err.println("Example (Unicast):\n" +
//	                    "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"\n" +
//	                    "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"");
//	            System.exit(1);
	@Override
	protected boolean sending(Message msg) {
		boolean success =  super.sending(msg);
		if(success) {
			return true;
		}
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JsonObject jGcmData = new JsonObject();
            JsonObject jData = new JsonObject();
            
        	String buffer;
            if(this.space != null) {
				buffer = this.space.convertMessage(msg);
			} else {
				buffer = msg.toString();
			}
            jData.put("message", buffer.trim());
            // Where to send GCM message.
            if (senderId != null && senderId.length() > 1) {
                jGcmData.put("to", senderId.trim());
            } else {
                jGcmData.put("to", "/topics/global");
            }
            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            byte[] messageArray = new byte[BUFFER];
            ByteBuffer readBuffer=new ByteBuffer();
            InputStream is = conn.getInputStream();
    		int bytesRead;
    		while (-1 != (bytesRead = is.read(messageArray, 0, BUFFER))) {
    			readBuffer.with(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
    			if(bytesRead != BUFFER) {
    				break;
    			}
    		}
            String resp = buffer.toString();
            System.out.println(resp);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
        return true;
	}
}
