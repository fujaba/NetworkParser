package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.bytes.qr.ByteMatrix;
import de.uniks.networkparser.bytes.qr.DecoderResult;
import de.uniks.networkparser.bytes.qr.ErrorCorrectionLevel;
import de.uniks.networkparser.bytes.qr.QRCode;
import de.uniks.networkparser.bytes.qr.QRTokener;

public class testQRCodeTest {
	@Test
	public void testGen() throws Exception {
		QRTokener tokener = new QRTokener();
		
		QRCode encode = tokener.encode("test", ErrorCorrectionLevel.Q);
		ByteMatrix matrix = encode.getMatrix();
		
		StringBuilder sb=new StringBuilder();
		sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\r\n");
		int posx;
		int posy = 0;
		for(int y=0;y<matrix.getHeight();y++) {
			posx=0;
			for(int x=0;x<matrix.getWidth();x++) {
				if(matrix.get(x, y)>0) {
					sb.append("<rect x=\""+posx+"\" y=\""+posy+"\" width=\"3\" height=\"3\" fill=\"back\"/>");
				}else {
					sb.append("<rect x=\""+posx+"\" y=\""+posy+"\" width=\"3\" height=\"3\" fill=\"white\"/>");
				}
				posx +=3;
			}
			sb.append("\r\n");
			posy+=3;
		}
		sb.append("</svg>");
//		FileWriter writer=new FileWriter(new File("qr.svg"));
//		writer.write(sb.toString());
//		writer.close();
		
		DecoderResult decode = tokener.decode(matrix.getArray());
		Assert.assertEquals("test", decode.getText());
	}
}
