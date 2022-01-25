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
- GitLab CI
  - Coveralls [![Coverage Status](https://coveralls.io/repos/gitlab/StefanLindel/NetworkParser/badge.svg?branch=feature/gitlab)](https://coveralls.io/gitlab/StefanLindel/NetworkParser?branch=feature/gitlab)
  - SonarCloud [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=StefanLindel_NetworkParser&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=StefanLindel_NetworkParser)
- 
- Master
  - travis-ci: [![Build Status](https://travis-ci.org/fujaba/NetworkParser.svg?branch=master)](https://travis-ci.org/fujaba/NetworkParser)
<!--  - Maven: [![Maven Status](http://se.cs.uni-kassel.de/maven/icon?project=NetworkParser)](http://se.cs.uni-kassel.de/maven/de/uniks/NetworkParser/latest/NetworkParser.jar)-->
  - Coverage: [![Coverage Status](https://coveralls.io/repos/fujaba/NetworkParser/badge.svg?branch=master&service=github)](https://coveralls.io/github/fujaba/NetworkParser?branch=master)
  - CII Best Practices [![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/561/badge)](https://bestpractices.coreinfrastructure.org/projects/561)
  - Codacy [![Codacy Badge](https://api.codacy.com/project/badge/Grade/03b590f35f334375b890f4261bf80bea)](https://www.codacy.com/app/stefan_7/NetworkParser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fujaba/NetworkParser&amp;utm_campaign=Badge_Grade)
  - Code Climate [![Code Climate](https://codeclimate.com/github/fujaba/NetworkParser/badges/gpa.svg)](https://codeclimate.com/github/fujaba/NetworkParser)
  - Coverity Scan: [![Coverity Status](https://scan.coverity.com/projects/8708/badge.svg)](https://scan.coverity.com/projects/fujaba-networkparser)
  - Glitter-Chat: [![Join the chat at https://gitter.im/NetworkParser/Lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/NetworkParser/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
  - Java-Documentation: [![Javadocs](http://javadoc.io/badge/de.uniks/NetworkParser.svg)](http://javadoc.io/doc/de.uniks/NetworkParser)
  - Maven Central: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.uniks/NetworkParser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.uniks/NetworkParser)

- Develop
  - travis-ci: [![Build Status](https://travis-ci.org/fujaba/NetworkParser.svg?branch=develop)](https://travis-ci.org/fujaba/NetworkParser)
<!--  - Maven: [![Maven](http://se.cs.uni-kassel.de/maven/icon?project=NetworkParser&type=snaphots)](http://se.cs.uni-kassel.de/maven/de/uniks/NetworkParser/latest-SNAPSHOT/NetworkParser-SNAPSHOT.jar)-->
  - Coverage: [![Coverage Status](https://coveralls.io/repos/fujaba/NetworkParser/badge.svg?branch=develop&service=github)](https://coveralls.io/github/fujaba/NetworkParser?branch=develop)
  
[![Open Hub](https://www.openhub.net/p/NetworkParser/widgets/project_partner_badge?format=gif&ref=Partner+Badge "Open Hub")](https://www.openhub.net/p/NetworkParser/)

[![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_small.png)](http://www.ej-technologies.com/products/jprofiler/overview.html) optimized

[![Open Source Love](https://badges.frapsoft.com/os/v2/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)

[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/fujaba/networkparser.svg)](http://isitmaintained.com/project/fujaba/networkparser "Average time to resolve an issue")

[![Percentage of issues still open](http://isitmaintained.com/badge/open/fujaba/networkparser.svg)](http://isitmaintained.com/project/fujaba/networkparser "Percentage of issues still open")

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=NetworkParser&metric=alert_status)](https://sonarcloud.io/dashboard?id=NetworkParser) 

Project Managment

[![Join the chat at https://gitter.im/NetworkParser/Lobby](https://badges.gitter.im/NetworkParser/Lobby.svg)](https://gitter.im/NetworkParser/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Download latest Release](https://img.shields.io/github/downloads/fujaba/NetworkParser/total.svg)](../../releases)


# Getting Started

# Gradle

<pre>
<!-- insert_code_fragment: gradle.repositories -->
repositories {
    mavenCentral()
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}
<!-- end_code_fragment: -->
</pre>
<pre>
<!-- insert_code_fragment: gradle.dependencies -->
dependencies {
	compile group: "de.uniks", name: "NetworkParser", version: "latest.integration", classifier:"sources18", changing: true
}
<!-- end_code_fragment: -->
</pre>

## Installation
$ git clone https://github.com/fujaba/NetworkParser.git

# Maven artifacts
Maven artifacts are available at:
- http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22NetworkParser%22 - release repository
- https://oss.sonatype.org/content/repositories/snapshots/com/github/fujaba/NetworkParser/ - snaphots repository

# Usage
Simple Example with ClassModelBuilder for build a small class model:
```
ClassModelBuilder mb = new ClassModelBuilder("de.uniks.studyright");
Clazz uni = mb.buildClass("University").withAttribute("name", DataType.STRING);
Clazz student = mb.buildClass("Student").withAttribute("matNo", DataType.INT);

uni.withAssoc(student, "students", Association.MANY, "uni", Association.ONE);
Clazz room = mb.buildClass("Room")
    .withAttribute("roomNo", DataType.STRING);
uni.withAssoc(room, "rooms", Association.MANY, "uni", Association.ONE);
ClassModel model = mb.build();
```
Simple Example with Old ClassModel for build a small class model:
```
ClassModel model = new ClassModel("de.uniks.studyright");
Clazz uni = model.createClazz("University").withAttribute("name", DataType.STRING);
Clazz student = model.createClazz("Student").withAttribute("matNo", DataType.INT);
        
uni.withAssoc(student, "students", Association.MANY, "uni", Association.ONE);
Clazz room = model.createClazz("Room").withAttribute("roomNo", DataType.STRING);
uni.withAssoc(room, "rooms", Association.MANY, "uni", Association.ONE);
model.generate();
```

![simple class diagram](doc/SimpleClassDiagram.png)

<!---
Here are a simple Usage of IdMap for serialization and deserialization
{{md  '..src/test/java/de/uniks/networkparser/test/SimpleUsage.java[tag=serialization]'}} 
-->

- [serialization](example.adoc "simple Serialization")

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
# Building Jar
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
NetworkParser is released under an [The MIT License](src/main/resources/Licence.txt). [![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)
