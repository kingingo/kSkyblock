package me.kingingo.kSkyblock.World;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import me.kingingo.kSkyblock.SkyBlockManager;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.MySQL.MySQLErr;
import me.kingingo.kcore.MySQL.Events.MySQLErrorEvent;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.InventoryHolder;

public class SkyBlockWorld extends kListener{

	@Getter
	private SkyBlockManager manager;
	@Getter
	private World world;
	private HashMap<String,Location> islands = new HashMap<>();
	private int X=0;
	private int Z=0;
	private int radius;
	@Getter
	private String schematic;
	int creature_limit;
	
	public SkyBlockWorld(SkyBlockManager manager,String schematic,World world,int radius,int anzahl,int creature_limit) {
		super(manager.getInstance(),"[SkyBlockWorld:"+world.getName()+"]");
		this.manager=manager;
		this.world=world;
		this.creature_limit=creature_limit;
		this.schematic=schematic;
		this.radius=radius;
		loadIslands();
		addIslands(anzahl);
		Log(islands.size()+" Inseln wurden Geladen!");
	}
	
	@EventHandler
	public void CreatureSpawn(CreatureSpawnEvent ev){
		for(String player : islands.keySet()){
			if(player.charAt(0)!='!'){
				if(isInIsland(player,ev.getLocation())){
					int a = 0;
					for(Entity e : world.getEntities()){
						if(!(e instanceof Player)){
							if(isInIsland(player,e.getLocation()))a++;
						}
					}
					if(a>=creature_limit)ev.setCancelled(true);
					break;
				}
			}
		}
	}
	
	public void loadIslandPlayer(Player player){
		loadIslandPlayer(player.getName());
	}
	
	public boolean haveIsland(Player player){
		return haveIsland(player.getName());
	}
	
	public boolean haveIsland(String player){
		player=player.toLowerCase();
		if(islands.containsKey(player))return true;
		return false;
	}
	
	public void loadIslandPlayer(String player){
		player=player.toLowerCase();
		if(!islands.containsKey(player)){
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `player`,`X`,`Z` FROM `list_skyblock_worlds` WHERE worldName='"+world.getName().toLowerCase()+"' AND player='"+player+"'");
		      while (rs.next()) {
		    	islands.put(rs.getString(1).toLowerCase(), new Location(world,rs.getInt(2),0,rs.getInt(3)));
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
		}
	}
	
	public void addIslands(int anzahl){
		if(recycelnIslandAnzahl()<anzahl){
			String u;
			for(int i = 0; i<anzahl;i++){
				u=UUID.randomUUID().toString();
				addIsland("!"+u,u,schematic,false);
			}
		}
	}
	
	public Location getIslandFixHome(Player player){
		Location loc = getIslandHome(player.getName());
		loc.getBlock().setType(Material.AIR);
		loc.clone().add(0,-1,0).getBlock().setType(Material.AIR);
		loc.clone().add(0,-2,0).getBlock().setType(Material.GLASS);
		return loc;
	}
	
	public Location getIslandHome(Player player){
		return getIslandHome(player.getName());
	}
	
	public Location getIslandHome(String player){
		player=player.toLowerCase();
		if(islands.containsKey(player)){
			return new Location(getWorld(), (islands.get(player).getBlockX()-(radius/2)) ,93, (islands.get(player).getBlockZ()-(radius/2)) );
		}
		return null;
	}
	
	public boolean addIsland(Player player){
		if(player.hasPermission("epicpvp.skyblock.schematic."+schematic)){
			addIsland(player.getName(), player.getUniqueId().toString(), schematic,true);
			return true;
		}
		return true;
	}
	
	public boolean removeIsland(Player player){
		return removeIsland(player.getName());
	}
	
	public boolean removeIsland(String player){
		player=player.toLowerCase();
		if(islands.containsKey(player)){
			Location loc = islands.get(player);
			int min_x = loc.getBlockX()-radius;
			int max_x = loc.getBlockX();
			
			int min_z = loc.getBlockZ()-radius;
			int max_z = loc.getBlockZ();
			
			int count=0;
			for(Entity e : world.getEntities()){
				if(!(e instanceof Player)){
					if(e.getLocation().getBlockX() > min_x&&e.getLocation().getBlockX()<max_x&&e.getLocation().getBlockZ()>min_z&&e.getLocation().getBlockZ()<max_z){
						e.remove();
						count++;
					}
				}
			}
			
			Block block;
			BlockState state;
			InventoryHolder invHolder;
			int b_count=0;
			for(int x = min_x; x < max_x; x++){
				for(int z = min_z; z < max_z; z++){
					for(int y = 0; y < 256; y++){
						block=world.getBlockAt(x,y,z);
						if(block!=null&&(!block.isEmpty())){
							block.getDrops().clear();
							state=block.getState();
							if(state instanceof InventoryHolder){
								invHolder=(InventoryHolder)state;
								invHolder.getInventory().clear();
							}
							block.setType(Material.AIR);
							b_count++;
						}
					}
				}
			}
			islands.remove(player);
			Log("Die Insel von den Spieler "+player+"(Entities:"+count+"/Bloecke:"+b_count+") wurde resetet.");
			player = UUID.randomUUID().toString();
			getManager().getInstance().getMysql().Update("UPDATE list_skyblock_worlds SET player='!"+player+"' WHERE X='"+loc.getBlockX()+"' AND Z='"+loc.getBlockZ()+"' AND worldName='"+world.getName()+"'");
			islands.put("!"+player, loc);
			getManager().getSchematic().pastePlate(new Location(getWorld(), (max_x-(radius/2)) ,90, (max_z-(radius/2)) ), new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
			return true;
		}
		return false;
	}
	
	public int recycelnIslandAnzahl(){
		int count = 0;
		for(String player : islands.keySet()){
			if(player.charAt(0)=='!'){
				count++;
			}
		}
		return count;
	}
	
	public String recycelnIsland(){
		String island = null;
		for(String player : islands.keySet()){
			if(player.charAt(0)=='!'){
				island=player;
				break;
			}
		}
		return island;
	}
	
	public void addIsland(String player,String uuid,String schematic,boolean recyceln){
		if(recyceln){
			String island = recycelnIsland();
			if(island!=null){
				int x = islands.get(island).getBlockX();
				int z = islands.get(island).getBlockZ();
				islands.remove(island);
				islands.put(player.toLowerCase(), new Location(world,X,0,Z));
				getManager().getInstance().getMysql().Update("UPDATE list_skyblock_worlds SET player='"+player+"', UUID='"+uuid+"' WHERE X='"+x+"' AND Z='"+z+"' AND worldName='"+world.getName()+"'");
				Log("Die Insel von den Spieler "+player+"(X:"+x+",Z:"+z+") wurde erstellt.");
				return;
			}
		}
			
		if( Z == (radius*100)){
			X+=radius;
			Z=0;
		}else{
			Z+=radius;
		}
		getManager().getSchematic().pastePlate(new Location(getWorld(), (X-(radius/2)) ,90, (Z-(radius/2)) ), new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
		islands.put(player.toLowerCase(), new Location(world,X,0,Z));
		getManager().getInstance().getMysql().Update("INSERT INTO list_skyblock_worlds (player,UUID,worldName,X,Z) VALUES ('"+player.toLowerCase()+"','"+uuid+"','"+getWorld().getName()+"','"+X+"','"+Z+"');");
		Log("Die Insel von den Spieler "+player+"(X:"+X+",Z:"+Z+") wurde erstellt.");
	}
	
	public boolean isInIsland(Player player,Location loc){
		return isInIsland(player.getName(),loc);
	}
	
	public boolean isInIsland(String player,Location loc){
		player=player.toLowerCase();
		if(islands.containsKey(player)){
			return isInIsland(islands.get(player),loc);
		}
		return false;
	}
	
	public boolean isInIsland(Location loc,Location loc1){
		if(MinLoc(loc).getX() < loc1.getX() && MinLoc(loc).getZ() < loc1.getZ() && MaxLoc(loc).getBlockX() > loc1.getBlockX() && MaxLoc(loc).getBlockZ() > loc1.getBlockZ()){
			return true;
		}
		return false;
	}
	
	public Location MinLoc(Location loc){
		loc=loc.clone();
		loc.add(loc.getBlockX()-radius,0,loc.getBlockZ()-radius);
		return loc;
	}
	
	public Location MaxLoc(Location loc){
		return loc.clone();
	}
	
	public void setBiome(String player){
		player=player.toLowerCase();
		if(islands.containsKey(player)){
			int min_x = islands.get(player).getBlockX()-radius;
			int max_x = islands.get(player).getBlockX();
			
			int min_z = islands.get(player).getBlockZ()-radius;
			int max_z = islands.get(player).getBlockZ();
			
			for(int x = min_x; x < max_x; x++){
				for(int z = min_z; z < max_z; z++){
					getWorld().setBiome(x, z, Biome.JUNGLE);
				}
			}
		}
	}
	
	public void solveXandZ(){
		if(!islands.isEmpty()){
			for(Location loc : islands.values()){
				X=loc.getBlockX();
				Z=loc.getBlockZ();
				for(Location loc1 : islands.values()){
					if(loc1.getBlockX()>X&&loc1.getBlockZ()>Z){
						X=-1;
						Z=-1;
						break;
					}
				}
				if(X!=-1&&Z!=-1)break;
			}
		}else{
			X=radius;
			Z=0;
		}
	}
	
	public void loadIslands(){
		try
	    {
	      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `player`,`X`,`Z` FROM `list_skyblock_worlds` WHERE worldName='"+world.getName().toLowerCase()+"'");
	      while (rs.next()) {
	    	if(rs.getString(1).charAt(0)!='!')continue;
	    	islands.put(rs.getString(1).toLowerCase(), new Location(world,rs.getInt(2),0,rs.getInt(3)));
	      }
	      rs.close();
	    } catch (Exception err) {
	    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
	    }
		solveXandZ();
	}
	
}
