package cn.innc11.peppershop.shop;

import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.PepperShop;

public enum ShopType 
{
	BUY,
	SELL;
	
	@Override
	public String toString() 
	{
		return PepperShop.ins.localization.get(LangNodes.valueOf(this.name().toLowerCase()));
	}
}
