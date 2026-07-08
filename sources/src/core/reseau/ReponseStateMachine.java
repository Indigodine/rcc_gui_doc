package core.reseau;

import javax.xml.ws.Holder;

import core.SMState;
import core.SMTransition;

public class ReponseStateMachine extends Reponse
{
	private Holder<Integer> SMEtat = new Holder<Integer>(-1);

	private Holder<Integer> SMTransit = new Holder<Integer>(-1);

	private Holder<Integer> SMError = new Holder<Integer>(-1);

	public void setSMEtat(Holder<Integer> s)
	{
		SMEtat = s;
	}

	public Holder<Integer> getSMEtat()
	{
		return SMEtat;
	}

	public void setSMError(Holder<Integer> s)
	{
		SMError = s;
	}

	public Holder<Integer> getSMError()
	{
		return SMError;
	}

	public boolean isSMWarning()
	{
		if (SMError.value == 22)
			return true;
		else
			return false;
	}

	public boolean isSMError()
	{
		if ((SMError.value > 0) && (SMError.value != 22))
			return true;
		else
			return false;
	}

	public int getIntSMState()
	{
		return SMEtat.value;
	}

	public SMState getSMState()
	{
		return SMState.decode(SMEtat.value);
	}

	public String getStringSMState()
	{
		return getSMState().name();
	}

	public int getIntSMTransition()
	{
		return SMTransit.value;
	}

	public boolean isSMTransition()
	{
		if (SMTransit.value > 0)
			return true;
		else
			return false;
	}

	public void setSMTransit(Holder<Integer> s)
	{
		SMTransit = s;
	}

	public Holder<Integer> getSMTransit()
	{
		return SMTransit;
	}

	public SMTransition getSMTransition()
	{
		return SMTransition.decode(SMTransit.value);
	}

	public String getStringSMTransition()
	{
		return getSMTransition().getName();
	}
}
