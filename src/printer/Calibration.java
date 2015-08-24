package printer;
import lejos.nxt.Button;
import lejos.nxt.Motor;


/**
 * Static class used to calibrate the printer
 * 
 * @author czubehead <a href="petrcech.eu">website</a>
 * @version 1.0
 * @since 24.8.2015
 */
public class Calibration
{
	private Calibration()
	{
	}

	/**
	 * calibrate vertically using light sensor
	 * 
	 * @param verbose
	 *            print to LCD
	 */
	public static void calibrateVert(boolean verbose)
	{
		if (verbose)
			System.out.print("V cal...");

		int speed = Motor.A.getRotationSpeed();
		Motor.A.setSpeed(100);
		Motor.A.backward();

		while (Printer.lightSensor.getLightValue() < 40);// edit the const
															// value, light
															// calibration
															// cannot
		// be used, because if the obstacle was in front of sensor during
		// calibration,
		// program would end up in infinite loop

		try
		{
			Thread.sleep(250);
		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}

		Motor.A.flt();
		Motor.A.setSpeed(speed);

		Motor.A.rotate(40);

		if (verbose)
			System.out.println("done");
	}

	/**
	 * calibrate horizontally
	 * 
	 * @param verbose
	 *            print output to LCD
	 */
	public static void calibrateHor(boolean verbose)
	{
		if (verbose)
			System.out.print("H cal...");

		int speed = Motor.B.getRotationSpeed();
		Motor.B.setSpeed(100);
		Motor.B.backward();

		while (!Printer.touchSensor.isPressed());

		Motor.B.stop();
		Motor.B.setSpeed(speed);

		if (verbose)
			System.out.println("done");
	}

	/**
	 * calibrate the printing head the user has to manually lower the printing
	 * head just above the paper when he does so, he presses the enter button
	 * and head is now in raised position
	 */
	public static void calibrateHead()
	{
		System.out.print("Head cal...");// set the head just above the surface
		Motor.C.flt();

		Button.ENTER.waitForPress();
		Motor.C.stop();

		System.out.println("done");
	}
}
