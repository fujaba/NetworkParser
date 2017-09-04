package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.xml.HTMLEntity;

public class StoryStepDiagram implements StoryStep {
	private GraphModel model;

	@Override
	public void finish() {
	}

	@Override
	public boolean dump(Story story, HTMLEntity element) {
		element.withGraph(this.model);
		return true;
	}

	public StoryStepDiagram withModel(GraphModel model) {
		this.model = model;
		return this;
	}
/*
 * public void addObjectDiagram(Object... elems)
   {
      String objectName;
      String objectIcon;
      Object object;
      Object root = null;
      LinkedHashSet<Object> explicitElems = new LinkedHashSet<Object>();
      boolean restrictToExplicitElems = false;

      // do we have a JsonIdMap?
      if (jsonIdMap == null)
      {
         // jsonIdMap = (IdMap) new GenericIdMap().withSessionId(null);
         jsonIdMap = (IdMap) new SDMLibIdMap("s").withSession(null).withTimeStamp(1);
         // FIXME TRY IF NESSESSARY jsonIdMap.getLogger().withError(false);
      }

      // go through all diagram elems
      int i = 0;

      while (i < elems.length)
      {
         objectName = null;
         objectIcon = null;
         object = null;

         while (i < elems.length && elems[i] instanceof String)
         {
            String txt = (String) elems[i];
            String suffix = CGUtil.shortClassName(txt);

            if (txt.indexOf('.') >= 0 && "png gif tif".indexOf(suffix) >= 0)
            {
               // it is a file name
               objectIcon = txt;
            }
            else
            {
               // name for an object
               objectName = (String) elems[i];
            }
            i++;
         }

         if (!(i < elems.length))
         {
            // ups no object for this name.
            break;
         }

         object = elems[i];
         i++;

         if (object == null)
         {
            continue;
         }

         if (object.equals(true))
         {
            restrictToExplicitElems = true;
            continue;
         }

         if (object.getClass().isPrimitive())
         {
            // not an object
            continue;
         }

         if (object instanceof Collection)
         {
            explicitElems.addAll((Collection<?>) object);

            Collection<?> coll = (Collection<?>) object;
            if (!coll.isEmpty())
            {
               object = coll.iterator().next();
            }
            else
            {
               continue;
            }
         }
         else
         {
            // plain object
            explicitElems.add(object);
         }

         if (root == null)
         {
            root = object;
         }

         // add to jsonIdMap
         if (objectName != null)
         {
            jsonIdMap.put(objectName, object);
         }
         else
         {
            objectName = jsonIdMap.getId(object);
         }

         if (objectIcon != null)
         {
            iconMap.put(objectName, objectIcon);
         }

      }

      // all names collected, dump it
      if (restrictToExplicitElems)
      {
         RestrictToFilter jsonFilter = new RestrictToFilter(explicitElems);
         addObjectDiagram(jsonIdMap, explicitElems, jsonFilter);
      }
      else
      {
         AlwaysTrueCondition conditionMap = new AlwaysTrueCondition();
         addObjectDiagram(jsonIdMap, explicitElems, conditionMap);
      }
   }
 */
}
