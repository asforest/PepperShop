package cn.innc11.peppershop.form;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.stroage.PluginConfig;
import cn.innc11.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.window.FormWindowCustom;

import java.lang.annotation.Annotation;
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
				"PLUGIN_NAME",  PepperShop.ins.getDescription().getName(),
				"CONFIG_VERSION", PepperShop.ins.pluginConfig.version +"",
				"PLUGIN_VERSION", PepperShop.ins.getDescription().getVersion()));

		convertToForm(PepperShop.ins.pluginConfig);
	}

	@Override
	public void onFormResponse(PlayerFormRespondedEvent e) 
	{
		Player player = e.getPlayer();

		if(!player.isOp())
			return;

		try {
			player.sendMessage(Quick.t(LangNodes.pm_configure_updated));
			convertFromForm(PepperShop.ins.pluginConfig, e);
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


	private void convertToForm(Object obj)
	{
		Field[] allFields = obj.getClass().getDeclaredFields();

		for(int i=0;i<allFields.length;i++)
		{
			Field field = allFields[i];
			String fieldName = field.getName();

			if(field.isAnnotationPresent(PluginConfig.Default.class) && field.isAnnotationPresent(PresentInForm.class))
			{
				int defaultInt = field.getAnnotation(PluginConfig.Default.class).intValue();
				String defaultString = field.getAnnotation(PluginConfig.Default.class).stringValue();

				String conditionCallback = field.getAnnotation(PresentInForm.class).conditionCallback();

				boolean skip = true;

				if(!conditionCallback.isEmpty())
				{
					try {
						skip = (boolean) field.getDeclaringClass().getDeclaredMethod(conditionCallback).invoke(obj);
					}catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e){ e.printStackTrace(); }
				}

				if(!skip || (defaultInt==Integer.MIN_VALUE && defaultString.isEmpty()))
					continue;

				Object instanceValue_ = null;

				try {
					instanceValue_ = obj.getClass().getDeclaredField(fieldName).get(obj);
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

	private void convertFromForm(Object obj, PlayerFormRespondedEvent event) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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

				Object instanceValue = obj.getClass().getDeclaredField(fieldName).get(obj);

				if((defaultInt==Integer.MIN_VALUE && defaultString.isEmpty()))
					continue;

				if (field.getType() == int.class)
				{
					int response = (int) getResponse().getSliderResponse(elementIndex);
					obj.getClass().getDeclaredField(fieldName).set(obj, response);
					elementIndex ++;
				}

				if (field.getType() == boolean.class)
				{
					boolean response = getResponse().getToggleResponse(elementIndex);
					obj.getClass().getDeclaredField(fieldName).set(obj, response);
					elementIndex ++;
				}

				if (field.getType().isEnum())
				{
					int response = getResponse().getDropdownResponse(elementIndex).getElementID();
					obj.getClass().getDeclaredField(fieldName).set(obj, field.getType().getEnumConstants()[response]);
					elementIndex ++;
				}

				if(field.getType()==String.class)
				{
					String response = getResponse().getInputResponse(elementIndex);
					obj.getClass().getDeclaredField(fieldName).set(obj, response);
					elementIndex ++;
				}

				if (field.isAnnotationPresent(UpdateCallbackInForm.class))
				{
					Class parameterType = instanceValue.getClass();
					String methodName = field.getAnnotation(UpdateCallbackInForm.class).methodName();
					Object oldValue = instanceValue;
					Object newValue = obj.getClass().getDeclaredField(fieldName).get(obj);

					if(!oldValue.equals(newValue))
						field.getDeclaringClass().getDeclaredMethod(methodName, parameterType, parameterType, Player.class).invoke(obj, newValue, oldValue, event.getPlayer());
				}

			}

		}
	}

}
