package me.kingingo.kSkyblock.Listener.Holiday;

import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilParticle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ChristmasListener extends kListener{

	public ChristmasListener(kSkyBlock skyblock) {
		super(skyblock, "ChrismasListener");
		Log("Enabled");
	}

	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FAST)return;
		try {
			for(Player player : Bukkit.getWorld("world").getPlayers())
					UtilParticle.FIREWORKS_SPARK.sendToPlayer(player, player.getLocation(), 10F, 4F, 10F, 0, 60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
