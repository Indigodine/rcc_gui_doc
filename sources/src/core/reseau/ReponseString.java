package core.reseau;

import javax.xml.ws.Holder;

public class ReponseString extends Reponse
{
	private Holder<String> value = new Holder<String>("");

	public void setValue(Holder<String> value)
	{
		this.value = value;
	}

	public Holder<String> getValue()
	{
		return value;
	}
	public String getStringValue()
	{
		return value.value;
	}
}
