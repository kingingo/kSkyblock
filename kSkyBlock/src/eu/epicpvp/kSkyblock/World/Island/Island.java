package eu.epicpvp.kSkyblock.World.Island;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.kSkyblock.Util.UtilSchematic;
import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kSkyblock.World.Events.IslandDeleteEvent;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import eu.epicpvp.kcore.Util.UtilWorld;
import eu.epicpvp.kcore.kConfig.kConfig;
import eu.epicpvp.nbt.NBTCompressedStreamTools;
import eu.epicpvp.nbt.NBTTagCompound;
import lombok.Getter;

public class Island {

	@Getter
	private SkyBlockWorld world;
	@Getter
	private int playerId;
	@Getter
	private Location location;
	@Getter
	private HomeAble homeAble=HomeAble.QUESTION;
	private Location home;
	@Getter
	private boolean mobSpawn=false;
	@Getter
	private WrapperPacketPlayOutWorldBorder islandBorder;
	@Getter
	private HashMap<Integer,Member> member;
	@Getter
	private NBTTagCompound nbt;
	
	public Island(int playerId,Location location,NBTTagCompound nbt, SkyBlockWorld world){
		this.world=world;
		this.nbt=nbt;
		this.playerId=playerId;
		this.location=location;
		this.member=new HashMap<>();
		this.islandBorder=UtilWorld.createWorldBorder(new Location(getWorld().getMinecraftWorld(), getLocation().getX()-(getWorld().getRadius()/2),90, getLocation().getZ()-(getWorld().getRadius()/2)), getWorld().getRadius()-getWorld().getSpace(), 25, 10);
		loadNBT();
		loadMember();
	}
	
	public void removeHomes(){
		for(int playerId : getHomes()){
			removeHome(playerId);
		}
	}
	
	public void removeHome(int playerId){
		ArrayList<Integer> list = getHomes();
		if(!list.contains(playerId)&&playerId!=this.playerId)return;
		kConfig config = UtilServer.getUserData().getConfig(playerId);
		
		for(String home : config.getPathList("homes").keySet()){
			Location loc = config.getLocation("homes."+home);
			if(loc.getWorld().getUID() == getLocation().getWorld().getUID()){
				if(contains(loc)){
					config.set("homes."+home, null);
				}
			}
		}
		
		UtilServer.getUserData().saveConfig(playerId);
		list.remove(Integer.valueOf(playerId));
		setHomes(list);
	}
	
	public void addHome(int playerId){
		ArrayList<Integer> list = getHomes();
		if(!list.contains(playerId)){
			list.add(Integer.valueOf(playerId));
			setHomes(list);
		}
	}
	
	public void setHomes(ArrayList<Integer> list){
		nbt.setIntArray("homes", ArrayUtils.toPrimitive(list.toArray(new Integer[list.size()])));
		updateNBT();
	}
	
	public ArrayList<Integer> getHomes(){
		return new ArrayList<>(Arrays.asList(ArrayUtils.toObject(getHomesArray())));
	}
	
	public int[] getHomesArray(){
		if(!nbt.hasKey("homes"))return new int[]{};
		return nbt.getIntArray("homes");
	}
	
	public void setMobSpawn(boolean mobSpawn){
		this.mobSpawn=mobSpawn;
		nbt.setBoolean("mobSpawn", mobSpawn);
		updateNBT();
	}
	
	public void removeMember(int playerId){
		if(member.containsKey(playerId)){
			member.get(playerId).delete();
		}
	}
	
	public boolean addMember(int playerId){
		if(!member.containsKey(playerId)){
			member.put(playerId, new Member(playerId, this));
			UtilServer.getMysql().Update("INSERT INTO list_skyblock_worlds_friends (playerId,ownerId, worldName, permission) VALUES ('" + playerId + "','" + this.playerId + "','" + getLocation().getWorld().getName() + "','none');");
			return true;
		}
		return false;
	}
	
	public void loadMember(){
		Island is = this;
		UtilServer.getMysql().asyncQuery("SELECT * FROM list_skyblock_worlds_friends WHERE ownerId='"+playerId+"' AND worldName='"+this.location.getWorld().getName()+"';", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rs,Throwable ex) {
				try {
					if(!member.containsKey(rs.getInt("playerId")))member.put(rs.getInt("playerId"), new Member(rs.getInt("playerId"),is));
					if(!rs.getString("permission").equalsIgnoreCase("none")&&IslandPermission.of(rs.getString("permission"))!=null)member.get(rs.getInt("playerId")).getPermissions().add(IslandPermission.of(rs.getString("permission")));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setOwner(int playerId){
		if(this.playerId!=-1){
			getWorld().logMessage("Die Spieler ID ist nicht -1 sondern "+this.playerId+" "+(getWorld().getEmpty_islands().contains(this))+ " " + getWorld().getIslands().containsKey(playerId));
		}
		
		getWorld().getEmpty_islands().remove(this);
		this.playerId=playerId;
		getWorld().getIslands().remove(playerId);
		getWorld().getIslands().put(playerId, this);
		getWorld().getManager().getInstance().getMysql().Update(getWorld().isAsync(), "UPDATE list_skyblock_worlds SET playerId='"+ playerId + "' WHERE worldName='" + getWorld().getMinecraftWorld().getName() + "' AND X='" + getLocation().getBlockX() + "' AND Z='" + getLocation().getBlockZ() + "';");
		getWorld().logMessage("Der Spieler " + playerId + " besitzt nun eine neue Insel (X:" + getLocation().getBlockX() + ",Z:" + getLocation().getBlockZ() + ").");
	}
	
	public int clearEntities(){
		int min_x = getLocation().getBlockX()-getWorld().getRadius();
		int max_x = getLocation().getBlockX();
			
		int min_z = getLocation().getBlockZ()-getWorld().getRadius();
		int max_z = getLocation().getBlockZ();
			
		int count=0;
		for(Entity e : getWorld().getMinecraftWorld().getEntities()){
			if(!(e instanceof Player)){
				if(e.getLocation().getBlockX() > min_x&&e.getLocation().getBlockX()<max_x&&e.getLocation().getBlockZ()>min_z&&e.getLocation().getBlockZ()<max_z){
					e.remove();
					count++;
				}
			}
		}
		return count;
	}
	
	public void reset(boolean clean){
		Player player = UtilPlayer.searchExact(playerId);
		if(player!=null){
			if(contains(player.getLocation())){
				player.teleport(Bukkit.getWorld("world").getSpawnLocation());
			}
		}
		
		if(clean){
			int min_x = getLocation().getBlockX()-getWorld().getRadius();
			int max_x = getLocation().getBlockX();
				
			int min_z = getLocation().getBlockZ()-getWorld().getRadius();
			int max_z = getLocation().getBlockZ();
			int count = clearEntities();
				
			Block block;
			BlockState state;
			InventoryHolder invHolder;
			int b_count=0;
			for(int x = min_x; x < max_x; x++){
				for(int z = min_z; z < max_z; z++){
					for(int y = 0; y < 256; y++){
						block=getWorld().getMinecraftWorld().getBlockAt(x,y,z);
						if(block!=null&&(!block.isEmpty())&&block.getType()!=Material.AIR){
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
			getWorld().logMessage("Die Insel " + getLocation().getX() + "/" + getLocation().getZ() + " (Entities:" + count + "/Bloecke:" + b_count + ") wurde erneuert.");
		}
		
		Location home = getHome();
		loadChunk();
		UtilSchematic.pastePlate(getWorld().getSession(),home, new File("plugins/kSkyBlock/schematics/"+getWorld().getSchematic()+".schematic"));
		if(playerId==-2){
			getWorld().getManager().getInstance().getMysql().Update(getWorld().isAsync(),"UPDATE list_skyblock_worlds SET playerId='-1' WHERE X='"+getLocation().getBlockX()+"' AND Z='"+getLocation().getBlockZ()+"' AND worldName='"+getWorld().getMinecraftWorld().getName()+"'");
			playerId=-1;
		}
		nbt=new NBTTagCompound();
		nbt.setIntArray("homes", new int[]{});
		updateNBT();
		loadNBT();
		updateNBT();
		
		getWorld().logMessage("Die Insel " + getLocation().getX() + "/" + getLocation().getZ() + " schematic gepastet!");
	}
	
	public void loadChunk(){
		Location home = getHome();
		home.getWorld().loadChunk(home.getChunk());
	}
	
	public boolean delete(){
		Player player = UtilPlayer.searchExact(playerId);
		if(player!=null && !player.isOp() && getWorld().getManager().getDelete().contains(player.getName().toLowerCase()))return false;
		
		if(player!=null){
			getWorld().logMessage("Die Insel von den Spieler "+player.getName()+"(PlayerId:"+playerId+") wurde gelöscht.");
			getWorld().getManager().getDelete().add(player.getName().toLowerCase());
		}else{
			getWorld().logMessage("Die Insel von den Spieler "+playerId+" wurde gelöscht.");
		}
		Bukkit.getPluginManager().callEvent(new IslandDeleteEvent(this));

		for(Member m : new ArrayList<>(getMember().values()))m.delete();
		removeHomes();
		removeHome(playerId);
		getWorld().getIslands().remove(playerId);
		UtilServer.getMysql().Update(getWorld().isAsync(),"UPDATE list_skyblock_worlds SET playerId='-2' WHERE X='"+getLocation().getBlockX()+"' AND Z='"+getLocation().getBlockZ()+"' AND worldName='"+getWorld().getMinecraftWorld().getName()+"'");
		this.playerId=-2;
		return true;
	}
	
	public boolean contains(Location loc){		
		return ((getLocation().getBlockX()-(getWorld().getRadius()/2)) - ((getWorld().getRadius()-getWorld().getSpace())/2)) <= loc.getX() 
				&& ((getLocation().getBlockZ()-(getWorld().getRadius()/2)) - ((getWorld().getRadius()-getWorld().getSpace())/2)) <= loc.getZ() 
				&& ((getLocation().getBlockX()-(getWorld().getRadius()/2)) + ((getWorld().getRadius()-getWorld().getSpace())/2)) >= loc.getX()
				&& ((getLocation().getBlockZ()-(getWorld().getRadius()/2)) + ((getWorld().getRadius()-getWorld().getSpace())/2)) >= loc.getZ();
	}
	
	public void setHomeAble(HomeAble homeAble){
		this.homeAble=homeAble;
		nbt.setInt("homeAble", homeAble.ordinal());
		updateNBT();
	}

	public Location getFixHome(){
		Location loc = getHome();
		loc.getBlock().setType(Material.AIR);
		loc.clone().add(0,-1,0).getBlock().setType(Material.AIR);
		loc.clone().add(0,-2,0).getBlock().setType(Material.BEDROCK);
		return loc;
	}
	
	public void loadNBT(){
		if(nbt.hasKey("mobSpawn")){
			mobSpawn=nbt.getBoolean("mobSpawn");
		}
		
		if(nbt.hasKey("homeable")){
			this.homeAble=HomeAble.values()[nbt.getInt("homeable")];
		}
		
		if(nbt.hasKey("homeX")&&nbt.hasKey("homeY")&&nbt.hasKey("homeZ")&&nbt.hasKey("homeYaw")&&nbt.hasKey("homePitch")){
			this.home=new Location(location.getWorld(),nbt.getDouble("homeX"),nbt.getDouble("homeY"),nbt.getDouble("homeZ"));
			this.home.setPitch(nbt.getFloat("homePitch"));
			this.home.setYaw(nbt.getFloat("homeYaw"));
		}
	}
	
	public void updateNBT(){
		try {
			UtilServer.getMysql().Update(true,"UPDATE list_skyblock_worlds SET properties='"+NBTCompressedStreamTools.toString(nbt)+"' WHERE X='"+getLocation().getBlockX()+"' AND Z='"+getLocation().getBlockZ()+"' AND worldName='"+getWorld().getMinecraftWorld().getName()+"'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setHome(Location location){
		this.home=location;
		
		nbt.setDouble("homeX", home.getX());
		nbt.setDouble("homeY", home.getY());
		nbt.setDouble("homeZ", home.getZ());
		nbt.setFloat("homePitch", home.getPitch());
		nbt.setFloat("homeYaw", home.getYaw());
		updateNBT();
	}
	
	public Location getHome(){
		if(this.home==null)this.home=getMiddle();	
		return this.home;
	}
	
	public Location getMiddle(){
		return new Location(getWorld().getMinecraftWorld(), (getLocation().getBlockX()-(getWorld().getRadius()/2)) ,93, (getLocation().getBlockZ()-(getWorld().getRadius()/2)) );
	}
	
	public void setBiome(Biome biome){
		int min_x = getLocation().getBlockX()-getWorld().getRadius();
		int max_x = getLocation().getBlockX();
			
		int min_z = getLocation().getBlockZ()-getWorld().getRadius();
		int max_z = getLocation().getBlockZ();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(int x = min_x; x < max_x; x++){
					for(int z = min_z; z < max_z; z++){
						getWorld().getMinecraftWorld().getBlockAt(x, 90, z).getChunk().load();
						getWorld().getMinecraftWorld().setBiome(x, z, biome);
					}
				}
				
				if(UtilPlayer.isOnline(playerId)){
					Player player = UtilPlayer.searchExact(playerId);
					if(contains(player.getLocation())){
						player.teleport(Bukkit.getWorld("world").getSpawnLocation());
					}
					
					player.sendMessage(TranslationHandler.getPrefixAndText(player, "SKYBLOCK_BIOME_CHANGE",UtilString.toUpperCase(biome.name())));
				}
				getWorld().logMessage("Das Biome der Insel ("+playerId+") wurde zu "+biome.name()+" geändert!");
				cancel();
			}
		}.runTaskTimer(world.getManager().getInstance(), 0L, 20L);
	}
}
