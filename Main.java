
/**
 *
 * @author Zofia Sobocinska
 */
public class Main {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		double a0 = 0.0;
		double a1 = Double.parseDouble(args[1]);

		Fun fun = new Fun(a1);

		Chain chain = new Chain(a0, a1, fun);
		double integral = chain.integral();

		System.out.println(integral);
	}

	/**
	 *
	 */
	private static class Fun implements Chain.Fun {

		private double a;

		/**
		 *
		 * @param a
		 */
		public Fun(double a) {
			this.a = a;
		}

		/**
		 *
		 * @param x
		 * @return
		 */
		@Override
		public double calc(double x) {
			return a * Math.cosh(x / a);
		}

		/**
		 *
		 * @param start
		 * @param end
		 * @return
		 */
		@Override
		public double max(double start, double end) {
			start = Math.abs(start);
			end = Math.abs(end);

			return calc((start < end) ? end : start);
		}
	}
}
