package core.reseau;

import javax.swing.JFrame;

public class AccesException extends Exception
{
	public static int ACCESNARVAL = 0;
	public static int ACCESVMECOM = 1;
	public static int ACCESMIDAS = 2;

	String[] acces = { "Narval access error", "VmeCom access error", "Midas access error" };
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	int code;
	String message;
	String err;

	public AccesException(int code, String message)
	{
		this.code = code;
		this.message = message;
	}

	public void intervention(JFrame frame)
	{
	}

	public void log()
	{
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return acces[code];
	}
}
