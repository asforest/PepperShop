package cn.hyperrain.peppershop.stroage;

import cn.hyperrain.peppershop.shop.Shop;
import cn.hyperrain.peppershop.shop.ShopData;

import java.io.File;

public class ShopsConfig extends BaseMultiConfig
{
	public ShopsConfig(File dir)
	{
		super(dir, ShopConfig.class);
		reloadAllShops();
	}

	@Override
	protected String getFilenameFilterRegex(String suffix)
	{
		return String.format("^.*\\%s$", suffix);
	}

	public ShopConfig getShopsConfig(Shop shop, boolean create)
	{
		return getShopsConfig(shop.shopData.world, create);
	}

	public ShopConfig getShopsConfig(ShopData shopData, boolean create)
	{
		return getShopsConfig(shopData.world, create);
	}

	public ShopConfig getShopsConfig(String world, boolean create)
	{
		return (ShopConfig) getBaseConfigByFileName(world, create);
	}

	public void reloadAllShops()
	{
		reloadAll();
	}

	public ShopConfig[] getAllShops()
	{
		return contents.values().toArray(new ShopConfig[0]);
	}

}
