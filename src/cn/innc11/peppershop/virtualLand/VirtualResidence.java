package cn.innc11.peppershop.virtualLand;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class VirtualResidence extends VirtualAreaManage
{
	public ClaimedResidence res;

	public VirtualResidence(ClaimedResidence res)
	{
		this.res = res;
	}

	@Override
	public boolean hasPermission(String playerName, Permissions perm)
	{
		boolean has = false;

		switch(perm)
		{
			case build:
				has = res.getPermissions().playerHas(playerName, "build", false);
				break;

//			case placing:
//				has = res.getPermissions().playerHas(playerName, "place", false);
//				break;
//
//			case breaking:
//				has = res.getPermissions().playerHas(playerName, "break", false);
//				break;
		}

		return has;
	}

	@Override
	public String getOwner()
	{
		return res.getOwner();
	}

	@Override
	public String getName()
	{
		return res.getName();
	}
}
