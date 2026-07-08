package core.reseau;

import javax.xml.ws.Holder;

public class ReponseEquip extends Reponse
{
	private Holder<String> name = new Holder<String>("");
	private Holder<Integer> type = new Holder<Integer>();
	private Holder<String> typestr = new Holder<String>("");
	private Holder<String> host = new Holder<String>("");
	private Holder<String> cpu = new Holder<String>("");
	private Holder<String> narval = new Holder<String>("");
	private Holder<String> exe = new Holder<String>("");
	private Holder<String> log = new Holder<String>("");
	private Holder<String> username = new Holder<String>("");
	private Holder<String> expname = new Holder<String>("");
	private Holder<String> loggername = new Holder<String>("");
	private Holder<Integer> blocksize = new Holder<Integer>(-1);
	private Holder<Integer> port = new Holder<Integer>(-1);
	private Holder<String> id = new Holder<String>("");

	public void setName(Holder<String> name)
	{
		this.name = name;
	}

	public Holder<String> getName()
	{
		return name;
	}

	public void setType(Holder<Integer> type)
	{
		this.type = type;
	}

	public Holder<Integer> getType()
	{
		return type;
	}

	public void setTypestr(Holder<String> typestr)
	{
		this.typestr = typestr;
	}

	public Holder<String> getTypestr()
	{
		return typestr;
	}

	public void setHost(Holder<String> host)
	{
		this.host = host;
	}

	public Holder<String> getHost()
	{
		return host;
	}

	public void setCpu(Holder<String> cpu)
	{
		this.cpu = cpu;
	}

	public Holder<String> getCpu()
	{
		return cpu;
	}

	public void setNarval(Holder<String> narval)
	{
		this.narval = narval;
	}

	public Holder<String> getNarval()
	{
		return narval;
	}

	public void setExe(Holder<String> exe)
	{
		this.exe = exe;
	}

	public Holder<String> getExe()
	{
		return exe;
	}

	public void setLog(Holder<String> log)
	{
		this.log = log;
	}

	public Holder<String> getLog()
	{
		return log;
	}

	public void setUsername(Holder<String> username)
	{
		this.username = username;
	}

	public Holder<String> getUsername()
	{
		return username;
	}

	public void setExpname(Holder<String> expname)
	{
		this.expname = expname;
	}

	public Holder<String> getExpname()
	{
		return expname;
	}

	public void setLoggername(Holder<String> loggername)
	{
		this.loggername = loggername;
	}

	public Holder<String> getLoggername()
	{
		return loggername;
	}

	public void setBlocksize(Holder<Integer> blocksize)
	{
		this.blocksize = blocksize;
	}

	public Holder<Integer> getBlocksize()
	{
		return blocksize;
	}

	public void setPort(Holder<Integer> port)
	{
		this.port = port;
	}

	public Holder<Integer> getPort()
	{
		return port;
	}

	public void setId(Holder<String> id)
	{
		this.id = id;
	}

	public Holder<String> getId()
	{
		return id;
	}
}
