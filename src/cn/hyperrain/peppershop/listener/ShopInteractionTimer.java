package cn.hyperrain.peppershop.listener;

import cn.hyperrain.peppershop.utils.Pair;

public interface ShopInteractionTimer 
{
	// return null: not found the player
	// return pair: <isVaild, shop>
	public Pair<Boolean, ?> isValidInteraction(String player);
	
}
