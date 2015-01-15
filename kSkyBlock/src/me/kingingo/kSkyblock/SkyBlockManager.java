package me.kingingo.kSkyblock;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.kSkyblock.Util.UtilSchematic;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.ChunkGenerator.CleanroomChunkGenerator;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Util.FileUtil;
import me.kingingo.kcore.Util.WorldUtil;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class SkyBlockManager extends kListener{
	
	@Getter
	private kSkyBlock instance;
	@Getter
	private ArrayList<SkyBlockWorld> worlds = new ArrayList<>();
	@Getter
	private UtilSchematic schematic;
	@Getter
	private ArrayList<String> schematics = new ArrayList<>();
	private boolean whitelist = false;
	
	public SkyBlockManager(kSkyBlock instance){
		super(instance,"[SkyBlockManager]");
		this.instance=instance;
		this.schematic=new UtilSchematic();
		loadSchematics();
		getInstance().getMysql().Update("CREATE TABLE IF NOT EXISTS list_skyblock_worlds(player varchar(17),UUID varchar(100),worldName varchar(30),X int,Z int)");
		addWorld("normal", 100, 300);
	}
	
	public void loadSchematics(){
		File folder = new File("plugins/kSkyBlock/schematics");
		Log("Schamtics: ");
		if(!folder.exists())folder.mkdirs();
		for(File file : folder.listFiles()){
			if(file.isFile()){
				if(file.getName().contains(".schematic")){
					schematics.add(file.getName().replaceAll(".schematic", ""));
					Log(file.getName().replaceAll(".schematic", ""));
				}
			}
		}
	}
	
	public SkyBlockWorld addIsland(Player player){
		for(SkyBlockWorld world : worlds){
			if(player.hasPermission("epicpvp.skyblock.schematic."+world.getSchematic())){
				world.addIsland(player);
				return world;
			}
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
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.WHEREIS_TEXT.getText("SkyBlock"));
		for(SkyBlockWorld world : worlds)world.loadIslandPlayer(ev.getPlayer());
		ev.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
	}
	
	public SkyBlockWorld getIsland(Player player){
		return getIsland(player.getName());
	}
	
	public SkyBlockWorld getIsland(String player){
		for(SkyBlockWorld world : worlds){
			if(world.haveIsland(player)){
				return world;
			}
		}
		return null;
	}
	
	public boolean haveIsland(Player player){
		return haveIsland(player.getName());
	}
	
	public boolean haveIsland(String player){
		for(SkyBlockWorld world : worlds){
			if(world.haveIsland(player)){
				return true;
			}
		}
		return false;
	}
	
	public void loadWorld(String worldName){
		addWorld(worldName,getInstance().getFConfig().getInt("Config.World."+worldName+".Radius"),getInstance().getFConfig().getInt("Config.World."+worldName+".GenerateIsland"));
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
		if(!FileUtil.existPath(new File(worldName))){
			setConifg("Config.World."+worldName+".Radius", radius);
			setConifg("Config.World."+worldName+".GenerateIsland", generate);
			setConifg("Config.World."+worldName+".CreatureLimit", 50);
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			worlds.add(new SkyBlockWorld(this,worldName,Bukkit.createWorld(wc),radius,generate,50));
		}else{
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			worlds.add(new SkyBlockWorld(this,worldName,WorldUtil.LoadWorld(wc),radius,generate,getInstance().getConfig().getInt("Config.World."+worldName+".CreatureLimit")));
		}
	}
	
}
