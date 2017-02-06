package de.uniks.pm.game.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.pm.game.model.util.DiceCreator;
import de.uniks.pm.game.model.util.GameCreator;
import de.uniks.pm.game.model.util.GrassCreator;
import de.uniks.pm.game.model.util.GroundCreator;
import de.uniks.pm.game.model.util.RockCreator;
import de.uniks.pm.game.model.util.TrainerCreator;
import de.uniks.pm.game.model.util.TrapCreator;
import de.uniks.pm.game.model.util.ZombieCreator;
import de.uniks.pm.game.model.util.ZombieOwnerCreator;

class CreatorCreator
{

   public static IdMap createIdMap(String sessionID)
   {

      IdMap idMap = new IdMap().withSessionId(sessionID);

      idMap.with(new DiceCreator());
      idMap.with(new GameCreator());
      idMap.with(new GrassCreator());
      idMap.with(new GroundCreator());
      idMap.with(new RockCreator());
      idMap.with(new TrainerCreator());
      idMap.with(new TrapCreator());
      idMap.with(new ZombieCreator());
      idMap.with(new ZombieOwnerCreator());

      return idMap;

   }
   
}