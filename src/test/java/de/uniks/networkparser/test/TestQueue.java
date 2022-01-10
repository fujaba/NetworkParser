package de.uniks.networkparser.test;

import java.util.concurrent.PriorityBlockingQueue;

import org.junit.Test;

public class TestQueue {

	@Test
	public void testqueue() 
	{
		PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>(20);
//		PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
		queue.put(5); //System.out.println(queue);
		queue.put(1);// System.out.println(queue);
//		queue.put(5); System.out.println(queue);
		queue.put(2);// System.out.println(queue);
		queue.put(3);// System.out.println(queue);
		queue.put(4);// System.out.println(queue);
		if(!queue.contains(2)) {
		    queue.put(2);
		}
		System.out.println(queue);
		queue.poll();
		
		System.out.println(queue);
//		queue.drainTo(polledElements);

//		assertThat(polledElements).containsExactly(1, 2, 3, 4, 5);
	}
}
