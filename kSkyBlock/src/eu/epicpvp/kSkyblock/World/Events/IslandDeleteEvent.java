package eu.epicpvp.kSkyblock.World.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.kSkyblock.World.Island.Island;
import lombok.Getter;

public class IslandDeleteEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Island island;
	
	public IslandDeleteEvent(Island island){
		this.island=island;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
