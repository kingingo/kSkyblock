package eu.epicpvp.kSkyblock.World.Party;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import eu.epicpvp.kSkyblock.World.Events.IslandAccessEvent;
import eu.epicpvp.kcore.Listener.kListener;

public class PartyListener extends kListener {
	
	private HashMap<Integer,IslandParty> parties;

	public PartyListener(JavaPlugin instance) {
		super(instance, "PartyListener");
		this.parties=new HashMap<>();
	}

	@EventHandler
	public void access(IslandAccessEvent ev){
		
	}
}
