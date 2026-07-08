package core.reseau;

import javax.xml.ws.Holder;

public class ReponseLink extends Reponse
{
	
	private Holder<String> name = new Holder<String>("");
	private Holder<String> sourceName = new Holder<String>("");
	private Holder<String> destName = new Holder<String>("");
	private Holder<String> sourceOutput = new Holder<String>("");
	private Holder<String> sourcePort = new Holder<String>("");
	private Holder<String> destPort = new Holder<String>("");
	private Holder<Integer> bufferSize = new Holder<Integer>(-1);

	public Holder<String> getName()
	{
		return name;
	}
	public Holder<String> getSourceName()
	{
		return sourceName;
	}
	public Holder<String> getDestName()
	{
		return destName;
	}
	public Holder<String> getSourcePort()
	{
		return sourcePort;
	}
	public Holder<String> getDestPort()
	{
		return destPort;
	}
	public Holder<Integer> getBufferSize()
	{
		return bufferSize;
	}
	public void setName(Holder<String> name)
	{
		this.name = name;
	}
	public void setSourceName(Holder<String> sourceName)
	{
		this.sourceName = sourceName;
	}
	public void setDestName(Holder<String> destName)
	{
		this.destName = destName;
	}
	public void setSourcePort(Holder<String> sourcePort)
	{
		this.sourcePort = sourcePort;
	}
	public void setDestPort(Holder<String> destPort)
	{
		this.destPort = destPort;
	}
	public void setBufferSize(Holder<Integer> bufferSize)
	{
		this.bufferSize = bufferSize;
	}
	public void setSourceOutput(Holder<String> sourceoutput)
	{
		this.sourceOutput = sourceoutput;
	}
	public Holder<String> getSourceOutput()
	{
		return sourceOutput;
	}
}
