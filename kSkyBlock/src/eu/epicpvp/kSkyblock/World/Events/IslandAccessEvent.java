package eu.epicpvp.kSkyblock.World.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import lombok.Getter;
import lombok.Setter;

public class IslandAccessEvent  extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private SkyBlockWorld world;
	@Getter
	private Player player;
	@Getter
	private Location location;
	@Getter
	@Setter
	private boolean cancelled=true;
	@Getter
	private Event event;
	
	public IslandAccessEvent(SkyBlockWorld world,Player player,Location location,Event event){
		this.world=world;
		this.player=player;
		this.location=location;
		this.event=event;
	}
	

	public IslandAccessEvent(SkyBlockWorld world,Player player,Event event){
		this(world,player,player.getLocation(),event);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
