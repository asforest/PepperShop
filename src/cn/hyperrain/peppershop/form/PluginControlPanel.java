package cn.hyperrain.peppershop.form;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.localization.LangNodes;
import cn.hyperrain.peppershop.stroage.PluginConfig;
import cn.hyperrain.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.window.FormWindowCustom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PluginControlPanel extends FormWindowCustom implements FormResponse
{
	ArrayList<Integer> showedFields = new ArrayList<>();

	public PluginControlPanel() 
	{
		super(Quick.t(LangNodes.cp_title,
				"{PLUGIN_NAME}",  PepperShop.ins.getDescription().getName(),
				"{CONFIG_VERSION}", PepperShop.ins.pluginConfig.version +"",
				"{PLUGIN_VERSION}", PepperShop.ins.getDescription().getVersion()));
		
		PluginConfig pc = PepperShop.ins.pluginConfig;

		Field[] allFields = PluginConfig.class.getDeclaredFields();

		for(int i=0;i<allFields.length;i++)
		{
			Field field = allFields[i];
			String fieldName = field.getName();

			if(field.isAnnotationPresent(PluginConfig.Default.class) && field.isAnnotationPresent(PresentInForm.class))
			{
				int defaultInt = field.getAnnotation(PluginConfig.Default.class).intValue();
				String defaultString = field.getAnnotation(PluginConfig.Default.class).stringValue();

				String conditionCallback = field.getAnnotation(PresentInForm.class).conditionCallback();

				boolean pass = true;

				if(!conditionCallback.isEmpty())
				{
					try {
						pass = (boolean) field.getDeclaringClass().getDeclaredMethod(conditionCallback).invoke(pc);
					}catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e){ e.printStackTrace(); }
				}

				if(!pass || (defaultInt==Integer.MIN_VALUE && defaultString.isEmpty()))
					continue;

				Object instanceValue_ = null;

				try {
					instanceValue_ = pc.getClass().getDeclaredField(fieldName).get(pc);
				} catch (Exception e) {e.printStackTrace();}

				String label = Quick.t(field.getAnnotation(PresentInForm.class).lang());

				if(field.getType()==int.class)
				{
					int instanceValue = (int) instanceValue_;

					int min = field.getAnnotation(PluginConfig.Default.class).min();
					int max = field.getAnnotation(PluginConfig.Default.class).max();
					int step = field.getAnnotation(PluginConfig.Default.class).step();

					if(min<0 || max<0 || min>max) continue;

					addElement(new ElementSlider(label, min, max, step, Math.min(max, Math.max(min, instanceValue))));
					showedFields.add(i);
				}

				if(field.getType()==boolean.class)
				{
					boolean instanceValue = (boolean) instanceValue_;

					addElement(new ElementToggle(label, instanceValue));
					showedFields.add(i);
				}

				if(field.getType()==String.class)
				{
					String instanceValue = (String) instanceValue_;

					addElement(new ElementInput(label, "", instanceValue));
					showedFields.add(i);
				}

				if(field.getType().isEnum())
				{
					int instanceIndex = 0;
					for(Object p : field.getType().getEnumConstants())
					{
						if(instanceValue_==p) break;
						instanceIndex++;
					}

					ArrayList<String> list = new ArrayList<>();
					for(Object p : field.getType().getEnumConstants())
					{
						list.add(p.toString());
					}

					addElement(new ElementDropdown(label, list, instanceIndex));
					showedFields.add(i);
				}

			}
		}

	}

	@Override
	public void onFormResponse(PlayerFormRespondedEvent e) 
	{
		Player player = e.getPlayer();

		if(!player.isOp())
			return;

		try {
			player.sendMessage(Quick.t(LangNodes.pm_configure_updated));

			PluginConfig pc = PepperShop.ins.pluginConfig;
			Field[] allFields = PluginConfig.class.getDeclaredFields();

			int elementIndex = 0;

			for(int i=0;i<allFields.length;i++)
			{
				if(!showedFields.contains(i)) continue;

				Field field = allFields[i];
				String fieldName = field.getName();

				if (field.isAnnotationPresent(PluginConfig.Default.class) && field.isAnnotationPresent(PresentInForm.class))
				{
					int defaultInt = field.getAnnotation(PluginConfig.Default.class).intValue();
					String defaultString = field.getAnnotation(PluginConfig.Default.class).stringValue();

					Object instanceValue = pc.getClass().getDeclaredField(fieldName).get(pc);

					if((defaultInt==Integer.MIN_VALUE && defaultString.isEmpty()))
						continue;

					if (field.getType() == int.class)
					{
						int response = (int) getResponse().getSliderResponse(elementIndex);
						pc.getClass().getDeclaredField(fieldName).set(pc, response);
						elementIndex ++;
					}

					if (field.getType() == boolean.class)
					{
						boolean response = getResponse().getToggleResponse(elementIndex);
						pc.getClass().getDeclaredField(fieldName).set(pc, response);
						elementIndex ++;
					}

					if (field.getType().isEnum())
					{
						int response = getResponse().getDropdownResponse(elementIndex).getElementID();
						pc.getClass().getDeclaredField(fieldName).set(pc, field.getType().getEnumConstants()[response]);
						elementIndex ++;
					}

					if(field.getType()==String.class)
					{
						String response = getResponse().getInputResponse(elementIndex);
						pc.getClass().getDeclaredField(fieldName).set(pc, response);
						elementIndex ++;
					}

					if (field.isAnnotationPresent(UpdateCallbackInForm.class))
					{
						Class parameterType = instanceValue.getClass();
						String methodName = field.getAnnotation(UpdateCallbackInForm.class).methodName();
						Object oldValue = instanceValue;
						Object newValue = pc.getClass().getDeclaredField(fieldName).get(pc);

						if(!oldValue.equals(newValue))
							field.getDeclaringClass().getDeclaredMethod(methodName, parameterType, parameterType, Player.class).invoke(pc, newValue, oldValue, player);
					}

				}

			}

		}catch (Exception ex){ex.printStackTrace();}

		PepperShop.ins.pluginConfig.save();
	}

	@Override
	public void onFormClose(PlayerFormRespondedEvent e)
	{

	}


	@Target({ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PresentInForm
	{
		LangNodes lang();
		String conditionCallback() default "";
	}

	@Target({ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UpdateCallbackInForm
	{
		String methodName();
	}

}
