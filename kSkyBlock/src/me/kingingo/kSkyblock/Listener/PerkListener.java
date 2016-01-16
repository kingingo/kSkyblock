package me.kingingo.kSkyblock.Listener;

import me.kingingo.kcore.Kit.PerkManager;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.UserDataConfig.Events.UserDataConfigLoadEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class PerkListener extends kListener{
	
	public PerkManager perkManager;
	
	public PerkListener(PerkManager perkManager){
		super(perkManager.getInstance(),"PerkListener");
		this.perkManager=perkManager;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void load(UserDataConfigLoadEvent ev){
		perkManager.configPlayer(ev.getPlayer());
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		perkManager.removePlayer(ev.getPlayer());
	}
	
}
