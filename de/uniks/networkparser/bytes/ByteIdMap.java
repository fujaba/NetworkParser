package de.uniks.networkparser.bytes;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or later as soon they
 will be approved by the European Commission - subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import de.uniks.networkparser.AbstractMap;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.bytes.converter.ByteConverter;
import de.uniks.networkparser.bytes.converter.ByteConverterHTTP;
import de.uniks.networkparser.event.BasicMessage;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.event.UnknownMessage;
import de.uniks.networkparser.event.creator.BasicMessageCreator;
import de.uniks.networkparser.exceptions.TextParsingException;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.BufferedBytes;
import de.uniks.networkparser.interfaces.ByteCreator;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
/**
 * The Class ByteIdMap.
 */

public class ByteIdMap extends IdMap {
	
