package eu.epicpvp.kSkyblock.World.Island;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.Getter;

public enum IslandPermission {
PICKUP_ITEMS(11,UtilItem.Item(new ItemStack(Material.HOPPER), new String[]{" ","§7Mitglieder deiner Insel können","§7Gegenstände aufsammeln."," "}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Gegenstände aufsammeln"),"island.item.pickup"),
KILL_MOBS(13,UtilItem.Item(new ItemStack(Material.LEATHER),new String[]{" ","§7Mitglieder deiner Insel","§7können Tiere oder Monster","§7töten."," "},"§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Tiere töten"), "island.kill.mobs"),
INTERACT(15,UtilItem.Item(new ItemStack(Material.REDSTONE), new String[]{" ","§7Spieler können mit Türen, Schaltern,","§7Buttons etc interagieren."," "}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Interagieren"),"island.interact"),
USE_CHEST(29,UtilItem.Item(new ItemStack(Material.CHEST), new String[]{" ","§7Mitglieder deiner Insel","§7können in deine Kisten schauen","§7und Items hinzufügen oder","§7entwenden."," "}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Truhen öffnen"),"island.use.chest"),
TELEPORT(31,UtilItem.Item(new ItemStack(Material.ENDER_PEARL), new String[]{" ","§7Mitglieder deiner Insel","§7können sich in deiner Abwesendheit","§7zu dieser teleportieren."," "}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Teleportieren"),"island.teleport"),
BUILD(33,UtilItem.Item(new ItemStack(Material.IRON_PICKAXE), new String[]{" ","§7Mitglieder deiner Insel","§7können auf deiner Insel Blöcke","§7setzen und abbauen."," "}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Bauen"),"island.build");
	
	@Getter
	private ItemStack item;
	@Getter
	private String permission;
	@Getter
	private int slot;
	
	private IslandPermission(int slot,ItemStack item,String permission){
		this.item=item;
		this.slot=slot;
		this.permission=permission;
	}
	
	public static IslandPermission of(String permission){
		for(IslandPermission i : values())
			if(i.getPermission().equalsIgnoreCase(permission))return i;
		
		return null;
	}
}
