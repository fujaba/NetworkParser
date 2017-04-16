package test;

import org.junit.Test;

import de.uniks.factory.java.JavaModelFactory;
import de.uniks.factory.typescript.TypeScriptModelFactory;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.template.generator.ModelGenerator;

public class TestAttributeFactory {

//	@Test
//	public void testAttributeFactory() {
//		AttributeFactory factory = new AttributeFactory();
//		Attribute attribute = new Attribute("age", DataType.INT);
//		System.out.println(factory.create(attribute, false));
//	}
	
	@Test
	public void testGraphModelAttributes() {
//		long startTime = System.nanoTime();
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		JavaModelFactory modelFactory = new JavaModelFactory();
//		for(int i = 0; i < 1000; i++) {
//			modelFactory.create(classModel);
//		}
//		long run = (System.nanoTime() - startTime);
//		System.out.println("Duration:" +run );
//		return run;
		System.out.println(modelFactory.create(classModel));
	}
	
	@Test
	public void testTypeScriptAttribute() {
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		Clazz student = classModel.createClazz("Student");
		student.createAttribute("type", DataType.STRING);
		student.createAttribute("person", DataType.create(person));
		TypeScriptModelFactory modelFactory = new TypeScriptModelFactory();
		System.out.println(modelFactory.create(classModel));
	}
	
//	@Test
//	public void testRunTen() {
//		long result =0;
//		for(int i=0;i<30;i++) {
//			result += testGraphModelAttributes(); 
//		}
//		System.out.println("Duration:" +result/30 );
//	}

	@Test
	public void testCodestyleAttributes() {
		GraphList classModel = new GraphList();
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		JavaModelFactory modelFactory = new JavaModelFactory();
		modelFactory.setCodeStyle(ModelGenerator.CODESTYLE_DIVIDED);
		System.out.println(modelFactory.create(classModel));
	}
	
    @Test
    public void muster_ha1() {
    	GraphList clazzModel = new GraphList().with("de.uniks.pm.game.model");


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
    			.withMethod("attackZombie", DataType.VOID, new Parameter(DataType.create("de.uniks.pm.game.model.Zombie")), new Parameter(DataType.create("de.uniks.pm.game.model.Zombie")))
    			.withMethod("catchZombie", DataType.VOID, new Parameter(DataType.create("de.uniks.pm.game.model.Trap")), new Parameter(DataType.create("de.uniks.pm.game.model.Zombie")))
    			.withMethod("movePlayer", DataType.VOID, new Parameter(DataType.create("de.uniks.pm.game.model.Ground")));

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
//    	javaModelFactory.create(clazzModel);
    	System.out.println(javaModelFactory.create(clazzModel));
    }
	
}
