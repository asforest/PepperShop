package cn.innc11.peppershop.listener;

import cn.innc11.peppershop.utils.Pair;

public interface ShopInteractionTimer 
{
	// return null: not found the player
	// return pair: <isVaild, shop>
	public Pair<Boolean, ?> isValidInteraction(String player);
	
}
