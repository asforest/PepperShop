package cn.hyperrain.peppershop.variousland;

import cn.smallaswater.land.data.LandData;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class MappedResidence extends VariousLand
{
	public ClaimedResidence res;

	public MappedResidence(ClaimedResidence res)
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
