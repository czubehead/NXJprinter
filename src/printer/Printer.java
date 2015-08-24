
package printer;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;

/**
 * @author @author czubehead <a href="petrcech.eu">website</a>
 * @version 1.1
 * @since 24.8.2015
 * <p>
 * motors configuration:
 * X axis (horizontal)=B
 * Y axis (vertical)=A
 * head=C
 */
public class Printer
{
	// if the value jumps up, the obstacle is in way
	static LightSensor lightSensor = new LightSensor(SensorPort.S2);

	static TouchSensor touchSensor = new TouchSensor(SensorPort.S1);

	static final int img_widht = 70;

	static final int img_height = 60;

	/**
	 * How much has the vertical motor to rotate to print a whole image
	 */
	public static final int vert_motor_total = 360;

	/**
	 * How much has the horizontal motor to rotate to print a whole image
	 */
	public static final int hor_motor_total = 420;

	/**
	 * How much to rotate a head motor to raise the head
	 */
	public static final int head_rotate_to_raise = 45;

	/**
	 * motors speed during printing
	 */
	public static final int motors_speed = 400;

	/**
	 * rotation of hor motor to slow down before touching a button
	 */
	public static final int slow_gap = 30;

	/**
	 * Additional rotation performed before printing right to left
	 */
	public static final int forces_conpensation = 10;// v-sync like

	/**
	 * Printing head instance
	 */
	private static Head head;

	/**
	 * Image to print, T=black, F=white
	 */
	static boolean[][] image;

	/**
	 * entry point
	 */
	public static void main(String[] args)
	{
		System.out.println("Upload an image");

		image = PrinterComm.WaitForData();

		Sound.setVolume(50);
		// now the image is received

		Sound.beepSequenceUp();
		// Sound.beepSequence();//report success

		diplayImageToLCD();

		if (Button.waitForAnyPress() != Button.ID_ENTER) // confirm image by
															// enter
		{
			System.out.println("canceled");
			sleeep(1500);
			return;
		}

		LCD.clear();
		LCD.scroll();

		Calibration.calibrateHor(true);
		Calibration.calibrateVert(true);

		Calibration.calibrateHead();

		// ready to print

		head = new Head(head_rotate_to_raise);

		System.out.println("printing image...");

		PrintImage();

		// image is now printed.

		head.up();

		LCD.clear();
		System.out.println("DONE");
		Sound.beepSequenceUp();
		Sound.beepSequence();// yay!

		sleeep(5000);
	}

	/**
	 * print the image after printer is properly calibrated
	 */
	public static void PrintImage()
	{
		Motor.A.setSpeed(motors_speed);
		Motor.B.setSpeed(motors_speed);
		Motor.C.setSpeed(motors_speed);

		LCD.clear();

		// motor A rotates by 390deg during printing
		// motor B by 455deg

		// so 6 deg in both directions is 1 pixel

		int hp = hor_motor_total / img_widht;// horizontal px as motor
												// rotation
		int vp = vert_motor_total / img_height;// vertical px as motor
												// rotation
		boolean[] line = new boolean[img_widht];
		boolean evenOddtoggle = false;

		for (int y = 0; y < img_height; y++)
		{
			boolean hasBlack = false;
			if (Button.ESCAPE.isDown())
				return;

			for (int x = 0; x < img_widht; x++)
			{
				line[x] = image[x][y];
				LCD.setPixel(x, y, line[x] ? 1 : 0);

				if (line[x])
					hasBlack = true;
			}

			if (!hasBlack) // blank line, skip it and remember it
			{
				evenOddtoggle = !evenOddtoggle;
				Motor.A.rotate(vp);// next line
				continue;
			}

			boolean even = y % 2 == 0;

			if (evenOddtoggle)
				even = !even;

			if (even) // even left>right
			{
				int pos = 0;
				boolean currentColor = line[pos];// toggle

				if (currentColor)
					head.down();// 1st pixel in line is black

				for (int x = pos; x < img_widht; x++)
				{
					if (line[x] != currentColor)
					{
						// now is the color change in line
						currentColor = line[x];
						Motor.B.rotate(hp * (x - pos));
						pos = x;

						if (currentColor)
							head.down();
						else
							head.up();
					}

					if (x == img_widht - 1) // last cycle
					{
						Motor.B.rotate(hp * (x - pos));
						head.up();
						break;
					}
				}
			}

			else// odd right>left
			{
				Motor.B.rotate(forces_conpensation);// compensate some Newtonian
													// forces

				int pos = img_widht - 1;
				boolean currentColor = line[pos];// toggle

				if (currentColor)
					head.down();// 1st pixel in line is black

				for (int x = pos; x >= 0; x--)
				{
					if (line[x] != currentColor)
					{
						// now is the color change in line
						currentColor = line[x];
						Motor.B.rotate(-hp * (pos - x));
						pos = x;

						if (currentColor)
							head.down();
						else
							head.up();
					}

					if (x == 0) // last cycle
					{
						if (pos * hp > slow_gap) // enough space for fast
													// non-telemetric end
						{
							Motor.B.rotate((-pos * hp) + slow_gap);
							// rotate to just in front of the button to improve
							// speed
						}

						Motor.B.setSpeed(100);//slow down to maximize precision
						Motor.B.backward();
						while (!touchSensor.isPressed());
						Motor.B.stop();
						Motor.B.setSpeed(motors_speed);

						head.up();
						break;
					}
				}
			}

			Motor.A.rotate(vp);
		}
	}

	/**
	 * utility to make the thread sleep
	 * @param ms how long in ms
	 */
	private static void sleeep(int ms)
	{
		try
		{
			Thread.sleep(ms);// avoid exiting on start
		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * displays the stored image to LCD
	 */
	public static void diplayImageToLCD()
	{
		LCD.clear();

		for (int x = 0; x < img_widht; x++)
		{
			for (int y = 0; y < img_height; y++)
			{
				if (image[x][y])
				{
					LCD.setPixel(x, y, 1);
				}
			}
		}
	}

}
