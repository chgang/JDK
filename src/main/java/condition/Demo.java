package condition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo {
	
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	
	private class T implements Runnable {
		@Override
		public void run() {
			try {
				lock.lock();
				try {
					System.out.println("开始等待");
					condition.await();
					System.out.println(Thread.currentThread().isInterrupted());
				} finally {
					lock.unlock();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void signal() {
		lock.lock();
		try {
			System.out.println("获得锁");
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
//		Demo demo = new Demo();
//		new Thread(demo.new T()).start();
//		Thread.sleep(2000);
//		demo.signal();
//		================================================================================================================
//		================================================================================================================

		Lock lock = new ReentrantLock();
		Condition conditionA = lock.newCondition();
		Condition conditionB = lock.newCondition();
		Condition conditionC = lock.newCondition();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lock.lock();
					for (;;) {
						conditionC.await();
						System.out.println("****** C *******");
						conditionC.signal();
						TimeUnit.SECONDS.sleep(2);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lock.lock();
					for (;;) {
						conditionC.await();
						System.out.println("****** B *******");
						conditionC.signal();
						TimeUnit.SECONDS.sleep(2);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lock.lock();
					for (;;) {
						System.out.println("****** A *******");
						conditionC.signal();
						TimeUnit.SECONDS.sleep(2);
						conditionC.await();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}).start();


//		================================================================================================================
//		================================================================================================================
//		Thread t1 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				for (int i = 0; i < 15; i++) {
//					try {
//						consumer();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		t1.start();
//		Thread t2 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				for (int i = 1; i < 16; i++) {
//					try {
//						product(i);
//						TimeUnit.SECONDS.sleep(2);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		t2.start();
//
//		t1.join();
//		t2.join();
//		System.out.println("执行结束 >>>>>>>>>>>>>>>");
	}

	private static Object object = new Object();
	public static int conCount = 0;
	public static int capacity = 6;
	private static List<Integer> list = new ArrayList<>(capacity);
	public static void consumer() throws InterruptedException {
		while (list.isEmpty()) {
			synchronized (object) {
				object.wait();
			}
		}
		synchronized (object) {
			System.out.println("移除元素：" + list.remove(conCount));
			object.notify();
		}
	}

	public static void product(int num) throws InterruptedException {
		while (list.size() == (capacity - 1)) {
			synchronized (object) {
				object.wait();
			}
		}
		synchronized (object) {
			System.out.println("添加元素：" + num);
			list.add(num);
			object.notify();
			TimeUnit.SECONDS.sleep(5);
			System.out.println("释放锁 >>>>>>>>>>>>>>>>");
		}
	}

}
