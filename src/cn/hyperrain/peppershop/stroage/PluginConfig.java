package cn.hyperrain.peppershop.stroage;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.form.PluginControlPanel;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static cn.hyperrain.peppershop.localization.LangNodes.*;

public class PluginConfig extends BaseConfig
{
	@Default(intValue = 7)
	public int version;

	@Default(stringValue = "cn")
	public String language;

	@Default(intValue = 0)
	public boolean workWithGac;

	@Default(intValue = 1)
	@PluginControlPanel.PresentInForm(lang = cp_interaction_way)
	public InteractionWay interactionWay;

	@Default(intValue = 5000, min = 2*1000, max = 10*1000, step = 500)
	@PluginControlPanel.PresentInForm(lang = cp_interaction_time)
	public int interactionTimeout;

	@Default(intValue = 150, min = 0, max = 1000, step = 10)
	@PluginControlPanel.PresentInForm(lang = cp_packet_send_ps)
	@PluginControlPanel.UpdateCallbackInForm(methodName = "updateHologramItemEffect")
	public int hologramItemEffect;

	@Default(intValue = 1)
	@PluginControlPanel.PresentInForm(lang = cp_link_with_residence, conditionCallback = "ccLinkWithResidencePlugin")
	@PluginControlPanel.UpdateCallbackInForm(methodName = "onUpdateLinkWithResidencePlugin")
	public boolean linkWithResidencePlugin;

	@Default(intValue = 1)
	@PluginControlPanel.PresentInForm(lang = cp_link_with_land, conditionCallback = "ccLinkWithLandPlugin")
	@PluginControlPanel.UpdateCallbackInForm(methodName = "onUpdateLinkWithLandPlugin")
	public boolean linkWithLandPlugin;

	@Default(intValue = 1)
	@PluginControlPanel.PresentInForm(lang = cp_create_in_residence_only, conditionCallback = "ccOnlyCreateShopInResidenceArea")
	public boolean onlyCreateShopInResidenceArea;

	@Default(intValue = 0)
	@PluginControlPanel.PresentInForm(lang = cp_op_ignore_build_permission, conditionCallback = "ccOperatorIgnoreBuildPermission")
	public boolean operatorIgnoreBuildPermission;

	@Default(intValue = 1)
	@PluginControlPanel.PresentInForm(lang = cp_hopper_limit, conditionCallback = "ccLimitHopper")
	public boolean limitHopper;

	@Default(intValue = 0)
	@PluginControlPanel.PresentInForm(lang = cp_use_translated_item_name)
	public boolean useItemNameTranslations;

	public PluginConfig(File file) throws FileNotFoundException
	{
		super(file, false);

		reload();

		if(!exists())
			save(true); // 保存默认值
	}

	@Override
	public void Save()
	{
		if(!isFileLoaded())
			loadFile();

		config.getRootSection().clear();

		for(Field field : getClass().getDeclaredFields())
		{
			if(field.isAnnotationPresent(Default.class))
			{
				int defaultInteger = field.getAnnotation(Default.class).intValue();
				String defaultString = field.getAnnotation(Default.class).stringValue();

				if(defaultInteger==Integer.MIN_VALUE && defaultString.isEmpty()) continue;

				String fieldName = formatFieldName(field.getName());

				try {

					if(field.getType().isEnum())
					{
						config.set(fieldName, field.get(this).toString());
					}else{
						config.set(fieldName, field.get(this));
					}

				} catch (IllegalAccessException e) {e.printStackTrace();}
			}
		}

		config.save();
	}

	@Override
	public void Reload()
	{
		if(!tryToLoad())
		{
			if(exists())
				config.reload();
		}

		for(Field field : getClass().getDeclaredFields())
		{
			if(field.isAnnotationPresent(Default.class))
			{
				int defaultInteger = field.getAnnotation(Default.class).intValue();
				String defaultString = field.getAnnotation(Default.class).stringValue();

				if(defaultInteger==Integer.MIN_VALUE && defaultString.isEmpty()) continue;

				String fieldName = formatFieldName(field.getName());

				try {

					if(field.getType()==int.class)
					{
						int defaultValue = defaultInteger;

						field.setInt(this, config.getInt(fieldName, defaultValue));
 					}

					if(field.getType()==boolean.class)
					{
						boolean defaultValue = defaultInteger!=0;
						field.setBoolean(this, config.getBoolean(fieldName, defaultValue));
					}

					if(field.getType()==String.class)
					{
						field.set(this, config.getString(fieldName, defaultString));
					}

					if(field.getType().isEnum())
					{
						int defaultIndex = Math.min(field.getType().getEnumConstants().length-1, defaultInteger);

						String configText = config.getString(fieldName, field.getType().getEnumConstants()[defaultIndex].toString());

						boolean found = false;

						for (Object v : field.getType().getEnumConstants())
						{
							InteractionWay object = (InteractionWay) v;

							if(configText.equals(object.toString()))
							{
								field.set(this, object);
								found = true;
							}
						}

						if(!found)
						{
							field.set(this, field.getType().getEnumConstants()[defaultIndex]);
						}

					}

				} catch (IllegalAccessException e) {e.printStackTrace();}

			}
		}

	}

	private static String formatFieldName(String string)
	{
		String[] ss = string.split("(?=[A-Z])");

		for(int i = 0;i<ss.length;i++)
		{
			ss[i] = ss[i].toLowerCase();
		}

		String temp = "";

		for(int i = 0;i<ss.length;i++)
		{
			temp += ss[i];
			if(i!=ss.length-1)  temp += '-';
		}

		return temp;
	}

	private static String unformatFieldName(String string)
	{
		String[] ss = string.split("\\-");

		for(int i = 0;i<ss.length;i++)
		{
			if(i==0) continue;
			ss[i] = ss[i].substring(0, 1).toUpperCase() + ss[i].substring(1);
		}

		String temp = "";

		for(int i = 0;i<ss.length;i++)
		{
			temp += ss[i];
		}

		return temp;
	}

	public void updateHologramItemEffect(Integer newValue, Integer oldValue, Player player)
	{
		if(newValue>0)
			PepperShop.ins.hologramListener.addAllItemEntityForAllPlayer();

		if(newValue<=0)
			PepperShop.ins.hologramListener.removeAllItemEntityForAllPlayer();
	}

	public void onUpdateLinkWithResidencePlugin(Boolean newValue, Boolean oldValue, Player player)
	{
		if(newValue)
			PepperShop.scheduler.scheduleDelayedTask(new PluginTask<PepperShop>(PepperShop.ins)
			{
				@Override
				public void onRun(int i)
				{
					player.showFormWindow(new PluginControlPanel());
				}
			}, 15);
	}

	public void onUpdateLinkWithLandPlugin(Boolean newValue, Boolean oldValue, Player player)
	{
		if(newValue)
			PepperShop.scheduler.scheduleDelayedTask(new PluginTask<PepperShop>(PepperShop.ins)
			{
				@Override
				public void onRun(int i)
				{
					player.showFormWindow(new PluginControlPanel());
				}
			}, 15);
	}


	public boolean ccLinkWithResidencePlugin()
	{
		return PepperShop.ins.ResidencePluginLoaded;
	}

	public boolean ccLinkWithLandPlugin()
	{
		return PepperShop.ins.LandPluginLoaded;
	}

	public boolean ccOnlyCreateShopInResidenceArea()
	{
		return linkWithResidencePlugin || linkWithLandPlugin;
	}

	public boolean ccOperatorIgnoreBuildPermission()
	{
		return linkWithResidencePlugin || linkWithLandPlugin;
	}

	public boolean ccLimitHopper()
	{
		return linkWithResidencePlugin || linkWithLandPlugin;
	}

	public enum InteractionWay
	{
		ChatBar("ChatBar"),
		Both("Both"),
		Interface("Interface");
		
		private String text;
		
		InteractionWay(String prettyText)
		{
			this.text = prettyText;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Default
	{
		String stringValue() default "";
		int intValue() default Integer.MIN_VALUE;
		int min() default -1; // the min value of 'integer'
		int max() default -1; // the max value of 'integer'
		int step() default 1; // the each step-length of 'integer'
	}

}
