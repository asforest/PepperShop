package cn.innc11.peppershop.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import cn.innc11.peppershop.PepperShop;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;

public abstract class BaseConfig
{
	public Config config;
	protected File file;
	protected boolean synchronousSave = false;

	private boolean modified = false;
	private boolean saving = false;
	private PluginTask<PepperShop> saveTask;

	public BaseConfig(File file, boolean readonly)
	{
		this(file, readonly, false);
	}

	public BaseConfig(File file, boolean readonly, boolean synchronousSave)
	{
		this.file = file;
		this.synchronousSave = synchronousSave;
		this.config = new Config(Config.YAML);

		if(!readonly)
			createSaveTask();
	}

	private void createSaveTask()
	{
		saveTask = new PluginTask<PepperShop>(PepperShop.ins)
		{
			@Override
			public void onRun(int currentTicks)
			{
				while(modified)
				{
					modified = false;

					Save();
				}

				saving = false;
			}
		};
	}

	public boolean exists()
	{
		return file.exists();
	}

	public String getCompleteFileName()
	{
		return file.getName();
	}

	public String getFileNameWithoutSuffix()
	{
		return getCompleteFileName().replaceAll("\\.\\w+$", "");
	}

	protected void loadFile()
	{
		config.load(file.getAbsolutePath(), Config.YAML);
	}

	protected boolean isFileLoaded()
	{
		File File = null;
		try{
			Field fileField = config.getClass().getDeclaredField("file");
			fileField.setAccessible(true);
			File = (java.io.File) fileField.get(config);
		}catch(NoSuchFieldException | IllegalAccessException e){
			e.printStackTrace();
		}

		return File!=null;
	}

	protected boolean tryToLoad()
	{
		if(exists() && !isFileLoaded())
		{
			loadFile();
			return true;
		}

		return false;
	}

	public void checkFileExistence() throws FileNotFoundException
	{
		if(!exists())
			throw new FileNotFoundException(String.format("File %s not found!", file.getAbsolutePath()));
	}

	public final void save()
	{
		save(false);
	}

	public final void save(boolean immediately)
	{
		if(this.synchronousSave || immediately)
		{
			modified = true;

			saveTask.onRun(0);
		}else{
			if(saveTask!=null)
			{
				modified = true;

				if(!saving)
				{
					saving = true;

					PepperShop.server.getScheduler().scheduleTask(PepperShop.ins, saveTask, true);
				}
			}
		}

	}

	public final void reload()
	{
		Reload();
	}

	protected void Save()
	{

	}

	protected void Reload()
	{

	}

}
