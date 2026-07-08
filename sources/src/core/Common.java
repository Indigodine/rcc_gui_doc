package core;

import gui.components.Img;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.apache.log4j.Logger;

import core.reseau.ClientSOAP;

public class Common
{
	static ResourceBundle labels = null;
	public static ClientSOAP myClientSOAP;
	private static boolean errorSoap = false;
	private static boolean online = false;
	public static boolean stopMonitor = true;
	public static boolean detailedView = true;
	public static boolean modeExpert = false;
	public static boolean modeBasic = false;
	public static Logger myLogger;

	public static void getLangue()
	{
		String nom = null;
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		nom = (String) env.get("RCC_LANG");
		try
		{
			if (nom.toUpperCase().equals("ENGLISH"))
			{
				Locale.setDefault(new Locale("en", "GB"));
			} else if (nom.toUpperCase().equals("FRENCH"))
			{
				Locale.setDefault(new Locale("fr", "FR"));
			}
		} catch (NullPointerException npe)
		{
			System.err.println("Variable d'environnement RCC_LANG absente (ENGLISH par défaut)");
			Locale.setDefault(new Locale("en", "GB"));
		}
		try
		{
			labels = ResourceBundle.getBundle("ApplicationResources");
		} catch (MissingResourceException mre)
		{
			mre.printStackTrace();
		}
	}

	public static boolean elogPresent()
	{
		String nom = null;
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		nom = (String) env.get("ELOG_PRESENT");
		try
		{
			if (nom.toUpperCase().equals("1"))
				return true;
			else
				return false;

		} catch (NullPointerException npe)
		{
			return false;
		}
	}

	public static String getPath()
	{
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		return ((String) env.get("RCC_SAVE_PATH") + "/");
	}

	public static String getRCCName()
	{
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		return ((String) env.get("RCC_EXPERIMENT_NAME"));
	}

	/** appeler dans RccLocator.java de SOAP **/
	// private java.lang.String rcc_address = "http://localhost:8061";
	public static String getRCCPort()
	{
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		return ((String) env.get("RCC_PORT"));
	}

	public static String getHost()
	{
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		return ((String) env.get("HOST"));
	}

	public static String getLoginName()
	{
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		return ((String) env.get("LOGNAME"));
	}

	public static String getRCCHost()
	{
		Properties env = null;

		try
		{
			env = getEnvironment();
		} catch (IOException e)
		{
		}

		return ((String) env.get("RCC_HOST"));
	}

	public static Properties getEnvironment() throws java.io.IOException
	{
		Properties env = new Properties();
		env.load(Runtime.getRuntime().exec("env").getInputStream());
		return env;
	}

	public static String getString(String s)
	{
		String ss;
		try
		{
			ss = labels.getString(s);
		} catch (MissingResourceException mre)
		{
			System.err.println(s + "=" + s);
			ss = s;
		}
		return ss;
	}

	public static boolean YesNoDialog(JFrame mw, String prompt, String yes, String no)
	{
		Object[] options =
		{ getString(yes), getString(no) };

		int n = JOptionPane.showOptionDialog(mw, prompt, Common.getString("Question"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Img.icon("question"), options, options[1]);

		if (n == 0)
			return true;
		else
			return false;
	}

	public static void setErrorSoap(boolean errorSoap)
	{
		Common.errorSoap = errorSoap;
	}

	public static boolean isErrorSoap()
	{
		return errorSoap;
	}

	public static void setOnline(boolean online)
	{
		Common.online = online;
	}

	public static void setOffline()
	{
		setOnline(false);
	}

	public static void setOnline()
	{
		setOnline(true);
	}

	public static boolean isOnline()
	{
		return online;
	}

	/**
	 * <br>
	 * set the default colors, fonts for the class i.e, buttons, labels, menus
	 * with values loaded from the properties file given as an InputStream
	 */
	public static void setColorsAndFonts(InputStream is)
	{
		Border borderR = BorderFactory.createRaisedBevelBorder();
		Border borderL = BorderFactory.createLoweredBevelBorder();
		Border buttonBorder = new CompoundBorder(borderL, borderR);
		Border labelBorder = new CompoundBorder(borderR, borderL);

		String[] myFonts =
		{ "Menu.font", "MenuItem.font", "Label.font", "Button.font", "ToolTip.font" };

		UIManager.put("Button.border", buttonBorder);
		UIManager.put("Label.border", labelBorder);

		String fontDefault = "Purisa-16";

		UIManager.put("Label.foreground", new Color(0, 0, 0));
		UIManager.put("Button.foreground", new Color(0, 0, 0));
		UIManager.put("ToolTip.background", new Color(255, 255, 23));
		UIManager.put("ToolTip.foreground", new Color(84, 58, 255));

		// Load file and setup display resources
		try
		{
			Properties p = new Properties();
			p.load(is);

			for (String element : myFonts)
			{
				Font f = Font.decode(p.getProperty(element, fontDefault));
				UIManager.put(element, f);
			}
		} catch (Exception e)
		{
			System.err.println("Using default properties.");
		}
	}

	public static String getDate()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		formatter.setCalendar(Calendar.getInstance(Locale.FRANCE));
		Date currentTime = new Date();
		String sDate = formatter.format(currentTime);
		return sDate;
	}

	public static String getComputerFullName()
	{
		String hostName = null;
		try
		{
			final InetAddress addr = InetAddress.getLocalHost();
			hostName = new String(addr.getHostName());
		} catch (final Exception e)
		{
		}
		return hostName;
	}
}