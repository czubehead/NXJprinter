package printer;
import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

/**
 * @author czubehead <a href="petrcech.eu">website</a>
 * @version 1.0
 * @since 24.8.2015
 * static class used to receive image from PC via USB connection
 */
public class PrinterComm
{
	private PrinterComm(){}
	
	/**
	 * method to acquire the image from USB.
	 * Image are 1s and 0s in rows, where 1 is black and 0 white.
	 * All stream initialization logic is here, you don't have to worry about anything.
	 * This method is blocking.
	 * @return image from USB
	 */
	public static boolean[][] WaitForData()
	{
		boolean[][] out=new boolean[Printer.img_widht][Printer.img_height];
		
		NXTConnection conn=USB.waitForConnection();
		DataInputStream stream=conn.openDataInputStream();
		
		try
		{
			for(int y=0;y<Printer.img_height;y++)//each line
			{
				String line=stream.readUTF();
				
				for(int x=0;x<Printer.img_widht;x++)//convert characters to boolean
				{
					out[x][y]=line.charAt(x)=='1';
				}
			}
			
			stream.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
}
