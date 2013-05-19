
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zofia Sobocinska
 */
public class Chain {

	/**
	 * 
	 * @param a
	 * @param b
	 * @param fun 
	 */
	public Chain(double a, double b, Fun fun) {

		Chunk.configure(a, b, fun);

	}

	/**
	 * 
	 * @return 
	 */
	public double integral() {

		return Chunk.integral();
	}

	/**
	 * 
	 */
	private static class Chunk extends Thread {

		private static ArrayList<Chunk> chunks;
		private static final long MAX_TIME = 10 * 1000;
		private static double max;
		private static Fun fun;
		private static final int THREADS_COUNT = 8;
		private static final int THREAD_ITERATIONS = 1000000;
		private static double area;
		private static final Hits HITS = new Hits(0);
		private int iterations;
		private double a;
		private double b;
		
		/**
		 * 
		 * @param a
		 * @param b 
		 */
		private Chunk(double a, double b) {
			this.a = a;
			this.b = b;
			this.iterations = THREAD_ITERATIONS;
		}

		/**
		 * 
		 * @param a
		 * @param b
		 * @param _fun 
		 */
		public static void configure(double a, double b, Fun _fun) {
			fun = _fun;

			max = fun.max(a, b);

			area = area(a, b);

			chunks = new ArrayList<>();

			double interval = (b - a) / THREADS_COUNT;

			for (int i = 0; i < THREADS_COUNT; i++) {
				chunks.add(new Chunk(i * interval, (i + 1) * interval));
			}
		}

		/**
		 * 
		 * @return 
		 */
		public static double integral() {

			Iterator<Chunk> i = chunks.iterator();
			while (i.hasNext()) {
				i.next().start();
			}
			try {
				for (Chunk chunk : chunks) {
					chunk.join();
				}
				return Chunk.getResult();
			} catch (InterruptedException ex) {
				Logger.getLogger(Chain.class.getName()).log(Level.SEVERE, null, ex);
			}
			return 0;
		}

		/**
		 * 
		 * @return 
		 */
		public static double getResult() {
			return (new Double(HITS.get()) / new Double(THREADS_COUNT * THREAD_ITERATIONS)) * area;
		}

		/**
		 * 
		 * @param a
		 * @param b
		 * @return 
		 */
		private static double area(double a, double b) {
			return max * (b - a);
		}

		/**
		 * 
		 */
		@Override
		public void run() {

			double x;
			double y;
			double f;

			int hit = 0;

			Rand rand = new Rand();

			long startTime = System.currentTimeMillis();
			do {
				x = rand.nextDouble(a, b);
				y = rand.nextDouble(0, max);
				f = Math.abs(fun.calc(x));

				if (y < f) {
					hit++;
				}
				iterations--;

			} while (iterations > 0
							&& (System.currentTimeMillis() - startTime) < MAX_TIME);

			synchronized (HITS) {
				HITS.add(hit);
			}

		}

		/**
		 * 
		 */
		public static class Hits {

			private Integer hits;

			public Hits(int init) {
				hits = init;
			}

			/**
			 * 
			 * @param val 
			 */
			public void add(int val) {
				synchronized (hits) {
					hits += val;
				}
			}

			/**
			 * 
			 * @param val 
			 */
			public void set(int val) {
				hits = val;
			}

			/**
			 * 
			 * @return 
			 */
			public double get() {
				return hits;
			}
		}
	}

	/**
	 * 
	 */
	public static interface Fun {

		/**
		 * 
		 * @param x
		 * @return 
		 */
		public double calc(double x);

		/**
		 * 
		 * @param start
		 * @param end
		 * @return 
		 */
		public double max(double start, double end);
	}
}

//----------------------------------------------------------------------------
/**
 * 
 * @author politechnika
 */
class Rand extends Random {

	/**
	 * 
	 * @param max
	 * @param min
	 * @return 
	 */
	private static final long serialVersionUID = 0;
	
	public double nextDouble(double max, double min) {
		return min + (max - min) * nextDouble();
	}
}