package cn.hyperrain.peppershop.listener;

import cn.hyperrain.peppershop.form.FormResponse;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;

public class FormResponseListener implements Listener 
{
	@EventHandler()
	public void onFormResponse(PlayerFormRespondedEvent event) 
	{
		if (!(event.getWindow() instanceof FormResponse))
			return;

		if (event.getResponse() == null)
		{
			((FormResponse)event.getWindow()).onFormClose(event);
		} else {
			((FormResponse)event.getWindow()).onFormResponse(event);
		}


	}
	
}
