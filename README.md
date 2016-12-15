NetworkParser
=============

Framework for serialization from Java objects to Json, XML and Byte.


NetworkParser is a simple framework for serializing complex model structures. 
To do that it transforms a given model to an intermediate model which can be serialized. It also offers lots of filters.

For serialization you are three formats available: Json, XML and Byte. 
For deserialization you can use following formats: Json, XML, Byte and EMF.

The Framework have many other features like:
- Calculator
- Date with holidays
- UML-Layouting with Javascript or Webservice like YUML
- JavaFX Container Classes:
  - for DataBinding
  - Table with Searchfield
  - Form
  - PopupDialog
  - Basic Shell-Class with Writing Errorfiles
- Logicstructure
- SimpleList as universal solution for datamodels

## Current Status ##
- Master
<!--  - Jenkins: [![Build Status](https://se.cs.uni-kassel.de/jenkins/job/NetworkParser/badge/icon)](https://se.cs.uni-kassel.de/jenkins/Networkparser/)-->
  - travis-ci: [![Build Status](https://travis-ci.org/fujaba/NetworkParser.svg?branch=master)](https://travis-ci.org/fujaba/NetworkParser)
<!--  - Maven: [![Maven Status](http://se.cs.uni-kassel.de/maven/icon?project=NetworkParser)](http://se.cs.uni-kassel.de/maven/de/uniks/NetworkParser/latest/NetworkParser.jar)-->
  - Coverage: [![Coverage Status](https://coveralls.io/repos/fujaba/NetworkParser/badge.svg?branch=master&service=github)](https://coveralls.io/github/fujaba/NetworkParser?branch=master)
  - Coverity Scan [![Coverity Status](https://scan.coverity.com/projects/8708/badge.svg)](https://scan.coverity.com/projects/fujaba-networkparser)
  - CII Best Practices [![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/561/badge)](https://bestpractices.coreinfrastructure.org/projects/561)
  - Codacy [![Codacy Badge](https://api.codacy.com/project/badge/Grade/03b590f35f334375b890f4261bf80bea)](https://www.codacy.com/app/stefan_7/NetworkParser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fujaba/NetworkParser&amp;utm_campaign=Badge_Grade)
  - Code Climate [![Code Climate](https://codeclimate.com/github/fujaba/NetworkParser/badges/gpa.svg)](https://codeclimate.com/github/fujaba/NetworkParser)
- Develop
  - travis-ci: [![Build Status](https://travis-ci.org/fujaba/NetworkParser.svg?branch=develop)](https://travis-ci.org/fujaba/NetworkParser)
<!--  - Maven: [![Maven](http://se.cs.uni-kassel.de/maven/icon?project=NetworkParser&type=snaphots)](http://se.cs.uni-kassel.de/maven/de/uniks/NetworkParser/latest-SNAPSHOT/NetworkParser-SNAPSHOT.jar)-->
  - Coverage: [![Coverage Status](https://coveralls.io/repos/fujaba/NetworkParser/badge.svg?branch=develop&service=github)](https://coveralls.io/github/fujaba/NetworkParser?branch=develop)
  
[![Open Hub](https://www.openhub.net/p/NetworkParser/widgets/project_partner_badge?format=gif&ref=Partner+Badge "Open Hub")](https://www.openhub.net/p/NetworkParser/)

# Getting Started

## Installation
$ git clone https://github.com/fujaba/NetworkParser.git

#Maven artifacts
Maven artifacts are available at:
- http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22NetworkParser%22 - release repository
- https://oss.sonatype.org/content/repositories/snapshots/com/github/fujaba/NetworkParser/ - snaphots repository

#Usage
Here are a simple Usage of JsonIdMap for serialization and deserialization and get UpdateMessages
```java
	House house=new House();
	house.setFloor(4);
	house.setName("University");
	IdMap map=new IdMap().withCreator(new HouseCreator());
	map.withUpdateListenerSend(new UpdateListener() {
		@Override
		public boolean update(String typ, BaseItem source, Object target, String property, Object oldValue,
				Object newValue) {
			System.out.println(source);
			return false;
		}
	});
	
	JsonObject json = map.toJsonObject(house);
	String string=json.toString();
	
	IdMap decodeMap=new IdMap().withCreator(new HouseCreator());
	House newHouse = (House) decodeMap.decode(string);

	house.setFloor(42);
```
## Maven Snapshot
### pom.xml
```xml
<dependency>
	<groupId>de.uniks</groupId>
	<artifactId>NetworkParser</artifactId>
	<version>4.2.*</version>
</dependency>

<repositories>
	<repository>
		<releases><enabled>false</enabled></releases>
		<snapshots><enabled>true</enabled></snapshots>
		<id>Sonatype Snapshots</id>
		<name>Sonatype Snapshots</name>
		<url>https://oss.sonatype.org/content/repositories/snapshots</url>
	</repository>
</repositories>
```
#Building Jar
| Gradle Command | Description |
|:--:|:--:|
| task | Show task to run |
| clean | Deletes the build directory. |
| buildAll | Build All Jars |
| buildCoreJar | Build Jar with NetworkParser-Core without dependency of JavaFX and Reflection |
| buildFullJar | Build FullJar with Class-Files, Source-Files and JavaDoc |
| buildJavadoc | Build JavaDoc Jar |
| buildSourceJar | Build Jar with class-Files and Source-Files |
| jar | Assembles a jar archive containing the main classes.|

## Links
- [SimpleJsonTest](src/test/java/de/uniks/networkparser/test/SimpleJsonTest.java "Sourcecode SimpleJsonTest.java")
- [House](src/test/java/de/uniks/networkparser/test/model/House.java "Sourcecode House.java")
- [HouseCreator](src/test/java/de/uniks/networkparser/test/model/util/HouseCreator.java "Sourcecode HouseCreator.java")
- The issue list: Head straight to https://github.com/fujaba/NetworkParser/issues for a list of all issues or click `Issues` in the navigation bar on the right.
- See also on Openhub https://www.openhub.net/p/NetworkParser

## Spenden
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=FSHD59SQ8PR2Y)

# License
NetworkParser is released under an [The MIT License](src/main/resources/Licence.txt).
