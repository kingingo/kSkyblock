package eu.epicpvp.kSkyblock;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.ServerType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.kSkyblock.Commands.CommadSkyBlock;
import eu.epicpvp.kSkyblock.Commands.CommandHomeaccept;
import eu.epicpvp.kSkyblock.Commands.CommandParty;
import eu.epicpvp.kSkyblock.Listener.PerkListener;
import eu.epicpvp.kSkyblock.Listener.SkyBlockListener;
import eu.epicpvp.kSkyblock.Listener.Holiday.ChristmasListener;
import eu.epicpvp.kcore.AACHack.AACHack;
import eu.epicpvp.kcore.AntiLogout.AntiLogoutManager;
import eu.epicpvp.kcore.AntiLogout.AntiLogoutType;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Command.Admin.CommandAddEpics;
import eu.epicpvp.kcore.Command.Admin.CommandBroadcast;
import eu.epicpvp.kcore.Command.Admin.CommandCMDMute;
import eu.epicpvp.kcore.Command.Admin.CommandChatMute;
import eu.epicpvp.kcore.Command.Admin.CommandDebug;
import eu.epicpvp.kcore.Command.Admin.CommandFly;
import eu.epicpvp.kcore.Command.Admin.CommandFlyspeed;
import eu.epicpvp.kcore.Command.Admin.CommandGive;
import eu.epicpvp.kcore.Command.Admin.CommandGiveAll;
import eu.epicpvp.kcore.Command.Admin.CommandGiveCoins;
import eu.epicpvp.kcore.Command.Admin.CommandGiveGems;
import eu.epicpvp.kcore.Command.Admin.CommandItem;
import eu.epicpvp.kcore.Command.Admin.CommandLocations;
import eu.epicpvp.kcore.Command.Admin.CommandMore;
import eu.epicpvp.kcore.Command.Admin.CommandPvPMute;
import eu.epicpvp.kcore.Command.Admin.CommandSocialspy;
import eu.epicpvp.kcore.Command.Admin.CommandToggle;
import eu.epicpvp.kcore.Command.Admin.CommandTp;
import eu.epicpvp.kcore.Command.Admin.CommandTpHere;
import eu.epicpvp.kcore.Command.Admin.CommandTppos;
import eu.epicpvp.kcore.Command.Admin.CommandTrackingRange;
import eu.epicpvp.kcore.Command.Admin.CommandVanish;
import eu.epicpvp.kcore.Command.Commands.CommandAmboss;
import eu.epicpvp.kcore.Command.Commands.CommandClearInventory;
import eu.epicpvp.kcore.Command.Commands.CommandDelHome;
import eu.epicpvp.kcore.Command.Commands.CommandEnchantmentTable;
import eu.epicpvp.kcore.Command.Commands.CommandEnderchest;
import eu.epicpvp.kcore.Command.Commands.CommandExt;
import eu.epicpvp.kcore.Command.Commands.CommandFeed;
import eu.epicpvp.kcore.Command.Commands.CommandFill;
import eu.epicpvp.kcore.Command.Commands.CommandHandel;
import eu.epicpvp.kcore.Command.Commands.CommandHead;
import eu.epicpvp.kcore.Command.Commands.CommandHeal;
import eu.epicpvp.kcore.Command.Commands.CommandHome;
import eu.epicpvp.kcore.Command.Commands.CommandInvsee;
import eu.epicpvp.kcore.Command.Commands.CommandKit;
import eu.epicpvp.kcore.Command.Commands.CommandMoney;
import eu.epicpvp.kcore.Command.Commands.CommandMsg;
import eu.epicpvp.kcore.Command.Commands.CommandNacht;
import eu.epicpvp.kcore.Command.Commands.CommandNear;
import eu.epicpvp.kcore.Command.Commands.CommandPotion;
import eu.epicpvp.kcore.Command.Commands.CommandR;
import eu.epicpvp.kcore.Command.Commands.CommandRemoveEnchantment;
import eu.epicpvp.kcore.Command.Commands.CommandRenameItem;
import eu.epicpvp.kcore.Command.Commands.CommandRepair;
import eu.epicpvp.kcore.Command.Commands.CommandSetHome;
import eu.epicpvp.kcore.Command.Commands.CommandSonne;
import eu.epicpvp.kcore.Command.Commands.CommandSpawn;
import eu.epicpvp.kcore.Command.Commands.CommandSpawner;
import eu.epicpvp.kcore.Command.Commands.CommandSpawnmob;
import eu.epicpvp.kcore.Command.Commands.CommandSuffix;
import eu.epicpvp.kcore.Command.Commands.CommandTag;
import eu.epicpvp.kcore.Command.Commands.CommandUserShop;
import eu.epicpvp.kcore.Command.Commands.CommandWarp;
import eu.epicpvp.kcore.Command.Commands.CommandWorkbench;
import eu.epicpvp.kcore.Command.Commands.CommandkSpawn;
import eu.epicpvp.kcore.DeliveryPet.DeliveryObject;
import eu.epicpvp.kcore.DeliveryPet.DeliveryPet;
import eu.epicpvp.kcore.GemsShop.GemsShop;
import eu.epicpvp.kcore.Gilden.GildenType;
import eu.epicpvp.kcore.Gilden.SkyBlockGildenManager;
import eu.epicpvp.kcore.Hologram.Hologram;
import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagType;
import eu.epicpvp.kcore.Inventory.InventoryBase;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.ItemShop.ItemShop;
import eu.epicpvp.kcore.JumpPad.CommandJump;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.PerkManager;
import eu.epicpvp.kcore.Kit.Command.CommandPerk;
import eu.epicpvp.kcore.Kit.Perks.PerkArrowPotionEffect;
import eu.epicpvp.kcore.Kit.Perks.PerkDoubleJump;
import eu.epicpvp.kcore.Kit.Perks.PerkDoubleXP;
import eu.epicpvp.kcore.Kit.Perks.PerkDropper;
import eu.epicpvp.kcore.Kit.Perks.PerkGetXP;
import eu.epicpvp.kcore.Kit.Perks.PerkGoldenApple;
import eu.epicpvp.kcore.Kit.Perks.PerkHat;
import eu.epicpvp.kcore.Kit.Perks.PerkHealPotion;
import eu.epicpvp.kcore.Kit.Perks.PerkItemName;
import eu.epicpvp.kcore.Kit.Perks.PerkNoFiredamage;
import eu.epicpvp.kcore.Kit.Perks.PerkNoHunger;
import eu.epicpvp.kcore.Kit.Perks.PerkNoWaterdamage;
import eu.epicpvp.kcore.Kit.Perks.PerkPotionClear;
import eu.epicpvp.kcore.Kit.Perks.PerkRunner;
import eu.epicpvp.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import eu.epicpvp.kcore.Listener.Chat.ChatListener;
import eu.epicpvp.kcore.Listener.Command.ListenerCMD;
import eu.epicpvp.kcore.Listener.EnderChest.EnderChestListener;
import eu.epicpvp.kcore.Listener.Enderpearl.EnderpearlListener;
import eu.epicpvp.kcore.Listener.EntityClick.EntityClickListener;
import eu.epicpvp.kcore.Listener.VoteListener.VoteListener;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Pet.PetManager;
import eu.epicpvp.kcore.Pet.Commands.CommandPet;
import eu.epicpvp.kcore.Pet.Shop.PlayerPetHandler;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.TeleportManager.TeleportManager;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.Updater;
import eu.epicpvp.kcore.UserDataConfig.UserDataConfig;
import eu.epicpvp.kcore.UserStores.UserStores;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilEnt;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilException;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilTime;
import lombok.Getter;

public class kSkyBlock extends JavaPlugin {
	
	@Getter
	private AntiLogoutManager antiLogout;
	@Getter
	private ClientWrapper client;
	@Getter
	private Updater Updater;
	@Getter
	private MySQL mysql;
	@Getter
	private PermissionManager permissionManager;
	@Getter
	private CommandHandler cmd;
	@Getter
	private FileConfiguration fConfig;
	@Getter
	private StatsManager statsManager;
	@Getter
	private StatsManager money;
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
	@Getter
	private ItemShop itemShop;
	
	public void onEnable(){
		try{
			long time = System.currentTimeMillis();
			loadConfig();
			this.Updater=new Updater(this);
			this.client = UtilServer.createClient(this,ClientType.OTHER, getConfig().getString("Config.Client.Host"), getConfig().getInt("Config.Client.Port"), "SkyBlock");
			this.mysql=new MySQL(getFConfig().getString("Config.MySQL.User"),getFConfig().getString("Config.MySQL.Password"),getFConfig().getString("Config.MySQL.Host"),getFConfig().getString("Config.MySQL.DB"),this);
			this.permissionManager=new PermissionManager(this);
			this.statsManager=new StatsManager(this, client, GameType.SKYBLOCK);
			this.money=new StatsManager(this, client, GameType.Money);
			this.userData=new UserDataConfig(this);
			this.hologram=new Hologram(this);
			this.hologram.RemoveText();
			this.cmd=new CommandHandler(this);
			this.base=new InventoryBase(this);
			UtilServer.createGemsShop(new GemsShop(getHologram(),getMoney(), getCmd(), getBase(), getPermissionManager(), ServerType.SKYBLOCK));
			this.petManager=new PetManager(this);
			this.petHandler= new PlayerPetHandler(ServerType.SKYBLOCK,mysql, getPetManager(), getBase(), getPermissionManager());
			this.petHandler.setAsync(true);
			this.teleport=new TeleportManager(getCmd(), getPermissionManager(), 5);
			this.perkManager=new PerkManager(this,new Perk[]{new PerkNoWaterdamage(),new PerkArrowPotionEffect(),new PerkHat(),new PerkGoldenApple(),new PerkNoHunger(),new PerkHealPotion(1),new PerkNoFiredamage(),new PerkRunner(0.35F),new PerkDoubleJump(),new PerkDoubleXP(),new PerkDropper(),new PerkGetXP(),new PerkPotionClear(),new PerkItemName(cmd)});
			this.antiLogout=new AntiLogoutManager(this,AntiLogoutType.KILL,5);
	
			this.cmd.register(CommandDebug.class, new CommandDebug());
			this.cmd.register(CommandGiveAll.class, new CommandGiveAll());
			this.cmd.register(CommandPet.class, new CommandPet(getPetHandler()));
			this.cmd.register(CommandCMDMute.class, new CommandCMDMute(this));	
			this.cmd.register(CommandPvPMute.class, new CommandPvPMute(this));	
			this.cmd.register(CommandChatMute.class, new CommandChatMute(this));
			this.cmd.register(CommandTrackingRange.class, new CommandTrackingRange());
			this.cmd.register(CommandToggle.class, new CommandToggle(this));
			this.cmd.register(CommandMoney.class, new CommandMoney(getStatsManager(),getMysql(),ServerType.SKYBLOCK));
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
			this.cmd.register(CommandDelHome.class, new CommandDelHome(getUserData()));
			this.cmd.register(CommandWarp.class, new CommandWarp(getTeleport()));
			this.cmd.register(CommandKit.class, new CommandKit(getUserData(),cmd));
			this.cmd.register(CommandSpawn.class, new CommandSpawn(getTeleport()));
			this.cmd.register(CommandClearInventory.class, new CommandClearInventory());
			this.cmd.register(CommadSkyBlock.class, new CommadSkyBlock(this));	
			this.cmd.register(CommandRenameItem.class, new CommandRenameItem());
			this.cmd.register(CommandInvsee.class, new CommandInvsee(mysql));
			this.cmd.register(CommandUserShop.class, new CommandUserShop(getTeleport()));
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
			this.cmd.register(CommandLocations.class, new CommandLocations(this));
			this.cmd.register(CommandPerk.class, new CommandPerk(perkManager));
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
			this.cmd.register(CommandGiveGems.class, new CommandGiveGems(getMoney()));
			this.cmd.register(CommandGiveCoins.class, new CommandGiveCoins(getMoney()));
			this.cmd.register(CommandAddEpics.class, new CommandAddEpics(getStatsManager()));
			
			UtilServer.createDeliveryPet(new DeliveryPet(getBase(),null,new DeliveryObject[]{
				new DeliveryObject(new String[]{"","§7Click for Vote!","","§ePvP Rewards:","§7   200 Epics","§7   1x Inventory Repair","","§eGame Rewards:","§7   25 Gems","§7   100 Coins","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},PermissionType.DELIVERY_PET_VOTE,false,28,"§aVote for EpicPvP",Material.PAPER,Material.REDSTONE_BLOCK,new Click(){
	
						@Override
						public void onClick(Player p, ActionType a,Object obj) {
							p.closeInventory();
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§7-----------------------------------------");
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+" ");
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"Vote Link:§a http://goo.gl/wxdAj4");
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+" ");
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§7-----------------------------------------");
						}
						
					},-1),
					new DeliveryObject(new String[]{"§aOnly for §eVIP§a!","","§ePvP Rewards:","§7   200 Epics","§7   10 Level","","§eGame Rewards:","§7   200 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},PermissionType.DELIVERY_PET_VIP_WEEK,true,11,"§cRank §eVIP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){
	
						@Override
						public void onClick(Player p, ActionType a,Object obj) {
							getStatsManager().add(p, StatsKey.MONEY,200);
							p.setLevel(p.getLevel()+10);
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","200"}));
						}
						
					},TimeSpan.DAY*7),
					new DeliveryObject(new String[]{"§aOnly for §6ULTRA§a!","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   300 Epics","§7   4x Diamonds","§7   4x Iron Ingot","§7   4x Gold Ingot"},PermissionType.DELIVERY_PET_ULTRA_WEEK,true,12,"§cRank §6ULTRA§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){
	
						@Override
						public void onClick(Player p, ActionType a,Object obj) {
							getStatsManager().add(p, StatsKey.MONEY,300);
							p.getInventory().addItem(new ItemStack(Material.DIAMOND,2));
							p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
							p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","300"}));
						}
						
					},TimeSpan.DAY*7),
					new DeliveryObject(new String[]{"§aOnly for §aLEGEND§a!","","§ePvP Rewards:","§7   400 Epics","§7   20 Level","","§eGame Rewards:","§7   400 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   400 Epics","§7   6x Diamonds","§7   6x Iron Ingot","§7   6x Gold Ingot"},PermissionType.DELIVERY_PET_LEGEND_WEEK,true,13,"§cRank §5LEGEND§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){
	
						@Override
						public void onClick(Player p, ActionType a,Object obj) {
							getStatsManager().add(p, StatsKey.MONEY,400);
							p.getInventory().addItem(new ItemStack(Material.DIAMOND,4));
							p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,4));
							p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,4));
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","400"}));
						}
						
					},TimeSpan.DAY*7),
					new DeliveryObject(new String[]{"§aOnly for §bMVP§a!","","§ePvP Rewards:","§7   500 Epics","§7   25 Level","","§eGame Rewards:","§7   500 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   500 Epics","§7   8x Diamonds","§7   8x Iron Ingot","§7   8x Gold Ingot"},PermissionType.DELIVERY_PET_MVP_WEEK,true,14,"§cRank §3MVP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){
	
						@Override
						public void onClick(Player p, ActionType a,Object obj) {
							getStatsManager().add(p, StatsKey.MONEY,500);
							p.getInventory().addItem(new ItemStack(Material.DIAMOND,4));
							p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,4));
							p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,4));
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","500"}));
						}
						
					},TimeSpan.DAY*7),
					new DeliveryObject(new String[]{"§aOnly for §bMVP§c+§a!","","§ePvP Rewards:","§7   600 Epics","§7   30 Level","","§eGame Rewards:","§7   600 Coins","§7   4x TTT Paesse","","§eSkyBlock Rewards:","§7   600 Epics","§7   10x Diamonds","§7   10x Iron Ingot","§7   10x Gold Ingot"},PermissionType.DELIVERY_PET_MVPPLUS_WEEK,true,15,"§cRank §9MVP§e+§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){
	
						@Override
						public void onClick(Player p, ActionType a,Object obj) {
							getStatsManager().add(p, StatsKey.MONEY,600);
							p.getInventory().addItem(new ItemStack(Material.DIAMOND,6));
							p.getInventory().addItem(new ItemStack(Material.IRON_INGOT,6));
							p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,6));
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","600"}));
						}
						
					},TimeSpan.DAY*7),
					new DeliveryObject(new String[]{"§7/twitter [TwitterName]","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","","§eSkyBlock Rewards:","§7   300 Epics","§7   15 Level"},PermissionType.DELIVERY_PET_TWITTER,false,34,"§cTwitter Reward",Material.getMaterial(351),4,new Click(){
	
						@Override
						public void onClick(Player p, ActionType a,Object obj) {
	//						String s1 = getMysql().getString("SELECT twitter FROM BG_TWITTER WHERE uuid='"+UtilPlayer.getRealUUID(p)+"'");
	//						if(s1.equalsIgnoreCase("null")){
	//							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_ACC_NOT"));
	//						}else{
	//							getPacketManager().SendPacket("DATA", new TWIITTER_IS_PLAYER_FOLLOWER(s1, p.getName()));
	//							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_CHECK"));
	//						}
						}
						
					},TimeSpan.DAY*7),
			},"§bThe Delivery Jockey!",EntityType.CHICKEN,CommandLocations.getLocation("DeliveryPet"),ServerType.SKYBLOCK,getHologram(),getMysql())
			);
	
			this.manager=new SkyBlockManager(this);
			this.ha=new CommandHomeaccept(manager);
			getAntiLogout().setStats(statsManager);
			new SkyBlockListener(this);
			new EnderpearlListener(this);
			new EnderChestListener(getUserData());
			Bukkit.getWorld("world").setStorm(false);
			AACHack a = new AACHack("SKYBLOCK", mysql,client);
			a.setAntiLogoutManager(getAntiLogout());
			this.itemShop = new ItemShop(getStatsManager(), getCmd());
			new UserStores(statsManager);
			perkManager.setPerkEntity(CommandLocations.getLocation("perk"));
			new PerkListener(perkManager);
			new BungeeCordFirewallListener(this,UtilServer.getCommandHandler());
			new ListenerCMD(this);
			new ChatListener(this,new SkyBlockGildenManager(manager, mysql, GildenType.SKY, cmd,getStatsManager()),permissionManager,getUserData());
			
			if(Calendar.getHoliday()!=null){
				switch(Calendar.holiday){
				case WEIHNACHTEN:
						new ChristmasListener(this);
					break;
				}
			}
			setTutorialCreature(CommandLocations.getLocation("tutorial"));
			UtilServer.createLagListener(this.cmd);
			new VoteListener(this,true, new Callback<String>() {
				
				@Override
				public void call(String playerName) {
					if(UtilPlayer.isOnline(playerName)){
						Player player = Bukkit.getPlayer(playerName);
						
						if(UtilServer.getDeliveryPet()!=null){
							UtilServer.getDeliveryPet().deliveryUSE(player, "§aVote for EpicPvP", true);
						}
						
						getStatsManager().addDouble(player, 200, StatsKey.MONEY);
						player.getInventory().addItem(new ItemStack(Material.DIAMOND,2));
						player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
						player.getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
						player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "VOTE_THX"));
					}
				}
			});
			DebugLog(time, 45, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, "skyblock", Bukkit.getIp(), mysql);
		}
	}
	
	public void onDisable(){
		UtilServer.disable();
	}
	
	public void setTutorialCreature(Location loc){
		Villager e = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		NameTagMessage m = new NameTagMessage(NameTagType.SERVER, e.getLocation().add(0, 2.1, 0), "§d§lTutorial");
		m.send();
		UtilEnt.setNoAI(e, true);
		//   §8»§7 
		InventoryPageBase page = new InventoryPageBase(InventorySize._54, "Tutorial Villager");
		
page.setItem(4, UtilItem.Item(new ItemStack(Material.NAME_TAG), new String[]{"§8»§7 Gems und Ränge kannst du im Onlineshop ","§7  unter §6shop.EpicPvP.de§7 kaufen."}, "§bInfo's"));
		
		page.setItem(20, UtilItem.Item(new ItemStack(Material.BARRIER), new String[]{}, "§cComing soon..."));
		
		page.setItem(28, UtilItem.Item(new ItemStack(Material.GRASS), new String[]{"§8»§7 Mit '§6/Is erstellen§7' kannst du eine eingene Insel generieren. ","§7  Anschließend kannst du mit '§6/Is§7' auf alle Funktionen und","§7  Einstellungen deiner Insel zugreifen. ","§7  Mit '§6/Spawn§7' kommst du zu der Spawn-Insel zurück. "}, "§6Insel"));
		
		page.setItem(38, UtilItem.Item(new ItemStack(Material.IRON_HELMET), new String[]{"§8»§7 Mit dem '§6/Gilde§7' Befehl kannst","§7  du eine eigene Gemeinschaft mit","§7  deinen Freunden gründen und eine","§7  gemeinsame Gilden-Insel gestalten."}, "§6Gilde"));
		
		page.setItem(31, UtilItem.Item(new ItemStack(Material.CHEST), new String[]{"§8»§7 Mit '§6/Shop§7' kannst du das Shop-Menü öffnen und","§7  unter verschiedenen Kategorien deine","§7  Items auswählen und §6kaufen§7 oder §6verkaufen§7. ","§7  Items kaufen kannst du mit einem links Klick auf","§7  das gewünschte Item und mit einem rechts Klick, ","§7  kannst du deine Items verkaufen. "}, "§6Shop"));
		
		page.setItem(24, UtilItem.Item(new ItemStack(Material.SIGN), new String[]{"§8»§7 Mit §7'§6[UserStore]§7' in der erste Zeile und den","§7  gewünschten Preis in der zweiten, kannst","§7  du deinen eigenen Shop erstellen. Darunter muss ","§7  eine Kiste, mit nur dem Item, welches du im","§7  Shop anbieten möchtest sein. Sobald du","§7  dies gemacht hast wird dir der Befehl §7'§6/Mystore§7'","§7  freigeschalten. Dies ermöglicht dir die","§7  Administration deiner Shop's.","§7  Du kannst mit '§6/Setusershop§7' deinen","§7  Shop-Warp setzen und dich mit","§7  '§6/Usershop <Spieler>§7' zu denn","§7  jeweiligen Shop telepotieren.","§7  Jeder Spieler kann fünf gratis Shop's erstellen,","§7  jeder weitere kostet dich§a 25 Gems§7.","§7  Diese erhältst du durch §7'§6/Vote§7' oder","§7  im Onlineshop:","§7  »§e shop.EpicPvP.de"}, "§6Usershop"));
		
		page.setItem(42, UtilItem.Item(new ItemStack(Material.TRIPWIRE_HOOK), new String[]{"§8»§7 Mit dem §7'§6/Handel§7' Befehl kannst","§7  du sicher mit anderen Spielern handeln.","§7  Du kannst deine Items in das linke","§7  Feld ziehen und siehst dann im rechten, ","§7  welche Items dir im Tausch dagegen angeboten","§7  werden. Unten kannst du dann auf §7'§6Accept§7'","§7  das Angebot akzeptieren. Sobald das dann","§7  auch der Tauschpartner","§7  macht, ist der Handel abgeschlossen. "}, "§6Handel"));
		
		page.setItem(34, UtilItem.Item(new ItemStack(Material.EMERALD), new String[]{"§8»§7 Du kannst mit dem Befehl '§6/Money§7","§7  deinen Kontostand überprüfen","§7  Mit verschiedensten Farmen kannst du diesen","§7  erhöhen und mit anderen Spielern handeln.","§7  Du kannst mit","§7  dem Befehl §7'§6/Money send <Spieler <Betrag>§7'","§7  deinen Freunden §6Epics§7 überweisen" }, "§6Wirtschaft"));
		
		page.fill(Material.STAINED_GLASS_PANE,7);
		UtilInv.getBase().addPage(page);
		new EntityClickListener(this, new Click(){

			@Override
			public void onClick(Player p, ActionType a, Object o) {
				p.openInventory(page);
			}
			
		}, e);
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
