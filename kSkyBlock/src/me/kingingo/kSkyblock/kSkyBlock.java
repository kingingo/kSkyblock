package me.kingingo.kSkyblock;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import me.kingingo.kSkyblock.Commands.CommadSkyBlock;
import me.kingingo.kSkyblock.Commands.CommandHomeaccept;
import me.kingingo.kSkyblock.Commands.CommandParty;
import me.kingingo.kSkyblock.Listener.PerkListener;
import me.kingingo.kSkyblock.Listener.kSkyBlockListener;
import me.kingingo.kSkyblock.Listener.Holiday.ChristmasListener;
import me.kingingo.kcore.AACHack.AACHack;
import me.kingingo.kcore.AntiLogout.AntiLogoutManager;
import me.kingingo.kcore.AntiLogout.AntiLogoutType;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandBroadcast;
import me.kingingo.kcore.Command.Admin.CommandCMDMute;
import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandDebug;
import me.kingingo.kcore.Command.Admin.CommandFly;
import me.kingingo.kcore.Command.Admin.CommandFlyspeed;
import me.kingingo.kcore.Command.Admin.CommandGive;
import me.kingingo.kcore.Command.Admin.CommandGiveAll;
import me.kingingo.kcore.Command.Admin.CommandGroup;
import me.kingingo.kcore.Command.Admin.CommandItem;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Command.Admin.CommandMore;
import me.kingingo.kcore.Command.Admin.CommandPvPMute;
import me.kingingo.kcore.Command.Admin.CommandSocialspy;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Command.Admin.CommandTp;
import me.kingingo.kcore.Command.Admin.CommandTpHere;
import me.kingingo.kcore.Command.Admin.CommandTppos;
import me.kingingo.kcore.Command.Admin.CommandTrackingRange;
import me.kingingo.kcore.Command.Admin.CommandURang;
import me.kingingo.kcore.Command.Admin.CommandVanish;
import me.kingingo.kcore.Command.Admin.CommandgBroadcast;
import me.kingingo.kcore.Command.Commands.CommandAmboss;
import me.kingingo.kcore.Command.Commands.CommandClearInventory;
import me.kingingo.kcore.Command.Commands.CommandDelHome;
import me.kingingo.kcore.Command.Commands.CommandEnchantmentTable;
import me.kingingo.kcore.Command.Commands.CommandEnderchest;
import me.kingingo.kcore.Command.Commands.CommandExt;
import me.kingingo.kcore.Command.Commands.CommandFeed;
import me.kingingo.kcore.Command.Commands.CommandFill;
import me.kingingo.kcore.Command.Commands.CommandHandel;
import me.kingingo.kcore.Command.Commands.CommandHead;
import me.kingingo.kcore.Command.Commands.CommandHeal;
import me.kingingo.kcore.Command.Commands.CommandHome;
import me.kingingo.kcore.Command.Commands.CommandInvsee;
import me.kingingo.kcore.Command.Commands.CommandKit;
import me.kingingo.kcore.Command.Commands.CommandMoney;
import me.kingingo.kcore.Command.Commands.CommandMsg;
import me.kingingo.kcore.Command.Commands.CommandNacht;
import me.kingingo.kcore.Command.Commands.CommandNear;
import me.kingingo.kcore.Command.Commands.CommandPotion;
import me.kingingo.kcore.Command.Commands.CommandR;
import me.kingingo.kcore.Command.Commands.CommandRemoveEnchantment;
import me.kingingo.kcore.Command.Commands.CommandRenameItem;
import me.kingingo.kcore.Command.Commands.CommandRepair;
import me.kingingo.kcore.Command.Commands.CommandSetHome;
import me.kingingo.kcore.Command.Commands.CommandSonne;
import me.kingingo.kcore.Command.Commands.CommandSpawn;
import me.kingingo.kcore.Command.Commands.CommandSpawner;
import me.kingingo.kcore.Command.Commands.CommandSpawnmob;
import me.kingingo.kcore.Command.Commands.CommandSuffix;
import me.kingingo.kcore.Command.Commands.CommandTag;
import me.kingingo.kcore.Command.Commands.CommandWarp;
import me.kingingo.kcore.Command.Commands.CommandWorkbench;
import me.kingingo.kcore.Command.Commands.CommandXP;
import me.kingingo.kcore.Command.Commands.CommandkSpawn;
import me.kingingo.kcore.DeliveryPet.DeliveryObject;
import me.kingingo.kcore.DeliveryPet.DeliveryPet;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.GemsShop.GemsShop;
import me.kingingo.kcore.Gilden.GildenType;
import me.kingingo.kcore.Gilden.SkyBlockGildenManager;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.Item.Click;
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
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.Listener.Command.ListenerCMD;
import me.kingingo.kcore.Listener.EnderChest.EnderChestListener;
import me.kingingo.kcore.Listener.Enderpearl.EnderpearlListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.TWIITTER_IS_PLAYER_FOLLOWER;
import me.kingingo.kcore.Permission.GroupTyp;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Pet.Commands.CommandPet;
import me.kingingo.kcore.Pet.Shop.PlayerPetHandler;
import me.kingingo.kcore.SignShop.SignShop;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.TeleportManager.TeleportManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.UserDataConfig.UserDataConfig;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
	@Getter
	private Hologram hologram;
	@Getter
	private InventoryBase base;
	@Getter
	private PetManager petManager;
	@Getter
	private PlayerPetHandler petHandler;
	
	public void onEnable(){
		try{
		long time = System.currentTimeMillis();
		loadConfig();
		this.Updater=new Updater(this);
		this.c = new Client(this,getFConfig().getString("Config.Client.Host"),getFConfig().getInt("Config.Client.Port"),"SkyBlock");
		this.mysql=new MySQL(getFConfig().getString("Config.MySQL.User"),getFConfig().getString("Config.MySQL.Password"),getFConfig().getString("Config.MySQL.Host"),getFConfig().getString("Config.MySQL.DB"),this);
		Language.load(mysql);
		this.PacketManager=new PacketManager(this,c);
		this.permissionManager=new PermissionManager(this,GroupTyp.SKY,PacketManager,mysql);
		new MemoryFix(this);
		this.statsManager=new StatsManager(this,this.mysql,GameType.SKYBLOCK);
		this.userData=new UserDataConfig(this);
		this.hologram=new Hologram(this);
		this.hologram.RemoveText();
		this.cmd=new CommandHandler(this);
		this.base=new InventoryBase(this);
		UtilServer.createGemsShop(new GemsShop(getHologram(), getCmd(), getBase(), getPermissionManager(), ServerType.SKYBLOCK));
		this.petManager=new PetManager(this);
		this.petHandler= new PlayerPetHandler(ServerType.SKYBLOCK, getPetManager(), getBase(), getPermissionManager());
		this.teleport=new TeleportManager(getCmd(), getPermissionManager(), 5);
		this.perkManager=new PerkManager(this,userData,new Perk[]{new PerkNoWaterdamage(),new PerkArrowPotionEffect(),new PerkHat(),new PerkGoldenApple(),new PerkNoHunger(),new PerkHealPotion(1),new PerkNoFiredamage(),new PerkRunner(0.35F),new PerkDoubleJump(),new PerkDoubleXP(),new PerkDropper(),new PerkGetXP(),new PerkPotionClear(),new PerkItemName(cmd)});
		new SignShop(this,this.cmd, this.statsManager);
		this.antiLogout=new AntiLogoutManager(this,AntiLogoutType.KILL,5);

		this.cmd.register(CommandDebug.class, new CommandDebug());
		this.cmd.register(CommandGiveAll.class, new CommandGiveAll());
		this.cmd.register(CommandPet.class, new CommandPet(getPetHandler()));
		this.cmd.register(CommandCMDMute.class, new CommandCMDMute(this));	
		this.cmd.register(CommandPvPMute.class, new CommandPvPMute(this));	
		this.cmd.register(CommandChatMute.class, new CommandChatMute(this));
		this.cmd.register(CommandTrackingRange.class, new CommandTrackingRange());
		this.cmd.register(CommandToggle.class, new CommandToggle(this));
		this.cmd.register(CommandURang.class, new CommandURang(permissionManager,mysql));	
		this.cmd.register(CommandMoney.class, new CommandMoney(getStatsManager(),ServerType.SKYBLOCK));
		this.cmd.register(CommandMsg.class, new CommandMsg());
		this.cmd.register(CommandR.class, new CommandR(this));
		this.cmd.register(CommandSocialspy.class, new CommandSocialspy(this));
		this.cmd.register(CommandFly.class, new CommandFly(this));
		this.cmd.register(CommandHandel.class, new CommandHandel(this));
		this.cmd.register(CommandJump.class, new CommandJump(this));
		this.cmd.register(CommandFeed.class, new CommandFeed());
		this.cmd.register(CommandRepair.class, new CommandRepair());
		this.cmd.register(CommandTag.class, new CommandTag());
		this.cmd.register(CommandNacht.class, new CommandNacht());
		this.cmd.register(CommandHeal.class, new CommandHeal());
		this.cmd.register(CommandHome.class, new CommandHome(getUserData(), teleport,this.cmd));
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
		this.cmd.register(CommandInvsee.class, new CommandInvsee(mysql));
		this.cmd.register(CommandEnderchest.class, new CommandEnderchest(mysql));
		this.cmd.register(CommandParty.class, new CommandParty(this));
		this.cmd.register(CommandBroadcast.class, new CommandBroadcast());
		this.cmd.register(CommandTppos.class, new CommandTppos());
		this.cmd.register(CommandItem.class, new CommandItem());
		this.cmd.register(CommandTp.class, new CommandTp());
		this.cmd.register(CommandTpHere.class, new CommandTpHere());
		this.cmd.register(CommandVanish.class, new CommandVanish(this));
		this.cmd.register(CommandkSpawn.class, new CommandkSpawn(getAntiLogout()));
		this.cmd.register(CommandMore.class, new CommandMore());
		this.cmd.register(CommandFlyspeed.class, new CommandFlyspeed());
		this.cmd.register(CommandGive.class, new CommandGive());
		this.cmd.register(CommandgBroadcast.class, new CommandgBroadcast(PacketManager));
		this.cmd.register(CommandLocations.class, new CommandLocations(this));
		this.cmd.register(CommandPerk.class, new CommandPerk(perkManager,getBase()));
		this.cmd.register(CommandSuffix.class, new CommandSuffix(getUserData()));
		this.cmd.register(CommandAmboss.class, new CommandAmboss());
		this.cmd.register(CommandNear.class, new CommandNear());
		this.cmd.register(CommandRemoveEnchantment.class, new CommandRemoveEnchantment());
		this.cmd.register(CommandEnchantmentTable.class, new CommandEnchantmentTable());
		this.cmd.register(CommandExt.class, new CommandExt());
		this.cmd.register(CommandWorkbench.class, new CommandWorkbench());
		this.cmd.register(CommandHead.class, new CommandHead());
		this.cmd.register(CommandPotion.class, new CommandPotion(getPermissionManager()));
		this.cmd.register(CommandFill.class, new CommandFill());
		
		UtilServer.createDeliveryPet(new DeliveryPet(getBase(),null,new DeliveryObject[]{
			new DeliveryObject(new String[]{"","§7Click for Vote!","","§ePvP Rewards:","§7   200 Epics","§7   1x Inventory Repair","","§eGame Rewards:","§7   25 Gems","§7   100 Coins","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},kPermission.DELIVERY_PET_VOTE,false,28,"§aVote for EpicPvP",Material.PAPER,Material.REDSTONE_BLOCK,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						p.closeInventory();
						p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
						p.sendMessage(Language.getText(p,"PREFIX")+" ");
						p.sendMessage(Language.getText(p,"PREFIX")+"Vote Link:§a http://goo.gl/wxdAj4");
						p.sendMessage(Language.getText(p,"PREFIX")+" ");
						p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
					}
					
				},-1),
				new DeliveryObject(new String[]{"§aOnly for §eVIP§a!","","§ePvP Rewards:","§7   200 Epics","§7   10 Level","","§eGame Rewards:","§7   200 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},kPermission.DELIVERY_PET_VIP_WEEK,true,11,"§cRank §eVIP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getStatsManager().addDouble(p, 200, Stats.MONEY);
						p.setLevel(p.getLevel()+10);
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","200"}));
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §6ULTRA§a!","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   300 Epics","§7   4x Diamonds","§7   4x Iron Ingot","§7   4x Gold Ingot"},kPermission.DELIVERY_PET_ULTRA_WEEK,true,12,"§cRank §6ULTRA§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getStatsManager().addDouble(p, 300, Stats.MONEY);
						p.getInventory().addItem(new ItemStack(Material.DIAMOND,2));
						p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
						p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","300"}));
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §aLEGEND§a!","","§ePvP Rewards:","§7   400 Epics","§7   20 Level","","§eGame Rewards:","§7   400 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   400 Epics","§7   6x Diamonds","§7   6x Iron Ingot","§7   6x Gold Ingot"},kPermission.DELIVERY_PET_LEGEND_WEEK,true,13,"§cRank §5LEGEND§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getStatsManager().addDouble(p, 400, Stats.MONEY);
						p.getInventory().addItem(new ItemStack(Material.DIAMOND,4));
						p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,4));
						p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,4));
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","400"}));
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§a!","","§ePvP Rewards:","§7   500 Epics","§7   25 Level","","§eGame Rewards:","§7   500 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   500 Epics","§7   8x Diamonds","§7   8x Iron Ingot","§7   8x Gold Ingot"},kPermission.DELIVERY_PET_MVP_WEEK,true,14,"§cRank §3MVP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getStatsManager().addDouble(p, 500, Stats.MONEY);
						p.getInventory().addItem(new ItemStack(Material.DIAMOND,4));
						p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,4));
						p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,4));
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","500"}));
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§c+§a!","","§ePvP Rewards:","§7   600 Epics","§7   30 Level","","§eGame Rewards:","§7   600 Coins","§7   4x TTT Paesse","","§eSkyBlock Rewards:","§7   600 Epics","§7   10x Diamonds","§7   10x Iron Ingot","§7   10x Gold Ingot"},kPermission.DELIVERY_PET_MVPPLUS_WEEK,true,15,"§cRank §9MVP§e+§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getStatsManager().addDouble(p, 600, Stats.MONEY);
						p.getInventory().addItem(new ItemStack(Material.DIAMOND,6));
						p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,6));
						p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,6));
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","600"}));
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§7/twitter [TwitterName]","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","","§eSkyBlock Rewards:","§7   300 Epics","§7   15 Level"},kPermission.DELIVERY_PET_TWITTER,false,34,"§cTwitter Reward",Material.getMaterial(351),4,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						String s1 = getMysql().getString("SELECT twitter FROM BG_TWITTER WHERE uuid='"+UtilPlayer.getRealUUID(p)+"'");
						if(s1.equalsIgnoreCase("null")){
							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_ACC_NOT"));
						}else{
							getPacketManager().SendPacket("DATA", new TWIITTER_IS_PLAYER_FOLLOWER(s1, p.getName()));
							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_CHECK"));
						}
					}
					
				},TimeSpan.DAY*7),
		},"§bThe Delivery Jockey!",EntityType.CHICKEN,CommandLocations.getLocation("DeliveryPet"),ServerType.SKYBLOCK,getHologram(),getMysql())
		);

		this.manager=new SkyBlockManager(this);
		this.ha=new CommandHomeaccept(manager);
		getAntiLogout().setStats(statsManager);
		new kSkyBlockListener(this);
		new EnderpearlListener(this);
		new EnderChestListener(getUserData());
		Bukkit.getWorld("world").setStorm(false);
		AACHack a = new AACHack("SKYBLOCK", mysql, PacketManager);
		a.setAntiLogoutManager(getAntiLogout());
		
		new PerkListener(perkManager);
		new BungeeCordFirewallListener(mysql, "sky");
		new ListenerCMD(this);
		new ChatListener(this,new SkyBlockGildenManager(manager, mysql, GildenType.SKY, cmd,getStatsManager()),permissionManager,getUserData());
		
		
		if(Calendar.getHoliday()!=null){
			switch(Calendar.holiday){
			case WEIHNACHTEN:
					new ChristmasListener(this);
				break;
			}
		}
		
		UtilServer.createLagListener(this.cmd);
		DebugLog(time, 45, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, "skyblock", Bukkit.getIp(), mysql);
		}
	}
	
	public void onDisable(){
		c.disconnect(false);
		getHologram().RemoveText();
		UtilServer.getGemsShop().onDisable();
		Updater.stop();
		if(UtilServer.getDeliveryPet()!=null)UtilServer.getDeliveryPet().onDisable();
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
