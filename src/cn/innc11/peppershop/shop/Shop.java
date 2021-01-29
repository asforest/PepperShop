package cn.innc11.peppershop.shop;

import java.util.Random;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.stroage.ShopConfig;
import cn.innc11.peppershop.utils.InvItem;
import cn.innc11.peppershop.utils.Quick;
import cn.innc11.peppershop.variousland.VariousLand;
import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockWallSign;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Faceable;

public abstract class Shop 
{
	public final ShopData shopData;

	protected Shop(String shopKey)
	{
		shopData = PepperShop.ins.shopsConfig.getShopsConfig(getShopWorldByLocation(shopKey), false).getShopData(shopKey);
		
		if(shopData ==null) throw new NullPointerException("The Shop-Key can not be found in the configure file("+shopKey+")");
	}

	public ShopData getShopData()
	{
		return shopData;
	}

	public Level getLevel()
	{
		return PepperShop.ins.getServer().getLevelByName(shopData.world);
	}

	public Position getChestPosition()
	{
		return new Position(shopData.chestX, shopData.chestY, shopData.chestZ, getLevel());
	}

	public Position getSignPosition()
	{
		return new Position(shopData.signX, shopData.chestY, shopData.signZ, getLevel());
	}

	public BlockEntityChest getShopEntityChest()
	{
		BlockEntity entityBlock = getLevel().getBlockEntity(getChestPosition());
		
		return (entityBlock instanceof BlockEntityChest) ? (BlockEntityChest)entityBlock : null;
	}
	
	public BlockEntitySign getShopEntitySign()
	{
		BlockEntity signEntity = getLevel().getBlockEntity(getSignPosition());
		
		return (signEntity instanceof BlockEntitySign) ? (BlockEntitySign)signEntity : null;
	}
	
	public void updateSignText()
	{
		// delay for 5 ticks(about 0.25 second)
		updateSignText(5);
	}
	
	@SuppressWarnings("deprecation")
	public void updateSignText(int delayTicks)
	{
		BlockChest blockChest = (BlockChest) getShopEntityChest().getBlock();
		
		BlockFace chestFace = BlockFace.SOUTH;
		
		switch (blockChest.getBlockFace()) 
		{
			case SOUTH: chestFace = BlockFace.WEST; break;
			case NORTH: chestFace = BlockFace.NORTH; break;
			case WEST: chestFace = BlockFace.EAST; break;
			case EAST: chestFace = BlockFace.SOUTH; break;
			default: break;
		}
		
		Position signPos = blockChest.getSide(chestFace);
		Block blockSign = blockChest.level.getBlock(signPos);
		
		// if there is an air-block in front of the Chest
		if(blockSign.getId()==Block.AIR)
		{
			BlockEntity.createBlockEntity(BlockEntity.SIGN, signPos);
			blockChest.level.setBlock(signPos, Block.get(Block.WALL_SIGN, blockChest.getDamage()));
		}
		
		PepperShop.ins.getServer().getScheduler().scheduleDelayedTask(new PluginTask<PepperShop>(PepperShop.ins){
			@Override
			public void onRun(int currentTick) 
			{
				updateSignTextImmediately();
			}
		}, delayTicks);
	}
	
	@Deprecated
	@DeprecationDetails(reason = "renamed", since = "1.3.1", replaceWith = "updateSignTextImmediately()")
	public void updateSignTextNow()
	{
		updateSignTextImmediately();
	}
	
	public void updateSignTextImmediately()
	{
		BlockEntitySign entitySign = getShopEntitySign();

		entitySign.setText(PepperShop.ins.localization.signText.get(this));
	}
	
	
	@Override
	public boolean equals(Object obj) 
	{
		boolean ret = false;
		
		if(obj instanceof Shop)
		{
			ret = ((Shop) obj).shopData.equals(shopData);
		}

		return ret;
	}
	
	
	public Item getItem()
	{
		return shopData.getItem();
	}

	public String getStringPrice()
	{
		return String.valueOf(shopData.price); /*String.format("%.2f", data.price)*/
	}

	public int getStock()
	{
		Item item = getItem();

		if(shopData.type==ShopType.BUY)
			return InvItem.getItemInInventoryCount(getShopEntityChest().getInventory(), item);
		
		return getShopEntityChest().getInventory().getFreeSpace(item);
	}

	public void destroySignOnly()
	{
		Block signBlock = getShopEntitySign().getBlock();

		if(signBlock.getId()==Block.WALL_SIGN)
		{
			signBlock.level.setBlock(signBlock, Block.get(Block.AIR));
		}
	}

	public boolean destroy(Player player)
	{
		if (PepperShop.ins.shopsConfig.getShopsConfig(shopData, false).destroyShop(shopData, player))
		{
			destroySignOnly();
			return true;
		}
		return false;
	}

	public String convertShopLocationByPos()
	{
		return String.format("%d:%d:%d:%s", shopData.chestX, shopData.chestY, shopData.chestZ, shopData.world);
	}
	
	public abstract int getMaxTransactionVolume(float playerMoney, int playerItemCount);


	// static methods
	
	@SuppressWarnings("deprecation")
	public static Shop placeShop(BlockChest chest, float price, Player player)
	{
		Shop SHOP = null;
		
		BlockChest chestBlock = chest;
		
		BlockFace chestFace = BlockFace.SOUTH;
		
		switch (chestBlock.getBlockFace()) 
		{
			case SOUTH: chestFace = BlockFace.WEST; break;
			case NORTH: chestFace = BlockFace.NORTH; break;
			case WEST: chestFace = BlockFace.EAST; break;
			case EAST: chestFace = BlockFace.SOUTH; break;
			default: break;
		}
		
		Position signPos = chestBlock.getSide(chestFace);
		Block signBlock = chestBlock.level.getBlock(signPos);
		
		if(signBlock.getId()==Block.AIR)
		{
			boolean allow = true;
			
			if(PepperShop.ins.ResidencePluginLoaded)
			{
				VariousLand chestVLand = VariousLand.getByLoc(chest);
				VariousLand signVLand = VariousLand.getByLoc(signPos);


				if(chestVLand!=null)
				{
					if(signVLand!=null)
					{
						if(!signVLand.getName().equals(chestVLand.getName()))
						{
							// the chest and the sign must be in a same residence
							player.sendMessage(Quick.t(LangNodes.im_not_allow_cross_residence));
							allow = false;
						}
					} else {
						// the sign not in a residence
						player.sendMessage(Quick.t(LangNodes.im_sign_not_in_residence));
						allow = false;
					}
				} else {
					if(signVLand!=null)
					{
						// the sign is not allowed in a residence
						player.sendMessage(Quick.t(LangNodes.im_not_allow_sign_in_another_residence));
						allow = false;
					}
				}
			}
			
			if(allow)
			{
				Item itemInHand = player.getInventory().getItemInHand();

				if(itemInHand.getId()!=Item.AIR)
				{
					BlockEntity.createBlockEntity(BlockEntity.SIGN, signPos);
					chestBlock.level.setBlock(signPos, Block.get(Block.WALL_SIGN, chestBlock.getDamage()));

					ShopData sd = new ShopData();

					sd.owner = player.getName();
					sd.type = ShopType.BUY;
					sd.price = price;
					sd.chestX = (int) chestBlock.x;
					sd.chestY = (int) chestBlock.y;
					sd.chestZ = (int) chestBlock.z;
					sd.signX = (int) signBlock.x;
					sd.signZ = (int) signBlock.z;
					sd.world = chestBlock.level.getName();
					sd.item = itemInHand.clone();
					sd.item.setCount(1);
					sd.serverShop = false;
					sd.shopRandomId = generateRandomShopId();

					ShopConfig shopConfig = PepperShop.ins.shopsConfig.getShopsConfig(sd, true);
					shopConfig.addOrUpdateShop(sd);

					SHOP = sd.getShop();
					SHOP.updateSignText();

					player.sendMessage(Quick.t(LangNodes.im_successfully_created_shop));
				} else {
					player.sendMessage(Quick.t(LangNodes.im_no_item_in_hand));
				}

			}
			
		} else {
			// the sign of shop is blocked
			player.sendMessage(Quick.t(LangNodes.im_shop_sign_blocked, "BLOCK", PepperShop.ins.itemNameTranslationConfig.getItemName(signBlock.toItem())));
		}
		
		return SHOP;
	}

	protected static String getShopWorldByLocation(String location)
	{
		return location.substring(location.lastIndexOf(":")+1);
	}
	
	private static String convertShopLocationByPos(Position pos)
	{
		return String.format("%d:%d:%d:%s", (int)pos.x, (int)pos.y, (int)pos.z, pos.level.getFolderName());
	}
	
	public static Shop getShopByRandomId(long randomId)
	{
		for (ShopConfig sc : PepperShop.ins.shopsConfig.getAllShops())
		{
			for(ShopData shopData : sc.shopDataMapping.values())
			{
				if(shopData.shopRandomId == randomId)
				{
					return shopData.getShop();
				}
			}
		}

		return null;
	}

	public static Shop getShopByLocation(String shopLoc)
	{
		ShopConfig sc = PepperShop.ins.shopsConfig.getShopsConfig(getShopWorldByLocation(shopLoc), false);
		ShopData shopData = null;

		if(sc!=null)
		{
			shopData = sc.getShopData(shopLoc);

			if(shopData==null)
			{
				return null;
			}
		}else {
			return null;
		}

		if(shopData.type==ShopType.BUY)
		{
			return new BuyShop(shopLoc);
		} else
		if(shopData.type==ShopType.SELL)
		{
			return new SellShop(shopLoc);
		}

		return null;
	}

	public static Shop findShopByChestPos(Position chestPos)
	{
		return Shop.getShopByLocation(Shop.convertShopLocationByPos(chestPos));
	}

	public static Shop findShopBySignPos(Position signPos)
	{
		if(!(signPos instanceof Faceable)) return null;

		Faceable sign = (Faceable) signPos.getLevelBlock();
		BlockFace chestFace = BlockFace.SOUTH;

		switch (sign.getBlockFace())
		{
			case SOUTH: chestFace = BlockFace.NORTH; break;
			case NORTH: chestFace = BlockFace.SOUTH; break;
			case WEST: chestFace = BlockFace.EAST; break;
			case EAST: chestFace = BlockFace.WEST; break;
			default: break;
		}

		Position chestPos = signPos.getSide(chestFace);
		return Shop.getShopByLocation(Shop.convertShopLocationByPos(chestPos));
	}

	public static Shop findShopByChest(Block chestBlock)
	{
		if(!(chestBlock instanceof BlockChest)) return null;

		return findShopByChestPos(chestBlock);
	}
	
	public static Shop findShopBySign(Block signBlock)
	{
		if(!(signBlock instanceof BlockWallSign)) return null;

		return findShopBySignPos(signBlock);
	}


	public static long generateRandomShopId()
	{
		return generateRandomShopId(false);
	}

	public static long generateRandomShopId(boolean noCheck)
	{
		Random random = new Random();
		random.setSeed(System.currentTimeMillis() + random.nextLong());
		long randomValue;

		if(noCheck)
		{
			randomValue = random.nextLong();
		}else{
			do {
				randomValue = random.nextLong();
			} while (Shop.getShopByRandomId(randomValue) != null);
		}

		return randomValue;
	}
	
}
