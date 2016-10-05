package eu.epicpvp.kSkyblock.World;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.Events.IslandAccessEvent;
import eu.epicpvp.kcore.Listener.kListener;
import lombok.Getter;

public class SkyBlockWorldListener extends kListener{

	@Getter
	private HashMap<UUID,SkyBlockWorld> worlds;
	@Getter
	private SkyBlockManager manager;
	
	public SkyBlockWorldListener(SkyBlockManager manager) {
		super(manager.getInstance(), "SkyBlockWorldListener");
		this.worlds=new HashMap<>();
		this.manager=manager;
	}
	
	@EventHandler
	public void PlayerInteractEntity(PlayerInteractEntityEvent ev){
		callIslandAccessEvent(ev.getPlayer(), (ev.getRightClicked()==null?ev.getPlayer().getLocation():ev.getRightClicked().getLocation()), ev);
	}

	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent ev) {
		if (getWorlds().containsKey(ev.getPlayer().getWorld().getUID()) && !ev.isCancelled() && !ev.getPlayer().isOp()) {
			ev.setCancelled(callIslandAccessEvent(ev.getPlayer(),ev.getBlockClicked().getLocation(),ev));
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent ev) {
		if (getWorlds().containsKey(ev.getPlayer().getWorld().getUID()) && !ev.isCancelled() && !ev.getPlayer().isOp()) {
			ev.setCancelled(callIslandAccessEvent(ev.getPlayer(),ev.getBlockClicked().getLocation(),ev));
		}
	}

	@EventHandler
	public void Damage(EntityDamageEvent ev) {
		if (getWorlds().containsKey(ev.getEntity().getWorld().getUID())) {
			if (ev.getEntity() instanceof Player && ev.getCause() == DamageCause.FALL && ev.getCause() == DamageCause.LAVA) {
				ev.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void Damage(EntityDamageByEntityEvent ev) {
		if (getWorlds().containsKey(ev.getDamager().getWorld().getUID())) {
			if (ev.getDamager() instanceof Player && ev.getEntity() instanceof Player) {
				ev.setCancelled(true);
			} else if (ev.getDamager() instanceof Projectile && ev.getEntity() instanceof Player) {
				ev.setCancelled(true);
			} else if (ev.getDamager() instanceof Projectile && ev.getEntity() instanceof Creature && ((Projectile) ev.getDamager()).getShooter() instanceof Player) {
				ev.setCancelled(callIslandAccessEvent(((Player) ((Projectile) ev.getDamager()).getShooter()),ev.getEntity().getLocation(),ev));
			} else if (ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature) {
				ev.setCancelled(callIslandAccessEvent(((Player) ev.getDamager()),ev.getEntity().getLocation(),ev));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryOpenEvent(InventoryOpenEvent ev) {
		if (!getWorlds().containsKey(ev.getPlayer().getWorld().getUID()) || (ev.isCancelled()) || (ev.getInventory() == null)
				|| (ev.getInventory().getHolder() == null) || ev.getPlayer().isOp())
			return;

		if (((ev.getInventory().getHolder() instanceof Hopper))
				|| ((ev.getInventory().getHolder() instanceof BrewingStand))
				|| ((ev.getInventory().getHolder() instanceof Chest))
				|| ((ev.getInventory().getHolder() instanceof DoubleChest))
				|| ((ev.getInventory().getHolder() instanceof Furnace))
				|| ((ev.getInventory().getHolder() instanceof Dispenser))) {
			Player player = (Player) ev.getPlayer();
			Location loc = player.getLocation();

			if ((ev.getInventory().getHolder() instanceof Chest)) {
				loc = ((Chest) ev.getInventory().getHolder()).getLocation();
			} else if ((ev.getInventory().getHolder() instanceof Furnace)) {
				loc = ((Furnace) ev.getInventory().getHolder()).getLocation();
			} else if ((ev.getInventory().getHolder() instanceof DoubleChest)) {
				loc = ((DoubleChest) ev.getInventory().getHolder()).getLocation();
			} else if ((ev.getInventory().getHolder() instanceof Dispenser)) {
				loc = ((Dispenser) ev.getInventory().getHolder()).getLocation();
			} else if ((ev.getInventory().getHolder() instanceof BrewingStand)) {
				loc = ((BrewingStand) ev.getInventory().getHolder()).getLocation();
			} else if ((ev.getInventory().getHolder() instanceof Hopper)) {
				loc = ((Hopper) ev.getInventory().getHolder()).getLocation();
			}
			ev.setCancelled(callIslandAccessEvent(player,loc,ev));
		}
	}

	@EventHandler
	public void PickUp(PlayerPickupItemEvent ev) {
		if (getWorlds().containsKey(ev.getPlayer().getWorld().getUID()) && !ev.isCancelled() && !ev.getPlayer().isOp()) {
			ev.setCancelled(callIslandAccessEvent(ev.getPlayer(),ev));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev) {
		if (getWorlds().containsKey(ev.getPlayer().getWorld().getUID()) && !ev.isCancelled() && !ev.getPlayer().isOp()) {
			if (ev.getBlock() == null) return;
			ev.setCancelled(callIslandAccessEvent(ev.getPlayer(),ev));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void interact(PlayerInteractEvent ev) {
		if (getWorlds().containsKey(ev.getPlayer().getWorld().getUID()) && !ev.isCancelled() && !ev.getPlayer().isOp()) {
			if (ev.getPlayer().getItemInHand() != null) {
				ev.setCancelled(callIslandAccessEvent(ev.getPlayer(),ev));
			}
		}
	}

	@EventHandler
	public void PotionSplash(PotionSplashEvent ev) {
		if(getWorlds().containsKey(ev.getPotion().getLocation().getWorld().getUID()) && !ev.isCancelled()) {
			if (ev.getPotion().getShooter() instanceof Player) {
				if (!((Player) ev.getPotion().getShooter()).isOp()) {
					ev.setCancelled(callIslandAccessEvent(((Player) ev.getPotion().getShooter()), ev.getPotion().getLocation(),ev));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev) {
		if (getWorlds().containsKey(ev.getPlayer().getWorld().getUID()) && !ev.isCancelled() && !ev.getPlayer().isOp()) {
			if (ev.getBlock() == null) return;
			ev.setCancelled(callIslandAccessEvent(ev.getPlayer(),ev));
		}
	}

	public boolean callIslandAccessEvent(Player player,Event event){
		return callIslandAccessEvent(player, player.getLocation(),event);
	}
	
	public boolean callIslandAccessEvent(Player player, Location location,Event event){
		if(!getWorlds().containsKey(location.getWorld().getUID()))return true;
		IslandAccessEvent access = new IslandAccessEvent(getWorlds().get(location.getWorld().getUID()), player,location,event);
		Bukkit.getPluginManager().callEvent(access);
		return access.isCancelled();
	}
	
}
