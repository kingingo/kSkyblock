package eu.epicpvp.kSkyblock;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.epicpvp.kSkyblock.Gilden.SkyBlockGildenWorld;
import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kSkyblock.World.SkyBlockWorldListener;
import eu.epicpvp.kSkyblock.World.Inventory.SkyBlockInventoyHandler;
import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kSkyblock.World.Island.kPlayer;
import eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator;
import eu.epicpvp.kcore.Gilden.GildenManager;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.MySQL.MySQLErr;
import eu.epicpvp.kcore.MySQL.Events.MySQLErrorEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilFile;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilWorld;
import lombok.Getter;

public class SkyBlockManager extends kListener{
	
	@Getter
	private kSkyBlock instance;
	@Getter
	private HashMap<Integer,kPlayer> players = new HashMap<>();

//	@Getter
//	private LoadingCache<Integer,kPlayer> players1 = CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<Integer,kPlayer>() {
//		public kPlayer load(Integer playerId) throws Exception {
//			kPlayer kplayer = new kPlayer(playerId);
//			kplayer.load();
//			return kplayer;
//		};
//	});
	
	@Getter
	private ArrayList<SkyBlockWorld> worlds = new ArrayList<>();
	@Getter
	private ArrayList<String> schematics = new ArrayList<>();
	@Getter
	private ArrayList<String> delete = new ArrayList<>();
	private boolean whitelist = false;
	@Getter
	private SkyBlockGildenWorld gilden_world;
	@Getter
	private SkyBlockWorldListener skyblockWorldListener;
	@Getter
	private SkyBlockInventoyHandler skyblockInventoryHandler;
	@Getter
	private HashMap<String, String> invite;
	
	@Getter
	private static SkyBlockManager manager;
	
	public SkyBlockManager(kSkyBlock instance){
		super(instance,"SkyBlockManager");
		this.instance=instance;
		getInstance().getMysql().Update("CREATE TABLE IF NOT EXISTS list_skyblock_worlds(playerId int,worldName varchar(30),X int,Z int,properties longtext,lastLogin timestamp)");
		getInstance().getMysql().Update("CREATE TABLE IF NOT EXISTS list_skyblock_worlds_friends(playerId int,ownerId int, worldName varchar(30),permission varchar(30))");
		this.skyblockWorldListener = new SkyBlockWorldListener(this);
		this.skyblockInventoryHandler = new SkyBlockInventoyHandler(this);
		this.invite=new HashMap<>();
		loadSchematics();	
		manager=this;
	}
	
	public void loadSchematics(){
		File folder = new File("plugins/kSkyBlock/schematics");
		logMessage("Schemtics: ");
		if(!folder.exists())folder.mkdirs();
		for(File file : folder.listFiles()){
			if(file.isFile()){
				if(file.getName().contains(".schematic")){
					schematics.add(file.getName().replaceAll(".schematic", ""));
					logMessage(file.getName().replaceAll(".schematic", ""));
					if(!file.getName().replaceAll(".schematic", "").equalsIgnoreCase("gilde")){
						addWorld(file.getName().replaceAll(".schematic", ""), 100, 0);
					}
				}
			}
		}
	}
	
	public SkyBlockGildenWorld addGildenIsland(Player player,String gilde){
		gilden_world.addIsland(player, gilde);
		return gilden_world;
	}
	
	public void addGildenWorld(String worldName,GildenManager gilde){
		if(!UtilFile.existPath(new File(worldName))){
			setConifg("Config.World."+worldName+".Radius", 100);
			setConifg("Config.World."+worldName+".GenerateIsland", 0);
			setConifg("Config.World."+worldName+".CreatureLimit", 50);
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			gilden_world=new SkyBlockGildenWorld(this,gilde,Bukkit.createWorld(wc),100,0,50);
		}else{
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			gilden_world=new SkyBlockGildenWorld(this,gilde,UtilWorld.LoadWorld(wc),getInstance().getFConfig().getInt("Config.World."+worldName+".Radius"),getInstance().getFConfig().getInt("Config.World."+worldName+".GenerateIsland"),getInstance().getConfig().getInt("Config.World."+worldName+".CreatureLimit"));
		}
		
	}
	
	public Island addIsland(Player player){
		for(SkyBlockWorld world : worlds){
			if(world.getMinecraftWorld().getName().equalsIgnoreCase("normal"))continue;
			return world.addIsland(player);
		}
		return null;
	}
	
	@EventHandler
	public void Login(PlayerLoginEvent ev){
		if(whitelist){
			ev.disallow(Result.KICK_WHITELIST, "§cDie Whitelist ist momentan Aktiv!");
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
	}
	

	@EventHandler
	public void member(AsyncPlayerPreLoginEvent ev){
		int playerId = UtilPlayer.getPlayerId(ev.getName());
		
		if(!getPlayers().containsKey(playerId)){
			getPlayers().put(playerId, new kPlayer(playerId));
			
			kPlayer kplayer = getPlayers().get(playerId);
			try {
				ResultSet rs = getInstance().getMysql().Query("SELECT * FROM `list_skyblock_worlds_friends` WHERE playerId='"+playerId+"' AND permission='none';");
				while (rs.next()) {
					if(!kplayer.getMemberList().containsKey(rs.getInt("ownerId"))){
						for(SkyBlockWorld world : worlds)
							world.loadIslandPlayer(rs.getInt("ownerId"));
					}
				}
				rs.close();
			} catch (Exception err) {
				Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getInstance().getMysql()));
			}
		}
	}
	
	@EventHandler
	public void AsyncLogin(AsyncPlayerPreLoginEvent ev){
		for(SkyBlockWorld world : worlds){
			world.loadIslandPlayer(UtilPlayer.getPlayerId(ev.getName()));
		}
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX")+TranslationHandler.getText(ev.getPlayer(), "WHEREIS_TEXT","SkyBlock"));
		ev.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
	}
	
	public Island getIsland(Player player){
		return getIsland(UtilPlayer.getPlayerId(player));
	}
	
	public Island getIsland(int playerId){
		for(SkyBlockWorld world : worlds){
			if(world.haveIsland(playerId)){
				return world.getIsland(playerId);
			}
		}
		return null;
	}
	
	public boolean haveIsland(Player player){
		return haveIsland(UtilPlayer.getPlayerId(player));
	}
	
	public boolean haveIsland(int playerId){
		for(SkyBlockWorld world : worlds){
			if(world.haveIsland(playerId)){
				return true;
			}
		}
		return false;
	}
	
	public void setConifg(String path,int paste){
		getInstance().getFConfig().set(path,paste);
		try {
			getInstance().getFConfig().save(new File("plugins/kSkyBlock/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setConifg(String path,String paste){
		getInstance().getFConfig().set(path,paste);
		try {
			getInstance().getFConfig().save(new File("plugins/kSkyBlock/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addWorld(String worldName,int radius,int generate){
		if(!UtilFile.existPath(new File(worldName))){
			setConifg("Config.World."+worldName+".Radius", radius);
			setConifg("Config.World."+worldName+".Space", 0);
			setConifg("Config.World."+worldName+".Convert", "false");
			setConifg("Config.World."+worldName+".GenerateIsland", generate);
			setConifg("Config.World."+worldName+".CreatureLimit", 50);
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			SkyBlockWorld sw = new SkyBlockWorld(this,worldName,Bukkit.createWorld(wc),radius,0,generate,50);
			sw.setAsync(true);
			worlds.add(sw);
			skyblockWorldListener.getWorlds().put(sw.getMinecraftWorld().getUID(), sw);
		}else{
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			SkyBlockWorld sw;
			sw = new SkyBlockWorld(this,worldName,UtilWorld.LoadWorld(wc),getInstance().getFConfig().getInt("Config.World."+worldName+".Radius"),getInstance().getFConfig().getInt("Config.World."+worldName+".Space"),getInstance().getFConfig().getInt("Config.World."+worldName+".GenerateIsland"),getInstance().getConfig().getInt("Config.World."+worldName+".CreatureLimit"));
			
			sw.setAsync(true);
			worlds.add(sw);
			skyblockWorldListener.getWorlds().put(sw.getMinecraftWorld().getUID(), sw);
		}
	}
	
}
