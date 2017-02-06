package de.uniks.pm.game.model;

import de.uniks.pm.game.model.Ground;

public class Rock extends Ground
{

   public void removeYou()
   {
      withoutZombies(this.getZombies().toArray(new Zombie[this.getZombies().size()]));
      withoutTrainers(this.getTrainers().toArray(new Trainer[this.getTrainers().size()]));
      firePropertyChange("REMOVE_YOU", this, null);
   }

}