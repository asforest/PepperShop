package cn.innc11.peppershop.config;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.pluginEvent.PlayerRemoveShopEvent;
import cn.innc11.peppershop.shop.ShopData;
import cn.innc11.peppershop.shop.ShopType;
import cn.innc11.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

public class ShopConfig extends BaseConfig
{
	private static final int VERSION = 2;

	public HashMap<String, ShopData> shopDataMapping = new HashMap<>();

	public ShopConfig(File config)
	{
		super(config, false);

		loadFile();

		reload();
	}

	private String getWorldName()
	{
		return file.getName().replaceAll("\\.yml$", "");
	}

	public void addOrUpdateShop(ShopData sd)
	{
		shopDataMapping.put(sd.getShopLocation(), sd);
		save();
	}

	public ShopData getShopData(String shopKey)
	{
		return shopDataMapping.get(shopKey);
	}

	public boolean destroyShop(ShopData sd, Player player)
	{
		PlayerRemoveShopEvent event = new PlayerRemoveShopEvent(player, sd.getShop());
		PepperShop.ins.getServer().getPluginManager().callEvent(event);

		if(!event.isCancelled())
		{
			shopDataMapping.get(sd.getShopLocation()).getShop().destroySign();
			shopDataMapping.remove(sd.getShopLocation());
			save();

			return true;
		}

		return false;
	}

	public void removeShop(ShopData sd)
	{
		shopDataMapping.remove(sd.getShopLocation());
		save();
	}


	@Override
	public void Reload()
	{
		if(exists())
		{
			config.reload();
		}else{
			return;
		}

		shopDataMapping.clear();

		int count = 0;
		for(String key : config.getKeys(false))
		{
			if(key.equals("version")) continue;

			ShopData shopData = new ShopData();
			shopData.owner = config.getString(key+".owner");
			shopData.type = ShopType.valueOf(config.getString(key+".shopType"));
			shopData.price = (float) config.getDouble(key+".price");
			shopData.chestX = config.getInt(key+".chestX");
			shopData.chestY = config.getInt(key+".chestY");
			shopData.chestZ = config.getInt(key+".chestZ");
			shopData.signX = config.getInt(key+".signX");
			shopData.signZ = config.getInt(key+".signZ");
			shopData.world = config.getString(key+".world");
			shopData.serverShop = config.getBoolean(key+".serverShop");
			shopData.shopRandomId = config.getLong(key+".shopRandomId");

			String nbtTagText = config.getString(key+".item.nbtTag");
			int id = config.getInt(key+".item.itemId");
			int metadata = config.getInt(key+".item.metadata");
			byte[] nbtTag = !nbtTagText.isEmpty() ? Base64.getDecoder().decode(nbtTagText) : null;

			shopData.item = Item.get(id, metadata);
			Optional.ofNullable(nbtTag).ifPresent((nbt)->shopData.item.setCompoundTag(nbt));

			shopDataMapping.put(key, shopData);
			count++;
		}

		Quick.info(LangNodes.pm_loaded_shops,
				"COUNT", count,
				"WORLD", getWorldName());
	}

	@Override
	protected void Save()
	{
		config.getRootSection().clear();
		config.set("version", VERSION);

		shopDataMapping.forEach((shopKey, shopData)->
		{
			Item item = shopData.getItem();
			String pureItemName = PepperShop.ins.itemNameTranslationConfig !=null? PepperShop.ins.itemNameTranslationConfig.getPureItemName(item):"";
			int id = item.getId();
			int metadata = item.getDamage();
			String[] lore = item.getLore(); // only for debug
			String customName = item.getCustomName(); // only for debug
			Enchantment[] enchantments = item.getEnchantments(); // only for debug
			String nbtTag = item.hasCompoundTag() ? Base64.getEncoder().encodeToString(item.getCompoundTag()) : null;

			config.set(shopKey+".owner", shopData.owner);
			config.set(shopKey+".shopType", shopData.type.name());
			config.set(shopKey+".price", shopData.price);
			config.set(shopKey+".chestX", shopData.chestX);
			config.set(shopKey+".chestY", shopData.chestY);
			config.set(shopKey+".chestZ", shopData.chestZ);
			config.set(shopKey+".signX", shopData.signX);
			config.set(shopKey+".signZ", shopData.signZ);
			config.set(shopKey+".world", shopData.world);
			config.set(shopKey+".shopRandomId", shopData.shopRandomId);
			config.set(shopKey+".serverShop", shopData.serverShop);
			config.set(shopKey+".item.name", pureItemName); // write only
			config.set(shopKey+".item.itemId", id);
			config.set(shopKey+".item.metadata", metadata);
			Optional.ofNullable(nbtTag).ifPresent((o)->config.set(shopKey+".item.nbtTag", nbtTag));

			if(!customName.isEmpty()) // for debugging
			{
				config.set(shopKey+".item.customName", customName);
			}

			if ((lore.length > 0)) // for debugging
			{
				config.set(shopKey+".item.lore", lore);
			}

			for (Enchantment enchant : enchantments) // for debugging
			{
				config.set(shopKey+".item.enchantments.E" + enchant.getId(), enchant.getLevel());
			}

		});

		config.save();
	}
}
