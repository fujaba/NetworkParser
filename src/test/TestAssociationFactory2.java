package test;

import java.io.IOException;

import org.junit.Test;

import de.uniks.factory.java.JavaModelFactory;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.Parameter;

public class TestAssociationFactory2 {

	@Test
	public void testBidirectionalAssociation() throws IOException {
		GraphList classModel = new GraphList().with("de.uniks.model");
		Clazz person = classModel.createClazz("Person");
		Clazz room = classModel.createClazz("Room");
		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		javaModelFactory.generate("src", classModel);
	}	
	
	@Test
    public void muster_ha1() throws IOException {
    	GraphList clazzModel = new GraphList().with("de.uniks.test.pm.game.model");

    	Clazz diceClass = clazzModel.createClazz("Dice")
    			.with(new Attribute("value", DataType.create("int")) );

    	Clazz gameClass = clazzModel.createClazz("Game")
    			.with(new Attribute("actionPoints", DataType.create("int")) )
    			.withMethod("checkEnd", DataType.BOOLEAN);

    	gameClass.withBidirectional(diceClass, "dice", Cardinality.ONE, "game", Cardinality.ONE);

    	Clazz groundClass = clazzModel.createClazz("Ground")
    			.with(new Attribute("x", DataType.create("int")) )
    			.with(new Attribute("y", DataType.create("int")) );

    	Clazz grassClass = clazzModel.createClazz("Grass")
    			.withSuperClazz(groundClass);

    	Clazz rockClass = clazzModel.createClazz("Rock")
    			.withSuperClazz(groundClass);

    	Clazz trainerClass = clazzModel.createClazz("Trainer")
    			.with(new Attribute("color", DataType.create("String")) )
    			.with(new Attribute("experience", DataType.create("int")) )
    			.with(new Attribute("name", DataType.create("String")) )
    			.withMethod("attackZombie", DataType.VOID, new Parameter(DataType.create("de.uniks.test.pm.game.model.Zombie")), new Parameter(DataType.create("de.uniks.test.pm.game.model.Zombie")))
    			.withMethod("catchZombie", DataType.VOID, new Parameter(DataType.create("de.uniks.test.pm.game.model.Trap")), new Parameter(DataType.create("de.uniks.test.pm.game.model.Zombie")))
    			.withMethod("movePlayer", DataType.VOID, new Parameter(DataType.create("de.uniks.test.pm.game.model.Ground")));

    	trainerClass.withBidirectional(groundClass, "ground", Cardinality.MANY, "trainers", Cardinality.MANY);

    	trainerClass.withBidirectional(trainerClass, "next", Cardinality.ONE, "prev", Cardinality.ONE);

    	Clazz trapClass = clazzModel.createClazz("Trap")
    			.with(new Attribute("successRate", DataType.create("int")) );

    	Clazz zombieClass = clazzModel.createClazz("Zombie")
    			.with(new Attribute("ap", DataType.create("int")) )
    			.with(new Attribute("hp", DataType.create("int")) )
    			.with(new Attribute("name", DataType.create("String")) );

    	Clazz zombieOwnerClass = clazzModel.createClazz("ZombieOwner")
    			.enableInterface();

    	zombieOwnerClass.withKidClazzes(trainerClass, groundClass);

    	zombieClass.withBidirectional(zombieOwnerClass, "zombieOwner", Cardinality.ONE, "zombies", Cardinality.MANY);
    	
    	JavaModelFactory javaModelFactory = new JavaModelFactory();
    	javaModelFactory.generate("src", clazzModel);
    }
	
}