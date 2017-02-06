package de.uniks.pm.game.model;

import de.uniks.pm.game.model.Zombie;
import de.uniks.pm.game.model.util.ZombieSet;

public interface ZombieOwner
{

   public static final String PROPERTY_ZOMBIES = "zombies";

   public ZombieSet getZombies();

   public ZombieOwner withZombies(Zombie... value);

   public ZombieOwner withoutZombies(Zombie... value);

   public Zombie createZombies();

}