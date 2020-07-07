package cn.innc11.peppershop.variousland;

import cn.smallaswater.land.lands.data.LandData;
import cn.smallaswater.land.players.LandSetting;

public class MappedLand_Small_139 extends VariousLand
{
	public LandData land;

	public MappedLand_Small_139(LandData land)
	{
		this.land = land;
	}

	@Override
	public boolean hasPermission(String playerName, VariousLand.Permissions perm)
	{
		boolean has = false;

		switch(perm)
		{
			case build:
				boolean placePerm = land.hasPermission(playerName, LandSetting.PLACE);
				boolean breakPerm = land.hasPermission(playerName, LandSetting.BREAK);
				has = placePerm && breakPerm;
				break;

			//			case placing:
			//				has = land.hasPermission(playerName, LandSetting.PLACE);
			//				break;
			//
			//			case breaking:
			//				has = land.hasPermission(playerName, LandSetting.BREAK);
			//				break;
		}

		return has;
	}

	@Override
	public String getOwner()
	{
		return land.getMaster();
	}

	@Override
	public String getName()
	{
		return land.getLandName();
	}
}
