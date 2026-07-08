package core.reseau;
import javax.xml.ws.Holder;

public class ReponseInt extends Reponse
{
	private Holder<Integer> value = new Holder<Integer>(-1);

	public void setValue(Holder<Integer> value)
	{
		this.value = value;
	}

	public Holder<Integer> getValue()
	{
		return value;
	}

	public int getIntValue()
	{
		return value.value;
	}
}
