package cn.innc11.peppershop.listener;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.config.ShopConfig;
import cn.innc11.peppershop.shop.ShopData;
import cn.innc11.peppershop.utils.Pair;
import cn.innc11.peppershop.utils.Quick;
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

public class HologramItemListener implements Listener, ReloadableListener
{
	PepperShop ins;
	public ArrayBlockingQueue<Pair<Collection<Player>, DataPacket>> queue;
	
	PluginTask<PepperShop> sendDataPacketTask = new PluginTask<PepperShop>(PepperShop.ins)
	{
		@Override
		public void onRun(int currentTicks) 
		{
			int packetSendingDelay;
			
			while (true) 
			{
				try {
					Pair<Collection<Player>, DataPacket> x = queue.take();
					
					Server.broadcastPacket(x.key, x.value);

					int interval = PepperShop.ins.pluginConfig.hologramItemEffect;

					packetSendingDelay = 1*1000 / interval;
					packetSendingDelay = Math.min(Math.max(packetSendingDelay, 0), 5000);

					if(packetSendingDelay>0)
						Thread.sleep(packetSendingDelay);
				}
				catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	};
	
	public HologramItemListener(PepperShop main)
	{
		ins = main;
		reload();
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
			queue.offer(new Pair<>(players, addItemEntityPacket), 10L, TimeUnit.MILLISECONDS);
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
			queue.offer(new Pair(players, removeItemEntityPacket), 10L, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {e.printStackTrace();}
	}

	@Override
	public void reload()
	{
		HashSet<Pair<Collection<Player>, DataPacket>> temp = null;

		if(queue != null)
		{
			int capacity = getCurrentQueueCapacity();

			if(ins.pluginConfig.packetQueueCapacity!=capacity)
			{
				Quick.debug("拿起 "+ins.pluginConfig.packetQueueCapacity+"|"+capacity);
				temp = new HashSet<>();
				queue.drainTo(temp);
			}
		}

		queue = new ArrayBlockingQueue<>(ins.pluginConfig.packetQueueCapacity);

		if(temp != null)
		{
			try {
				queue.addAll(temp);
			}catch (IllegalStateException e){}
			Quick.debug("放下");
		}
	}

	public int getCurrentQueueCapacity()
	{
		int length = 0;

		try {
			Field f = queue.getClass().getDeclaredField("items");
			f.setAccessible(true);
			length = ((Object[]) f.get(queue)).length;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		return length;
	}

	public int getCurrentQueueCount()
	{
		int count = 0;

		try {
			Field f = queue.getClass().getDeclaredField("count");
			f.setAccessible(true);
			count = (int) f.get(queue);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		return count;
	}
}
