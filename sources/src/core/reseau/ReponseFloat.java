package core.reseau;
import javax.xml.ws.Holder;

public class ReponseFloat extends Reponse
{
	private Holder<Float> value = new Holder<Float>();

	public void setValue(Holder<Float> value)
	{
		this.value = value;
	}

	public Holder<Float> getValue()
	{
		return value;
	}
	public float getFloatValue()
	{
		return value.value;
	}
}
