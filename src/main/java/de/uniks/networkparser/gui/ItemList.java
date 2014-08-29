package de.uniks.networkparser.gui;
/*
NetworkParser
Copyright (c) 2011 - 2013, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
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

import java.util.List;

import de.uniks.networkparser.AbstractEntityList;
import de.uniks.networkparser.AbstractList;

public abstract class ItemList<E>  extends AbstractEntityList<E> implements List<E>{
	private boolean readonly = false;

	public boolean isReadonly() {
		return readonly;
	}

	@SuppressWarnings("unchecked")
   public <ST extends AbstractList<E>> ST withReadonly(boolean readonly) {
		this.readonly = readonly;
		return (ST)this;
	}

	// Add all
    @Override
	public E set(int index, E element) {
		if (readonly) {
			throw new UnsupportedOperationException("set(" +index+ ")");
		}
		return super.set(index, element);
    }

    @Override
	public void add(int index, E element) {
		if (readonly) {
			throw new UnsupportedOperationException("add(" +index+ ")");
		}
		super.add(index, element);
    }
   
    @Override
	public E remove(int index) {
    	if (readonly) {
			throw new UnsupportedOperationException("remove(" +index+ ")");
		}
		return super.remove(index);
    }
   
    @Override
   public boolean add(E newValue)
   {
       if (readonly) {
          throw new UnsupportedOperationException("add()");
       }
      return super.addEntity(newValue);
   }
}
