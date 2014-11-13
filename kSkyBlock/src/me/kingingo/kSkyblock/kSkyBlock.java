package me.kingingo.kSkyblock;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import me.kingingo.kSkyblock.Commands.CommadSkyBlock;
import me.kingingo.kcore.AntiLogout.AntiLogoutManager;
import me.kingingo.kcore.AntiLogout.AntiLogoutType;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandMuteAll;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.PlayerStats.StatsManager;
import me.kingingo.kcore.SignShop.SignShop;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.YAML.API.file.FileConfiguration;
import me.kingingo.kcore.YAML.API.file.YamlConfiguration;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class kSkyBlock extends JavaPlugin {
	
	@Getter
	private AntiLogoutManager antiLogout;
	@Getter
	private Client c;
	@Getter
	private Updater Updater;
	@Getter
	private MySQL mysql;
	@Getter
	private PermissionManager permissionManager;
	@Getter
	private PacketManager PacketManager;
	@Getter
	private CommandHandler cmd;
	@Getter
	private FileConfiguration fConfig;
	@Getter
	private StatsManager statsManager;
	@Getter
	private SkyBlockManager manager;
	
	public void onEnable(){
		try{
		long time = System.currentTimeMillis();
		loadConfig();
		this.Updater=new Updater(this);
		this.c = new Client(getFConfig().getString("Config.Client.Host"),getFConfig().getInt("Config.Client.Port"),"SkyBlock",this,Updater);
		this.mysql=new MySQL(getFConfig().getString("Config.MySQL.User"),getFConfig().getString("Config.MySQL.Password"),getFConfig().getString("Config.MySQL.Host"),getFConfig().getString("Config.MySQL.DB"),this);
		this.PacketManager=new PacketManager(this,c);
		this.permissionManager=new PermissionManager(this,PacketManager,mysql);
		new MemoryFix(this);
		this.statsManager=new StatsManager(this,this.mysql,GameType.SKYBLOCK);
		new SignShop(this, this.statsManager);
		this.cmd=new CommandHandler(this);
		this.cmd.register(CommandMuteAll.class, new CommandMuteAll(permissionManager));	
		this.antiLogout=new AntiLogoutManager(this,AntiLogoutType.KILL,5);
		this.manager=new SkyBlockManager(this);
		this.cmd.register(CommadSkyBlock.class, new CommadSkyBlock(this));	
		DebugLog(time, 45, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, "skyblock", Bukkit.getIp(), mysql);
		}
	}
	
	public void onDisable(){
		c.disconnect(false);
		mysql.close();
	}
	
	public void DebugLog(long time,int zeile,String c){
		System.err.println("[DebugMode]: Class: "+c);
		System.err.println("[DebugMode]: Zeile: "+zeile);
		System.err.println("[DebugMode]: Zeit: "+UtilTime.formatMili(System.currentTimeMillis()-time));
	}
	
	public void DebugLog(String m){
		System.err.println("[DebugMode]: "+m);
	}
	
	public void loadConfig(){
		this.fConfig=YamlConfiguration.loadConfiguration(new File("plugins/kSkyBlock/config.yml"));
		getFConfig().addDefault("Config.MySQL.Host", "NONE");
	    getFConfig().addDefault("Config.MySQL.DB", "NONE");
	    getFConfig().addDefault("Config.MySQL.User", "NONE");
	    getFConfig().addDefault("Config.MySQL.Password", "NONE");
	    getFConfig().addDefault("Config.Client.Host", "79.133.55.5");
	    getFConfig().addDefault("Config.Client.Port", 9051);
		getFConfig().options().copyDefaults(true);
		try {
			getFConfig().save(new File("plugins/kSkyBlock/config.yml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
