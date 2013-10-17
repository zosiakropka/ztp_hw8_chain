
import java.text.DecimalFormat;


/**
 *
 * @author Zofia Sobocinska
 */
public class Main {

	static double a0 = 0.0;
	static double a1;
	static Fun fun;

	/**
	 * main method to run class.
	 * 
	 * @param args Program call args
	 */
	public static void main(String[] args) {

		try {
			a1 = Math.abs(Double.parseDouble(args[1]));
		} catch (Exception ex) {
			fallback(false);
		}

		fun = new Fun(a1);

		Chain chain = new Chain(a0, a1, fun);
		double integral;
		try {
			integral = chain.integral();
			System.out.println(formatOutput(integral));
		} catch (InterruptedException ex) {
			fallback(true);
		}

	}

	private static String formatOutput(double value) {
		return "Integral value: " + Double.parseDouble(new DecimalFormat("#.#####").format(value));

	}
	
	/**
	 * Method called when 
	 * @param aExists 
	 */
	private static void fallback(boolean aExists) {
		if (aExists) {
			System.out.println(fun.integral(a0, a1));
		} else {
			Rand r = new Rand();
			System.out.println(formatOutput(r.nextDouble()));
		}
		System.exit(1);
	}

	/**
	 * Class representing f(x) = a * cosh(x/a) function.
	 * 
	 */
	private static class Fun implements Chain.Fun {

		private double a;

		/**
		 * Function's constructor
		 * @param a - param from Class call
		 */
		public Fun(double a) {
			this.a = a;
		}

		/**
		 * Method to calculate function.
		 * 
		 * @param x function's parameter
		 * @return function's value at x point
		 */
		@Override
		public double calc(double x) {
			return a * Math.cosh(x / a);
		}

		/**
		 * Maximum of the function on the given range.
		 * 
		 * @param start Start of the range
		 * @param end End of the range
		 * @return 
		 */
		@Override
		public double max(double start, double end) {
			start = Math.abs(start);
			end = Math.abs(end);

			return calc((start < end) ? end : start);
		}

		/**
		 * Function of the integral calculated at the x point.
		 * (not used in Monte Carlo integration)
		 * 
		 * @param x Point to calculate integral at
		 * @return Integral at the given point
		 */
		public double integral(double x) {
			return a * a * Math.sinh(x / a);
		}

		/**
		 * Function of the integral at the given range.
		 * (not used in Monte Carlo integration)
		 * 
		 * @param start Beginning of the range
		 * @param end End of the range
		 * @return Integral at the given range
		 */
		public double integral(double start, double end) {
			double a = (start < end) ? start : end;
			double b = (start < end) ? end : start;

			return (integral(b) - integral(a));
		}
	}
}
