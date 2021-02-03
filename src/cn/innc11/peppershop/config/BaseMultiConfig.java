package cn.innc11.peppershop.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class BaseMultiConfig
{
	protected File baseDir;
	protected HashMap<String, BaseConfig> contents = new HashMap<>();
	protected Class cls;

	protected BaseMultiConfig(File dir, Class<? extends BaseConfig> cls)
	{
		this.baseDir = dir;
		this.cls = cls;

		baseDir.mkdirs();
	}

	private File[] getAllFiles()
	{
		return baseDir.listFiles((dir, name)->name.matches(getFilenameFilterRegex(".yml")));
	}

	protected abstract String getFilenameFilterRegex(String suffix);

	private BaseConfig newInstance(Object parameter)
	{
		try
		{
			return (BaseConfig) cls.getDeclaredConstructor(File.class).newInstance(parameter);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}


	protected void reloadAll()
	{
		contents.clear();

		for (File f : getAllFiles())
		{
			String key = f.getName().replaceAll("\\.yml$", "");
			contents.put(key, newInstance(f));
		}
	}

	protected void saveAll()
	{
		for(BaseConfig bc : contents.values())
		{
			bc.save();
		}
	}

	protected boolean reload(String fileNameWithoutSuffix) throws FileNotFoundException
	{
		if(contents.containsKey(fileNameWithoutSuffix))
		{
			contents.get(fileNameWithoutSuffix).reload();
			return true;
		}

		return false;
	}

	protected boolean save(String fileNameWithoutSuffix)
	{
		if(contents.containsKey(fileNameWithoutSuffix))
		{
			contents.get(fileNameWithoutSuffix).save();
			return true;
		}

		return false;
	}

	private File getFileByFileNameWithoutSuffix(String filename, boolean nullable)
	{
		File f = new File(baseDir, filename+".yml");

		if(f.exists() || !nullable)
		{
			return f;
		}

		return null;
	}

	protected BaseConfig getBaseConfigByFileName(String fileNameWithoutSuffix, boolean create)
	{
		BaseConfig bc = contents.get(fileNameWithoutSuffix);

		if(bc==null && create)
		{
			bc = addBaseConfigByFileName(fileNameWithoutSuffix, false);
		}

		return bc;
	}

	protected BaseConfig addBaseConfigByFileName(String fileNameWithoutSuffix, boolean update)
	{
		if(contents.containsKey(fileNameWithoutSuffix) && !update)
		{
			return null;
		}else{
			BaseConfig bc = newInstance(getFileByFileNameWithoutSuffix(fileNameWithoutSuffix, false));
			contents.put(fileNameWithoutSuffix, bc);
			return bc;
		}

	}

}
