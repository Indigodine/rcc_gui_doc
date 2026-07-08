package core.reseau;

import core.SMState;
import core.SMTransition;

public class retourStateMachine
{
	private int error = 0;
	private int etat = 0;
	private int transition = 0;

	public retourStateMachine(ReponseStateMachine rsm)
	{
		setError(rsm.getSMError().value);
		setEtat(rsm.getSMEtat().value);
		setTransition(rsm.getSMTransit().value);
	}

	public int getTransition()
	{
		return transition;
	}

	public void setTransition(int value)
	{
		transition = value;
	}

	public retourStateMachine()
	{}

	public void setError()
	{
		error = 1;
		setEtat(0);
	}

	public void setError(int value)
	{
		error = value;
	}

	public int getError()
	{
		return error;
	}

	public void setEtat(int value)
	{
		etat = value;
	}

	public int getEtat()
	{
		return etat;
	}

	public SMState getSMState()
	{
		return SMState.decode(etat);
	}

	public String getStringSMState()
	{
		return getSMState().name();
	}

	public SMTransition getSMTransition()
	{
		return SMTransition.decode(transition);
	}

	public String getStringSMTransition()
	{
		return getSMTransition().getName();
	}

	public boolean isError()
	{
		if (error == 0) return false;
		return true;
	}

	public boolean isTransition()
	{
		if (transition == 0) return false;
		return true;
	}
}
