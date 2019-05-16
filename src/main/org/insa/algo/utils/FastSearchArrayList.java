package org.insa.algo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class FastSearchArrayList<E> extends ArrayList<E>
{
	private static final long serialVersionUID = 1L;
	
	private final Map<E, BiIndex> map = new HashMap<E, BiIndex>();
	
	public FastSearchArrayList()
	{
		
	}
	
	public FastSearchArrayList(List<E> list)
	{
		for(E e: list)
		{
			add(e);
		}
	}
	
	@Override
	public boolean add(E e)
	{
		if(e == null)
		{
			throw new NullPointerException();
		}
		mapPut(e, super.size());
		return super.add(e);
	}
	
	@Override
	public E set(int index, E e)
	{
		if(e == null)
		{
			throw new NullPointerException();
		}
		mapRemove(index);
		mapPut(e, index);
		return super.set(index, e);
	}
	
	@Override
	public E get(int index)
	{
		return super.get(index);
	}
	
	@Override
	public int indexOf(Object o)
	{
		BiIndex b = map.get(o);
		if(b != null)
		{
			return b.lowIndex;
		}
		return -1;
	}
	
	@Override
	public E remove(int index)
	{
		mapRemove(index);
		return super.remove(index);
	}
	
	private void mapPut(E e, int index)
	{
		BiIndex b = map.get(e);
		if(b == null)
		{
			map.put(e, new BiIndex(index));
		}
		else
		{
			b.put(index);
		}
	}
	
	private void mapRemove(int index)
	{
		E e = super.get(index);
		BiIndex b = map.get(e);
		b.remove(index);
		if(b.isEmpty())
		{
			map.remove(e);
		}
	}
	
	class BiIndex
	{
		int lowIndex;
		int highIndex;
		
		public BiIndex(int index)
		{
			lowIndex = index;
			highIndex = -1;
		}
		
		public void put(int index)
		{
			if(isFull() || isEmpty())
			{
				throw new UnsupportedOperationException();
			}
			if(index < lowIndex)
			{
				highIndex = lowIndex;
				lowIndex = index;
			}
			else if(index > lowIndex)
			{
				highIndex = index;
			}
			else
			{
				throw new UnsupportedOperationException();
			}
		}
		
		public void remove(int index)
		{
			if(index == highIndex)
			{
				highIndex = -1;
			}
			else if(index == lowIndex)
			{
				lowIndex = highIndex;
				highIndex = -1;
			}
			else
			{
				throw new NoSuchElementException();
			}
		}
		
		public boolean isEmpty()
		{
			return lowIndex == -1;
		}
		
		public boolean isFull()
		{
			return highIndex != -1;
		}
	}
}