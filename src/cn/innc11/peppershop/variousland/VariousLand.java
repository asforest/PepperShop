package cn.innc11.peppershop.variousland;

import cn.innc11.peppershop.PepperShop;
import cn.nukkit.level.Position;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public abstract class VariousLand
{
	private static boolean ResidenceEnabled()
	{
		return PepperShop.ins.ResidencePluginLoaded && PepperShop.ins.pluginConfig.linkWithResidencePlugin;
	}

	private static boolean LandEnabled()
	{
		return PepperShop.ins.LandPluginLoaded && PepperShop.ins.pluginConfig.linkWithLandPlugin;
	}

	private static boolean isLandGreatThanVer1_3_8()
	{
		// PepperShop.ins.getServer().getPluginManager().getPlugin("Land").getDescription().getVersion()
		try {
			Class.forName("cn.smallaswater.utils.DataTool");
			return false;
		}catch(ClassNotFoundException e){
			return true;
		}
	}

	public static boolean existingAreaManagementPlugin()
	{
		return ResidenceEnabled() || LandEnabled();
	}

	public static VariousLand getByLoc(Position pos)
	{
		if(ResidenceEnabled())
		{
			ClaimedResidence res = Residence.getResidenceManager().getByLoc(pos);

			if(res!=null)
				return new MappedResidence(res);
		}

		if(LandEnabled())
		{
			if(VariousLand.isLandGreatThanVer1_3_8())
			{
				cn.smallaswater.land.lands.data.LandData land = cn.smallaswater.land.utils.DataTool.getPlayerLandData(pos);
				if(land!=null)
					return new MappedLand_Small_139(land);
			}else{
				cn.smallaswater.land.data.LandData land = cn.smallaswater.utils.DataTool.getPlayerLandData(pos);
				if(land!=null)
					return new MappedLand_Great_138(land);
			}
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
