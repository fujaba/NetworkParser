package de.uniks.jism.bytes;
/*
Copyright (c) 2012, Stefan Lindel
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
   must display the following acknowledgement:
   This product includes software developed by Stefan Lindel.
4. Neither the name of contributors may be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE 'Json Id Serialisierung Map' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.nio.ByteBuffer;

public class ByteUtil {
	public static String convertHexString(ByteBuffer value){
		return convertHexString(value.array(), value.limit());
	}
	
	public static String convertHexString(byte[] values, int size){
		String hexVal = "0123456789ABCDEF";

		StringBuilder returnValue = new StringBuilder(size*2);
		if(values!=null){
			for (int i = 0; i < size; i++) {
				int value=values[i];
				if(value<0){
					value+=256;
				}
				returnValue.append(""+hexVal.charAt(value/16)+hexVal.charAt(value%16));
			}
		}
		return returnValue.toString();
	}
	
	public static String convertString(ByteBuffer byteBuffer){
		StringBuilder returnValue=new StringBuilder();
		if(byteBuffer!=null){
			for (int i = 0; i < byteBuffer.limit(); i++) {
				byte value = byteBuffer.get(i);
				if (value <= 32 || value == 127) {
					returnValue.append(ByteIdMap.SPLITTER);
					returnValue.append((char) (value + ByteIdMap.SPLITTER + 1));
				} else {
					returnValue.append((char) value);
				}
			}
		}
		return returnValue.toString();
	}
	
	/**
	 * Gets the typ.
	 *
	 * @param group the group
	 * @param subgroup the subgroup
	 * @return the typ
	 */
	public static byte getTyp(byte group, byte subgroup){
		byte returnValue=(byte) ((group/16)*16);
		return (byte) (returnValue+(subgroup%16));
	}
	
	public static byte getTyp(byte group, int len){
		if (len > 32767) {
			return getTyp(group, ByteIdMap.DATATYPE_STRINGBIG);
		} else if (len > 250) {
			return getTyp(group, ByteIdMap.DATATYPE_STRINGMID);
		} else if(len>ByteIdMap.SPLITTER){
			return getTyp(group, ByteIdMap.DATATYPE_STRING);
		}else{
			return getTyp(group, ByteIdMap.DATATYPE_STRINGSHORT);
		}
	}
	public static int getTypLen(byte typ){
		if(isGroup(typ)){
			int ref=typ%16;
			if(ref==ByteIdMap.DATATYPE_STRINGSHORT%16){
				return 1;
			}else if(ref==ByteIdMap.DATATYPE_STRING%16){
				return 1;
			}else if(ref==ByteIdMap.DATATYPE_STRINGMID%16){
				return 2;
			}else if(ref==ByteIdMap.DATATYPE_STRINGBIG%16){
				return 4;
			}else if(ref==ByteIdMap.DATATYPE_STRINGLAST%16){
				return 0;
			}
		}else if(typ==ByteIdMap.DATATYPE_CLAZZ){
			return 1;
		}
		return 0;
	}
	public static ByteBuffer getBuffer(int len, byte typ){
		return getBuffer(len, typ, true);
	}
	public static ByteBuffer getBuffer(int len, byte typ, boolean isGroupable){
		if(len<1){
			return null;
		}
		ByteBuffer message;
		if(typ==ByteIdMap.DATATYPE_CLAZZ){
			message = ByteBuffer.allocate(len);
			message.put(typ);
			message.put((byte)(ByteIdMap.SPLITTER+len-2));
			
		}else{
			int lenGroup=getTypLen(typ);
			int lenTyp=0;
			if(!isGroupable){
				lenGroup=0;
				lenTyp=1;
			}
			if(lenGroup>0){
				message=ByteBuffer.allocate(len);
				int lenValue=len-ByteEntity.TYPBYTE-lenGroup;
				byte typGroup = getTyp(typ, lenValue);
				message.put(typGroup);
	
				if(typGroup%16==ByteIdMap.DATATYPE_STRINGSHORT%16){
					message.put((byte)(ByteIdMap.SPLITTER+lenValue));
				}else if(typGroup%16==ByteIdMap.DATATYPE_STRING%16){
					message.put((byte)lenValue);
				}else if(typGroup%16==ByteIdMap.DATATYPE_STRINGMID%16){
					message.putShort((short)lenValue);
				}else if(typGroup%16==ByteIdMap.DATATYPE_STRINGBIG%16){
					message.putInt(lenValue);
				}else if(typGroup%16==ByteIdMap.DATATYPE_STRINGLAST%16){
					// Nothing to ADD
				}
			}else if(typ>0){
				message = ByteBuffer.allocate(len+lenTyp);
				message.put(typ);
			}else{
				message = ByteBuffer.allocate(len);
			}
		}
		return message;
	}
	
	/**
	 * CHeck if the Typ is typ of Group
	 *
	 * @param typ the the typ of data
	 * @return the boolean
	 */
	public static boolean isGroup(byte typ){
		int ref=(typ/16);
		if(ref==(ByteIdMap.DATATYPE_BYTEARRAY/16)){
			return true;
		}else if(ref==(ByteIdMap.DATATYPE_STRING/16)){
			return true;
		}else if(ref==(ByteIdMap.DATATYPE_LIST/16)){
			return true;
		}else if(ref==(ByteIdMap.DATATYPE_MAP/16)){
			return true;
		}else if(ref==(ByteIdMap.DATATYPE_CHECK/16)){
			return true;
		}
		return false;
	}
}
