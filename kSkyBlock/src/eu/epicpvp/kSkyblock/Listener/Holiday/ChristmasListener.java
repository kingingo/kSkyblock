package eu.epicpvp.kSkyblock.Listener.Holiday;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import eu.epicpvp.kSkyblock.kSkyBlock;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilParticle;

public class ChristmasListener extends kListener{

	public ChristmasListener(kSkyBlock skyblock) {
		super(skyblock, "ChrismasListener");
		logMessage("Enabled");
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
