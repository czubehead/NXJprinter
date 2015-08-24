package printer;
import lejos.nxt.Motor;

/**
 * Printing head for the printer. Operates C motor
 * @author czubehead <a href="petrcech.eu">website</a>
 * @version 1.0
 * @since 24.8.2015
 */
public class Head
{
	private int angle;
	private boolean headUp=true;
	
	public Head(int deg_to_raise)
	{
		angle=deg_to_raise;
	}
	
	/**
	 * lowers the head
	 */
	public void down()
	{
		if(headUp)
		{
			Motor.C.rotate(-angle);
			headUp=false;
		}
	}
	
	/**
	 * raises the head
	 */
	public void up()
	{
		if(!headUp)
		{
			Motor.C.rotate(angle);
			headUp=true;
		}
	}
	
}
