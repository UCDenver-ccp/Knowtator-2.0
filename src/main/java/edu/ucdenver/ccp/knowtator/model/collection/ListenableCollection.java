/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorObjectInterface;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class ListenableCollection<K extends KnowtatorDataObjectInterface, C extends Collection<K>, L extends CollectionListener<K>> implements Iterable<K>, KnowtatorObjectInterface {
	final C collection;
	final List<L> collectionListeners;

	ListenableCollection(C collection) {
		this.collection = collection;
		collectionListeners = new ArrayList<>();
	}

	public void add(K objectToAdd) {
		collection.add(objectToAdd);

		collectionListeners.forEach(CollectionListener::added);
		if (collection.size() == 1) {
			collectionListeners.forEach(CollectionListener::firstAdded);
		}
	}

	void remove(K objectToRemove) {
		collection.remove(objectToRemove);

		collectionListeners.forEach(CollectionListener::removed);
		if (collection.size() == 0) {
			collectionListeners.forEach(CollectionListener::emptied);
		}
	}

	public K get(String id) {
		for (K obj : collection) {
			if (obj.getId().equals(id)) {
				return obj;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	public boolean contains(K objToFind) {
		return containsID(objToFind.getId());
	}

	public boolean containsID(String idToFind) {
		for (K id : collection) {
			if (id.getId().equals(idToFind)) {
				return true;
			}
		}
		return false;
	}

	public void addCollectionListener(L listener) {
		if (!collectionListeners.contains(listener)) collectionListeners.add(listener);
	}

	void removeCollectionListener(L listener) {
		collectionListeners.remove(listener);
	}

	@Override
	@Nonnull
	public Iterator<K> iterator() {
		return collection.iterator();
	}

	public Stream<K> stream() {
		return collection.stream();
	}

	public int size() {
		return collection.size();
	}

	@Override
	public void dispose() {
		collectionListeners.clear();
		forEach(KnowtatorDataObjectInterface::dispose);
		collection.clear();
	}

	public C getCollection() {
		return collection;
	}
}
