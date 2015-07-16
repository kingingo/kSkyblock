package me.kingingo.kSkyblock;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import me.kingingo.kSkyblock.Commands.CommadSkyBlock;
import me.kingingo.kSkyblock.Commands.CommandHomeaccept;
import me.kingingo.kSkyblock.Commands.CommandParty;
import me.kingingo.kcore.AACHack.AACHack;
import me.kingingo.kcore.AntiLogout.AntiLogoutManager;
import me.kingingo.kcore.AntiLogout.AntiLogoutType;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandBroadcast;
import me.kingingo.kcore.Command.Admin.CommandCMDMute;
import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandFly;
import me.kingingo.kcore.Command.Admin.CommandFlyspeed;
import me.kingingo.kcore.Command.Admin.CommandGroup;
import me.kingingo.kcore.Command.Admin.CommandInvsee;
import me.kingingo.kcore.Command.Admin.CommandItem;
import me.kingingo.kcore.Command.Admin.CommandMore;
import me.kingingo.kcore.Command.Admin.CommandPermissionTest;
import me.kingingo.kcore.Command.Admin.CommandSocialspy;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Command.Admin.CommandTp;
import me.kingingo.kcore.Command.Admin.CommandTpHere;
import me.kingingo.kcore.Command.Admin.CommandTppos;
import me.kingingo.kcore.Command.Admin.CommandURang;
import me.kingingo.kcore.Command.Admin.CommandVanish;
import me.kingingo.kcore.Command.Commands.CommandClearInventory;
import me.kingingo.kcore.Command.Commands.CommandDelHome;
import me.kingingo.kcore.Command.Commands.CommandEnderchest;
import me.kingingo.kcore.Command.Commands.CommandFeed;
import me.kingingo.kcore.Command.Commands.CommandHeal;
import me.kingingo.kcore.Command.Commands.CommandHome;
import me.kingingo.kcore.Command.Commands.CommandKit;
import me.kingingo.kcore.Command.Commands.CommandMoney;
import me.kingingo.kcore.Command.Commands.CommandMsg;
import me.kingingo.kcore.Command.Commands.CommandNacht;
import me.kingingo.kcore.Command.Commands.CommandR;
import me.kingingo.kcore.Command.Commands.CommandRenameItem;
import me.kingingo.kcore.Command.Commands.CommandRepair;
import me.kingingo.kcore.Command.Commands.CommandSetHome;
import me.kingingo.kcore.Command.Commands.CommandSonne;
import me.kingingo.kcore.Command.Commands.CommandSpawn;
import me.kingingo.kcore.Command.Commands.CommandSpawner;
import me.kingingo.kcore.Command.Commands.CommandSpawnmob;
import me.kingingo.kcore.Command.Commands.CommandTag;
import me.kingingo.kcore.Command.Commands.CommandWarp;
import me.kingingo.kcore.Command.Commands.CommandXP;
import me.kingingo.kcore.Command.Commands.CommandkSpawn;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Gilden.GildenType;
import me.kingingo.kcore.Gilden.SkyBlockGildenManager;
import me.kingingo.kcore.JumpPad.CommandJump;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.PerkManager;
import me.kingingo.kcore.Kit.Command.CommandPerk;
import me.kingingo.kcore.Kit.Perks.PerkArrowPotionEffect;
import me.kingingo.kcore.Kit.Perks.PerkDoubleJump;
import me.kingingo.kcore.Kit.Perks.PerkDoubleXP;
import me.kingingo.kcore.Kit.Perks.PerkDropper;
import me.kingingo.kcore.Kit.Perks.PerkGetXP;
import me.kingingo.kcore.Kit.Perks.PerkGoldenApple;
import me.kingingo.kcore.Kit.Perks.PerkHat;
import me.kingingo.kcore.Kit.Perks.PerkHealPotion;
import me.kingingo.kcore.Kit.Perks.PerkItemName;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoWaterdamage;
import me.kingingo.kcore.Kit.Perks.PerkPotionClear;
import me.kingingo.kcore.Kit.Perks.PerkRunner;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.Listener.Command.ListenerCMD;
import me.kingingo.kcore.Listener.EnderChest.EnderChestListener;
import me.kingingo.kcore.Listener.Enderpearl.EnderpearlListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.GroupTyp;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.SignShop.SignShop;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.TeleportManager.TeleportManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.UserDataConfig.UserDataConfig;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
	@Getter
	private UserDataConfig userData;
	@Getter
	private TeleportManager teleport;
	@Getter
	private CommandHomeaccept ha;
	private PerkManager perkManager;
	private PetManager pet;
	private PetShop petShop;
	
	public void onEnable(){
		try{
		long time = System.currentTimeMillis();
		loadConfig();
		this.Updater=new Updater(this);
		this.c = new Client(getFConfig().getString("Config.Client.Host"),getFConfig().getInt("Config.Client.Port"),"SkyBlock",this,Updater);
		this.mysql=new MySQL(getFConfig().getString("Config.MySQL.User"),getFConfig().getString("Config.MySQL.Password"),getFConfig().getString("Config.MySQL.Host"),getFConfig().getString("Config.MySQL.DB"),this);
		this.PacketManager=new PacketManager(this,c);
		this.permissionManager=new PermissionManager(this,GroupTyp.SKY,PacketManager,mysql);
		new MemoryFix(this);
		this.statsManager=new StatsManager(this,this.mysql,GameType.SKYBLOCK);
		this.userData=new UserDataConfig(this);
		new SignShop(this, this.statsManager);
		this.cmd=new CommandHandler(this);
		teleport=new TeleportManager(getCmd(), getPermissionManager(), 5);
//		pet=new PetManager(this);
//		petShop=new PetShop(pet, permissionManager);
//		this.cmd.register(CommandPet.class, new CommandPet(pet,petShop));
		this.cmd.register(CommandPermissionTest.class, new CommandPermissionTest(permissionManager));
		this.cmd.register(CommandCMDMute.class, new CommandCMDMute(this));	
		this.cmd.register(CommandURang.class, new CommandURang(permissionManager,mysql));	
		this.cmd.register(CommandMoney.class, new CommandMoney(getStatsManager()));
		this.cmd.register(CommandMsg.class, new CommandMsg());
		this.cmd.register(CommandR.class, new CommandR(this));
		this.cmd.register(CommandSocialspy.class, new CommandSocialspy(this));
		this.cmd.register(CommandFly.class, new CommandFly(this));
		this.cmd.register(CommandChatMute.class, new CommandChatMute(this));
		this.cmd.register(CommandToggle.class, new CommandToggle(this));
		this.cmd.register(CommandJump.class, new CommandJump(this));
		this.cmd.register(CommandFeed.class, new CommandFeed());
		this.cmd.register(CommandRepair.class, new CommandRepair());
		this.cmd.register(CommandTag.class, new CommandTag());
		this.cmd.register(CommandNacht.class, new CommandNacht());
		this.cmd.register(CommandHeal.class, new CommandHeal());
		this.cmd.register(CommandHome.class, new CommandHome(getUserData(), teleport));
		this.cmd.register(CommandSpawnmob.class, new CommandSpawnmob());
		this.cmd.register(CommandSpawner.class, new CommandSpawner());
		this.cmd.register(CommandSetHome.class, new CommandSetHome(getUserData(), getPermissionManager()));
		this.cmd.register(CommandSonne.class, new CommandSonne());
		this.cmd.register(CommandGroup.class, new CommandGroup(permissionManager));
		this.cmd.register(CommandDelHome.class, new CommandDelHome(getUserData()));
		this.cmd.register(CommandWarp.class, new CommandWarp(getTeleport()));
		this.cmd.register(CommandKit.class, new CommandKit(getUserData(),cmd));
		this.cmd.register(CommandSpawn.class, new CommandSpawn(getTeleport()));
		this.cmd.register(CommandClearInventory.class, new CommandClearInventory());
		this.cmd.register(CommadSkyBlock.class, new CommadSkyBlock(this));	
		this.cmd.register(CommandRenameItem.class, new CommandRenameItem());
		this.cmd.register(CommandXP.class, new CommandXP());
		this.cmd.register(CommandInvsee.class, new CommandInvsee(this));
		this.cmd.register(CommandEnderchest.class, new CommandEnderchest(this));
		this.cmd.register(CommandParty.class, new CommandParty(this));
		this.cmd.register(CommandBroadcast.class, new CommandBroadcast());
		this.cmd.register(CommandTppos.class, new CommandTppos());
		this.cmd.register(CommandItem.class, new CommandItem());
		this.cmd.register(CommandTp.class, new CommandTp());
		this.cmd.register(CommandTpHere.class, new CommandTpHere());
		this.cmd.register(CommandVanish.class, new CommandVanish(this));
		this.cmd.register(CommandkSpawn.class, new CommandkSpawn());
		this.cmd.register(CommandMore.class, new CommandMore());
		this.cmd.register(CommandFlyspeed.class, new CommandFlyspeed());
		this.perkManager=new PerkManager(this,userData,new Perk[]{new PerkNoWaterdamage(),new PerkArrowPotionEffect(),new PerkHat(),new PerkGoldenApple(),new PerkNoHunger(),new PerkHealPotion(1),new PerkNoFiredamage(),new PerkRunner(0.35F),new PerkDoubleJump(),new PerkDoubleXP(),new PerkDropper(),new PerkGetXP(),new PerkPotionClear(),new PerkItemName(cmd)});
		new PerkListener(perkManager);
		cmd.register(CommandPerk.class, new CommandPerk(perkManager));
		this.antiLogout=new AntiLogoutManager(this,AntiLogoutType.KILL,5);
		this.manager=new SkyBlockManager(this);
		this.ha=new CommandHomeaccept(manager);
		getAntiLogout().setStats(statsManager);
		new kSkyBlockListener(this);
		new EnderpearlListener(this);
		new EnderChestListener(getUserData());
		Bukkit.getWorld("world").setStorm(false);
		AACHack a = new AACHack("SKYBLOCK", mysql, PacketManager);
		a.setAntiLogoutManager(getAntiLogout());
		new ListenerCMD(this);
		new ChatListener(this,new SkyBlockGildenManager(manager, mysql, GildenType.SKY, cmd,getStatsManager()),permissionManager);
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
