package cn.innc11.peppershop;

import cn.innc11.peppershop.command.PluginCommand;
import cn.innc11.peppershop.localization.ItemNameTranslationConfig;
import cn.innc11.peppershop.stroage.ShopsConfig;
import cn.innc11.peppershop.stroage.PluginConfig;
import cn.innc11.peppershop.localization.Localization;
import cn.innc11.peppershop.utils.Quick;
import cn.innc11.peppershop.listener.*;
import cn.innc11.peppershop.localization.LangNodes;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import cn.smallaswater.module.LandModule;
import top.wetabq.easyapi.module.EasyAPIModuleManager;
import top.wetabq.easyapi.module.IEasyAPIModule;
import top.wetabq.easyapi.module.ModuleInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.regex.Pattern;

public class PepperShop extends PluginBase
{
	public static PepperShop ins;
	public static ServerScheduler scheduler;
	public static Server server;
	public static Logger logger;
	public static Localization loc;

	public boolean ResidencePluginLoaded = false;
	public boolean GACPluginLoaded = false;
	public boolean EasyAPIAPILoaded = false;
	public boolean LandPluginLoaded = false;

	public LandModule landModule = null;

	public ShopsConfig shopsConfig;
	public ItemNameTranslationConfig itemNameTranslationConfig;
	public PluginConfig pluginConfig;
	public Localization localization;

	public CreateShopListener createShopListener;
	public cn.innc11.peppershop.listener.ShopInteractionListener ShopInteractionListener;
	public HologramItemListener hologramListener;
	public FormResponseListener formResponseListener;
	public ShopProtectListener shopProtectListener;
	public ItemAndInventoryListener itemAndInventoryListener;

	@Override
	public void onEnable() 
	{
		ins = this;
		scheduler = getServer().getScheduler();
		server = getServer();
		logger  = getLogger();

		try {
			loadConfigs();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			server.getPluginManager().disablePlugin(this);
			return;
		}

		if(getServer().getPluginManager().getPlugin("QuickShop")!=null) 
		{
			getLogger().warning(TextFormat.colorize(Quick.t(LangNodes.pm_cannot_work_with_quickshop)));
			getServer().getPluginManager().disablePlugin(this);
			
			return;
		}

		if(getServer().getPluginManager().getPlugin("QuickShopX")!=null)
		{
			getLogger().warning(TextFormat.colorize(Quick.t(LangNodes.pm_cannot_work_with_quickshopx)));
			getServer().getPluginManager().disablePlugin(this);

			return;
		}
		
		ResidencePluginLoaded = getServer().getPluginManager().getPlugin("Residence")!=null;

		GACPluginLoaded = getServer().getPluginManager().getPlugin("GAC")!=null;

		EasyAPIAPILoaded = getServer().getPluginManager().getPlugin("EasyAPI")!=null;

		LandPluginLoaded = getServer().getPluginManager().getPlugin("Land")!=null;


		if(EasyAPIAPILoaded)
		{
			Quick.info(LangNodes.pm_discovered_easyapi);

			if(LandPluginLoaded)
			{
				for(Map.Entry<ModuleInfo, IEasyAPIModule> module : EasyAPIModuleManager.INSTANCE.getAllModule().entrySet())
				{
					if(module.getKey().getModuleOwner().getName().equals("Land"))
					{
						landModule = (LandModule) module.getValue();
						Quick.info(LangNodes.pm_linked_with_land);
						break;
					}
				}

			}

		}

		if(ResidencePluginLoaded)
		{
			getLogger().info(TextFormat.colorize(localization.get(LangNodes.pm_linked_with_residence)));
		}

		if(GACPluginLoaded)
		{
			if(!pluginConfig.workWithGac)
			{
				getLogger().error(TextFormat.colorize(localization.get(LangNodes.pm_gac_warning_1)));
				server.getPluginManager().disablePlugin(this);
				return;
			}else{
				getLogger().warning(TextFormat.colorize(localization.get(LangNodes.pm_gac_warning_2)));
			}
		}

		createShopListener = new CreateShopListener();
		ShopInteractionListener = new ShopInteractionListener();
		hologramListener = new HologramItemListener(this);
		formResponseListener = new FormResponseListener();
		shopProtectListener = new ShopProtectListener();
		itemAndInventoryListener = new ItemAndInventoryListener();

		registerListeners();

		registerCommands();
	}

	void registerListeners()
	{
		getServer().getPluginManager().registerEvents(ShopInteractionListener, this);
		getServer().getPluginManager().registerEvents(createShopListener, this);
		getServer().getPluginManager().registerEvents(hologramListener, this);
		getServer().getPluginManager().registerEvents(formResponseListener, this);
		getServer().getPluginManager().registerEvents(shopProtectListener, this);
		getServer().getPluginManager().registerEvents(itemAndInventoryListener, this);
	}

	void registerCommands()
	{
		getServer().getCommandMap().register("", new PluginCommand());
	}
	
	public void loadConfigs() throws FileNotFoundException
	{
		saveResource("localization/cn.yml", false);
		saveResource("localization/en.yml", false);

		analyse();

		File itemNamesFile = new File(getDataFolder(), "item-translations.yml");
		File shopsDir = new File(getDataFolder(), "shops");

		pluginConfig = new PluginConfig(new File(getDataFolder(), "config.yml"));
		loc = localization = new Localization(new File(getDataFolder(), String.format("localization/%s.yml", pluginConfig.language)));
		shopsConfig = new ShopsConfig(shopsDir);
		itemNameTranslationConfig = new ItemNameTranslationConfig(itemNamesFile);
	}

	public void reloadConfigs() throws FileNotFoundException
	{
		pluginConfig.reload();
		loc = localization = new Localization(new File(getDataFolder(), String.format("localization/%s.yml", pluginConfig.language)));
		shopsConfig.reloadAllShops();
		itemNameTranslationConfig.reload();
	}
	
	public static boolean isInteger(String str)
	{
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
	
	public static boolean isPrice(String str)
	{
		Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?$");
		return pattern.matcher(str).matches();
	}

	public static void analyse()
	{

	}

}
