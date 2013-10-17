
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Class holding integral mechanism
 *
 * @author Zofia Sobocinska
 */
public class Chain {

	/**
	 * Classe's constructor
	 *
	 * @param start Integral range's start
	 * @param end Integral range's end
	 * @param fun Function to calculate
	 */
	public Chain(double start, double end, Fun fun) {

		Chunk.configure(start, end, fun);

	}

	/**
	 * Method to start integral mechanism.
	 *
	 * @return
	 */
	public double integral() throws InterruptedException {
		return Chunk.integral();
	}

	/**
	 * Thread to calculate partial chunk of the integral.
	 */
	private static class Chunk extends Thread {

		// STATIC
		private static final long MAX_TIME = 10 * 1000;
		private static final int THREADS_COUNT = 8;
		private static ArrayList<Chunk> chunks;
		private static Fun fun;
//		private static final int THREAD_ITERATIONS = 1000; // DEVELOPMENT
		private static final int THREAD_ITERATIONS = 1000000; // PRODUCTION
		// NON-STATIC
		private int iterations;
		private double a;
		private double b;
		private double max;
		private double area;
		private double partial;

		/**
		 * Chunk's constructor.
		 *
		 * @param a
		 * @param b
		 */
		private Chunk(double a, double b) {
			this.a = a;
			this.b = b;
			this.iterations = 0;

			max = fun.max(a, b);
			area = area(a, b);
		}

		/**
		 * Method to configure chunk of the integral.
		 *
		 * @param start
		 * @param end
		 * @param _fun
		 */
		static void configure(double start, double end, Fun _fun) {
			fun = _fun;

			chunks = new ArrayList<>();

			double interval = (end - start) / THREADS_COUNT;

			for (int i = 0; i < THREADS_COUNT; i++) {
				chunks.add(new Chunk(i * interval, (i + 1) * interval));
			}
		}

		/**
		 * Method to clear static chunk configuration and calculated values.
		 */
		private static void clear() {
			fun = null;
			chunks = null;
		}

		/**
		 * Method to calculate integral of the function provided.
		 *
		 * @return Total integral value (chunk's sum).
		 */
		static double integral() throws InterruptedException {

			Iterator<Chunk> i = chunks.iterator();

			while (i.hasNext()) {
				i.next().start();
			}
			double result = 0;
			for (Chunk chunk : chunks) {
				chunk.join();
				result += chunk.partial;
			}
			clear();
			return result;
		}

		/**
		 *
		 * @param a
		 * @param b
		 * @return
		 */
		private double area(double a, double b) {
			return max * (b - a);
		}

		/**
		 * Thread's run method overriden
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
				iterations++;

			} while (iterations < THREAD_ITERATIONS
							&& (System.currentTimeMillis() - startTime) < MAX_TIME);

			partial = (new Double(hit) / iterations * area);

		}
	}

	/**
	 * Interface that integrated function's class should implement. It's used by
	 * Chain class to calculate integral.
	 */
	public static interface Fun {

		/**
		 * Method to calculate function.
		 *
		 * @param x
		 * @return
		 */
		public double calc(double x);

		/**
		 * Maximum of the function at given range.
		 *
		 * @param start Range start
		 * @param end Range end
		 * @return Maximum
		 */
		public double max(double start, double end);
	}
}

//----------------------------------------------------------------------------
/**
 * Custom randomizer extending Random class with randomizer at given range.
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

	public double nextDouble(double min, double max) {
		
		double rand = min + (max - min) * nextDouble();
		return (rand != max)?rand:nextDouble(min, max);
	}
}
