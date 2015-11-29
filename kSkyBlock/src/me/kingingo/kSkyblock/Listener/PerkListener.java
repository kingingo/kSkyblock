package me.kingingo.kSkyblock.Listener;

import me.kingingo.kcore.Kit.PerkManager;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PerkListener extends kListener{
	
	public PerkManager perkManager;
	
	public PerkListener(PerkManager perkManager){
		super(perkManager.getInstance(),"PerkListener");
		this.perkManager=perkManager;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Join(PlayerJoinEvent ev){
		perkManager.configPlayer(ev.getPlayer());
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		perkManager.removePlayer(ev.getPlayer());
	}
	
}
