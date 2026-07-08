package core.reseau;

import javax.xml.ws.Holder;

public class ReponseBool extends Reponse
{
	private Holder<Boolean> value = new Holder<Boolean>(false);

	public void setValue(Holder<Boolean> value)
	{
		this.value = value;
	}

	public Holder<Boolean> getValue()
	{
		return value;
	}

	public boolean getBooleanValue()
	{
		return value.value;
	}
}
