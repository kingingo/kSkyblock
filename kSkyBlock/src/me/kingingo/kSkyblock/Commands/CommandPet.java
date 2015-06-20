package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kSkyblock.PetShop;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Pet.PetManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CommandPet extends kListener implements CommandExecutor{
	
	@Getter
	private Player player;
	private PetManager manager;
	private PetShop shop;
	
	public CommandPet(PetManager manager,PetShop shop){
		super(manager.getInstance(),"CommandPet");
		this.manager=manager;
		this.shop=shop;
	}

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "pet",alias={"petshop"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		player = (Player)cs;
		if(args.length==0){
			player.openInventory(shop.getMain());
		}
		return false;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Create(CreatureSpawnEvent ev){
		if(ev.getLocation().getWorld().getName().equalsIgnoreCase("world")){
			if(ev.isCancelled()&&ev.getEntity() instanceof Creature&&ev.getSpawnReason()==SpawnReason.DEFAULT&&ev.getSpawnReason()==SpawnReason.DEFAULT){
				ev.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void Entity(EntityDamageByEntityEvent ev){
		if(ev.getDamager() instanceof Player){
			if(manager.getActivePetOwners().containsKey( ((Player)ev.getDamager()).getName().toLowerCase() )&&manager.GetPet( ((Player)ev.getDamager()) ).getEntityId() == ev.getEntity().getEntityId())ev.setCancelled(true);
		}
	}
	
}