package core.reseau;

import javax.xml.ws.Holder;

public class ReponseParam extends Reponse
{

	private Holder<String> name = new Holder<String>("");
	private Holder<String> type = new Holder<String>("");
	private Holder<String> permission = new Holder<String>("");
	private Holder<String> value = new Holder<String>("");
	private Holder<Boolean> narvalcfgfile = new Holder<Boolean>(false);
	private Holder<Boolean> dynamic = new Holder<Boolean>(false);

	public Holder<String> getName()
	{
		return name;
	}
	public Holder<String> getType()
	{
		return type;
	}
	public Holder<String> getPermission()
	{
		return permission;
	}
	public Holder<String> getValue()
	{
		return value;
	}
	public String getStringName()
	{
		return name.value;
	}
	public String getStringType()
	{
		return type.value;
	}
	public String getStringPermission()
	{
		return permission.value;
	}
	public String getStringValue()
	{
		return value.value;
	}
	public Holder<Boolean> getNarvalcfgfile()
	{
		return narvalcfgfile;
	}
	public Holder<Boolean> getDynamic()
	{
		return dynamic;
	}
	public void setName(Holder<String> name)
	{
		this.name = name;
	}
	public void setType(Holder<String> type)
	{
		this.type = type;
	}
	public void setPermission(Holder<String> permission)
	{
		this.permission = permission;
	}
	public void setValue(Holder<String> value)
	{
		this.value = value;
	}
	public void setNarvalcfgfile(Holder<Boolean> narvalcfgfile)
	{
		this.narvalcfgfile = narvalcfgfile;
	}
	public void setDynamic(Holder<Boolean> dynamic)
	{
		this.dynamic = dynamic;
	}
}
