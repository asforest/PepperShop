package cn.innc11.peppershop.shop;

import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.utils.Quick;

public enum ShopType 
{
	BUY,
	SELL;
	
	@Override
	public String toString() 
	{
		return Quick.t(LangNodes.valueOf(this.name().toLowerCase()));
	}
}
