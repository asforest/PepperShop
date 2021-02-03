package cn.innc11.peppershop.virtualLand;

import cn.innc11.peppershop.PepperShop;
import cn.nukkit.level.Position;
import cn.smallaswater.land.lands.data.LandData;
import cn.smallaswater.land.utils.DataTool;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public abstract class VirtualAreaManage
{
	private static boolean ResidenceEnabled()
	{
		return PepperShop.ins.ResidencePluginLoaded && PepperShop.ins.pluginConfig.linkWithResidencePlugin;
	}

	private static boolean LandEnabled()
	{
		return PepperShop.ins.LandPluginLoaded && PepperShop.ins.pluginConfig.linkWithLandPlugin;
	}

	public static boolean existingAreaManagementPlugin()
	{
		return ResidenceEnabled() || LandEnabled();
	}

	public static VirtualAreaManage getByLoc(Position pos)
	{
		if(ResidenceEnabled())
		{
			ClaimedResidence res = Residence.getResidenceManager().getByLoc(pos);

			if(res!=null)
				return new VirtualResidence(res);
		}

		if(LandEnabled())
		{
			LandData land = DataTool.getPlayerLandData(pos);
			if(land!=null)
				return new VirtualLand(land);
		}

		return null;
	}

	public abstract boolean hasPermission(String playerName, Permissions perm);

	public abstract String getOwner();

	public abstract String getName();

	public enum Permissions
	{
		build,
//		placing,
//		breaking
	}
}
