package cn.hyperrain.peppershop.shop;

import cn.hyperrain.peppershop.localization.LangNodes;
import cn.hyperrain.peppershop.PepperShop;

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
