package de.uniks.networkparser.test.model.util;

import java.util.Collection;
import java.util.Iterator;

import de.uniks.networkparser.gui.ItemList;


public class SDMSetBase<T> extends ItemList<T>
{
   @Override
   protected boolean initAllowDuplicate()
   {
      return false;
   }
   
//   @Override
//   public String toString()
//   {
//      StringList stringList = new StringList();
//      
//      for (T elem : this)
//      {
//         stringList.add(elem.toString());
//      }
//      
//      return "(" + stringList.concat(", ") + ")";
//   }
   
//   public <ST extends SDMSetBase<T>> ST instanceOf(ST target)
//   {
//      String className = target.getClass().getName();
//      className = CGUtil.baseClassName(className, "Set");
//      try
//      {
//         Class<?> targetClass = target.getClass().getClassLoader().loadClass(className);
//         for (T elem : this)
//         {
//            if (targetClass.isAssignableFrom(elem.getClass()))
//            {
//               target.add(elem);
//            }
//         }
//      }
//      catch (ClassNotFoundException e)
//      {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      }
//      return target;
//   }
//   
   public <ST extends SDMSetBase<T>> ST union(ST other)
   {
      @SuppressWarnings("unchecked")
      ST result = (ST) this.getNewInstance();
      result.addAll(other);
      
      return result;
   }
   
   
   public <ST extends SDMSetBase<T>> ST intersection(ST other)
   {
      @SuppressWarnings("unchecked")
      ST result = (ST) this.getNewInstance();
      result.retainAll(other);
      return result;
   }
   
   
   @SuppressWarnings("unchecked")
   public <ST extends SDMSetBase<T>> ST minus(Object other)
   {
      ST result = (ST) this.getNewInstance();
      result.addAll(this);
      
      if (other instanceof Collection)
      {
         result.removeAll((Collection<?>) other);
      }
      else
      {
         result.remove(other);
      }
      
      return result;
   }

   @SuppressWarnings("unchecked")
   public <ST extends SDMSetBase<T>> ST has(Condition condition)
   {
      ST result = (ST) this.getNewInstance();
      
      for (T elem : this)
      {
         if ( ! condition.check(elem))
         {
            result.remove(elem);
         }
      };
      return result;
   }
   
//   @Override
//   public AbstractList<T> clone() {
//      return this.getNewInstance().with(this);
//   }
   
   public Iterator<T> cloneIterator() {
      return super.clone().iterator();
   }
   
   public abstract class Condition
   {
      public abstract boolean check(T elem);
   }
   
   @SuppressWarnings("unchecked")
@Override
   public SDMSetBase<T> getNewInstance() 
   {
      SDMSetBase<T> result = null;
      try
      {
         result = this.getClass().newInstance();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   @Override
   public SDMSetBase<T> with(Object... values) {
      for(Object item : values){
         this.add((T) item);
      }
      return this;
   }

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object value) {
		return removeItemByObject((T)value) >= 0;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}
}
