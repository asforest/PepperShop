package cn.hyperrain.peppershop.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.stroage.ShopConfig;
import cn.hyperrain.peppershop.shop.ShopData;
import cn.hyperrain.peppershop.utils.Pair;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.scheduler.PluginTask;

public class HologramItemListener implements Listener
{
	ArrayBlockingQueue<Pair<Collection<Player>, DataPacket>> queue = new ArrayBlockingQueue<Pair<Collection<Player>,DataPacket>>(3000);
	
	PluginTask<PepperShop> sendDataPacketTask = new PluginTask<PepperShop>(PepperShop.ins)
	{
		@Override
		public void onRun(int currentTicks) 
		{
			int packetSendDelay = 50;
			
			while (true) 
			{
				try {
					Pair<Collection<Player>, DataPacket> x = queue.take();
					
					Server.broadcastPacket(x.key, x.value);

					int interval = PepperShop.ins.pluginConfig.hologramItemEffect;

					packetSendDelay = interval==0? 1:(1*1000 / interval);
					packetSendDelay = Math.min(Math.max(packetSendDelay, 0), 5000);
					
					Thread.sleep(packetSendDelay);
				}
				catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	};
	
	public HologramItemListener(PepperShop main)
	{
		main.getServer().getScheduler().scheduleDelayedTask(sendDataPacketTask, 0, true);
	}
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) 
	{
		for (ShopConfig shopConfig : PepperShop.ins.shopsConfig.getAllShops())
		{
			for (ShopData shopData : shopConfig.shopDataMapping.values())
			{
				if(shopData.world.equals(e.getPlayer().level.getFolderName()))
					addShopItemEntity(Arrays.asList(e.getPlayer()), shopData);
			}
		}
    }
	
	
	
	@EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) 
    {
		Player player = event.getPlayer();
		
		if(!event.getTo().level.getName().equals(event.getFrom().level.getName()))
		{
			for (ShopConfig shopConfig : PepperShop.ins.shopsConfig.getAllShops())
			{
				for (ShopData shopData : shopConfig.shopDataMapping.values())
				{
					if(shopData.world.equals(event.getTo().level.getFolderName()))
					{
						addShopItemEntity(Arrays.asList(player), shopData);
					}

					if(shopData.world.equals(event.getFrom().level.getFolderName()))
					{
						removeItemEntity(Arrays.asList(player), shopData);
					}
				}

			}
		}
    }
	
	
	public void removeAllItemEntityForAllPlayer()
	{
		for (ShopConfig shopConfig : PepperShop.ins.shopsConfig.getAllShops())
		{
			for (ShopData shopData : shopConfig.shopDataMapping.values())
			{
				for (Player player : Server.getInstance().getOnlinePlayers().values())
				{
					if (shopData.world.equals(player.level.getFolderName()))
						removeItemEntity(Arrays.asList(player), shopData, true);
				}
			}
		}
	}
	
	public void addAllItemEntityForAllPlayer()
	{
		for (ShopConfig shopConfig : PepperShop.ins.shopsConfig.getAllShops())
		{
			for (ShopData shopData : shopConfig.shopDataMapping.values())
			{
				for (Player player : Server.getInstance().getOnlinePlayers().values())
				{
					if (shopData.world.equals(player.level.getFolderName()))
						addShopItemEntity(Arrays.asList(player), shopData, true);
				}
			}

		}
	}


	public long addShopItemEntity(Collection<Player> players, ShopData shopData)
	{
		return addShopItemEntity(players, shopData, false);
	}

	public long addShopItemEntity(Collection<Player> players, ShopData shopData, boolean force)
	{
		if(!force && PepperShop.ins.pluginConfig.hologramItemEffect<=0) return 0L;
		
		long entityId = shopData.shopRandomId;
		
		AddItemEntityPacket addItemEntityPacket = new AddItemEntityPacket();
		addItemEntityPacket.entityUniqueId = entityId;
		addItemEntityPacket.entityRuntimeId = addItemEntityPacket.entityUniqueId;
		addItemEntityPacket.item = shopData.getShop().getItem();

		addItemEntityPacket.x = (shopData.chestX + 0.5F);
		addItemEntityPacket.y = (shopData.chestY + 1F);
		addItemEntityPacket.z = (shopData.chestZ + 0.5F);
		addItemEntityPacket.speedX = 0f;
		addItemEntityPacket.speedY = 0f;
		addItemEntityPacket.speedZ = 0f;
		long flags = 1 << Entity.DATA_FLAG_IMMOBILE;
		addItemEntityPacket.metadata = new EntityMetadata()
				.putLong(Entity.DATA_FLAGS, flags)
				.putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
				.putFloat(Entity.DATA_SCALE, 4f);
		try {
			queue.offer(new Pair<Collection<Player>, DataPacket>(players, addItemEntityPacket), 2L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {e.printStackTrace();}
		
		return entityId;
	}

	public void removeItemEntity(Collection<Player> players, ShopData shopData)
	{
		removeItemEntity(players, shopData, false);
	}
	
	public void removeItemEntity(Collection<Player> players, ShopData shopData, boolean force)
	{
		if(!force && PepperShop.ins.pluginConfig.hologramItemEffect<=0) return;
		
		RemoveEntityPacket removeItemEntityPacket = new RemoveEntityPacket();
		removeItemEntityPacket.eid = shopData.shopRandomId;

		try {
			queue.offer(new Pair(players, removeItemEntityPacket), 2L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {e.printStackTrace();}
	}
	
}
