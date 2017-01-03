package main;

/**
 * a drop-in replacement for Android's logging class
 * @author Qi Liang
 *
 */
public class Log {
	private static final boolean debug = true;
	public static void i (String mainMessage, String detail){
		if(debug){
			System.out.println(mainMessage + ": " + detail);
		}
	}

}
