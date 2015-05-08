package de.uniks.networkparser.emf;

public class EMFUtil {
	public static final String emfTypes = " EOBJECT EBIG_DECIMAL EBOOLEAN EBYTE EBYTE_ARRAY ECHAR EDATE EDOUBLE EFLOAT EINT EINTEGER ELONG EMAP ERESOURCE ESHORT ESTRING ";

	public static boolean isEMFType(String tag) {
		return emfTypes.indexOf(" " + tag.toUpperCase() + " ") >= 0;
	}

	public static boolean isPrimitiveType(String type) {
		String primitiveTypes = " String long Long int Integer char Char boolean Boolean byte Byte float Float double Double java.util.Date ";

		if (type == null)
			return false;

		return primitiveTypes.indexOf(" " + type + " ") >= 0;
	}

	public static final String javaKeyWords = " abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for if goto implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while ";

	public static String toValidJavaId(String tag) {
		if (javaKeyWords.indexOf(" " + tag + " ") >= 0) {
			tag = "_" + tag;
		}

		return tag;
	}

	public static String getId(String name) {
		if (name.indexOf("#") > 0) {
			return name.substring(name.indexOf("#") + 2);
		}
		return name;
	}

	public static String shortClassName(String name) {
		int pos = name.lastIndexOf('.');
		name = name.substring(pos + 1);
		pos = name.lastIndexOf('$');
		if (pos >= 0) {
			name = name.substring(pos + 1);
		}
		return name;
	}
	
	public static String upFirstChar(String name) {
		if(name == null || name.length()<1) {
			return name;
		}
		return name.substring(0, 1).toUpperCase()+name.substring(1);
	}
}
