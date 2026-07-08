package core.reseau;

import javax.xml.ws.Holder;

public class Reponse
{
	private Holder<Integer> error = new Holder<Integer>(-1);
	private Holder<String> errorMessage = new Holder<String>("");

	public Reponse()
	{}

	public void setErrorMessage(Holder<String> errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public Holder<String> getErrorMessage()
	{
		return errorMessage;
	}

	public void setError(Holder<Integer> error)
	{
		this.error = error;
	}

	public Holder<Integer> getError()
	{
		return error;
	}

	public boolean isWarning()
	{
		if (error.value == 22)
			return true;
		else
			return false;
	}

	public boolean isError()
	{
		if ((error.value > 0) && (error.value != 22))
			return true;
		else
			return false;
	}

	public String getErrorStringMessage()
	{
		return errorMessage.value;
	}
}
