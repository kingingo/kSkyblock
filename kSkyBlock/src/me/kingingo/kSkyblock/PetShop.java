package me.kingingo.kSkyblock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Inventory.Item.SalesPackageBase;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Pet.Events.PetCreateEvent;
import me.kingingo.kcore.Pet.Setting.PetSetting;
import me.kingingo.kcore.Pet.Shop.IPetShop;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftAgeable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PetShop extends InventoryBase implements IPetShop{

	@Getter
	private PetManager manager;
	@Getter
	private PermissionManager permManager;
	@Getter
	private ArrayList<Player> change_settings = new ArrayList<>();
	private HashMap<UUID,String> settings = new HashMap<>();
	
	public PetShop(final PetManager manager,final PermissionManager permManager){
		super(manager.getInstance(),InventorySize._9.getSize(),"Pet-Shop");
		this.manager=manager;
		this.permManager=permManager;
		this.manager.setShop(this);
		this.manager.setSetting(true);
		this.permManager.getMysql().Update("CREATE TABLE IF NOT EXISTS list_pet_sky(uuid varchar(100),pet varchar(100))");
		
		this.manager.getSetting_list().put(EntityType.PIG, new PetSetting(manager,EntityType.PIG,UtilItem.RenameItem(new ItemStack(Material.MONSTER_EGG,1,(byte) 90), "§aPig")));
		this.manager.getSetting_list().put(EntityType.ZOMBIE, new PetSetting(manager,EntityType.ZOMBIE,UtilItem.RenameItem(new ItemStack(Material.MONSTER_EGG,1,(byte) 57), "§aZombie")));
		this.manager.getSetting_list().put(EntityType.OCELOT, new PetSetting(manager,EntityType.OCELOT,UtilItem.RenameItem(new ItemStack(Material.MONSTER_EGG,1,(byte) 98), "§aOcelot")));
		this.manager.getSetting_list().put(EntityType.CHICKEN, new PetSetting(manager,EntityType.CHICKEN,UtilItem.RenameItem(new ItemStack(Material.MONSTER_EGG,1,(byte) 93), "§aChicken")));
		
		getMain().addButton(2, new SalesPackageBase(new Click(){
			public void onClick(Player player, ActionType type,Object object) {
				if(permManager.hasPermission(player, kPermission.PET_OCELOT)||permManager.hasPermission(player, kPermission.PET_ALL)){
					manager.AddPetOwner(player, "Ocelot", EntityType.OCELOT, player.getLocation());
					if(!manager.getShop().getChange_settings().contains(player))manager.getShop().getChange_settings().add(player);
					player.closeInventory();
				}else{
					player.sendMessage(Text.PREFIX.getText()+"§cDu musst das Pet im Online-Store kaufen §bShop.EpicPvP.de");
					player.closeInventory();
				}
			}
			
		}, Material.MONSTER_EGG,98, "§aOcelot", new String[]{""}));
		
		getMain().addButton(3, new SalesPackageBase(new Click(){
			public void onClick(Player player, ActionType type,Object object) {
				if(permManager.hasPermission(player, kPermission.PET_ZOMBIE)||permManager.hasPermission(player, kPermission.PET_ALL)){
					manager.AddPetOwner(player, "Zombie", EntityType.ZOMBIE, player.getLocation());
					if(!manager.getShop().getChange_settings().contains(player))manager.getShop().getChange_settings().add(player);
					player.closeInventory();
				}else{
					player.sendMessage(Text.PREFIX.getText()+"§cDu musst das Pet im Online-Store kaufen §bShop.EpicPvP.de");
					player.closeInventory();
				}
			}
			
		}, Material.MONSTER_EGG,57, "§aZombie", new String[]{""}));
		
		getMain().addButton(4, new SalesPackageBase(new Click(){
			public void onClick(Player player, ActionType type,Object object) {
				if(permManager.hasPermission(player, kPermission.PET_PIG)||permManager.hasPermission(player, kPermission.PET_ALL)){
					manager.AddPetOwner(player, "Pig", EntityType.PIG, player.getLocation());
					if(!manager.getShop().getChange_settings().contains(player))manager.getShop().getChange_settings().add(player);
					player.closeInventory();
				}else{
					player.sendMessage(Text.PREFIX.getText()+"§cDu musst das Pet im Online-Store kaufen §bShop.EpicPvP.de");
					player.closeInventory();
				}
			}
			
		}, Material.MONSTER_EGG,90, "§aPig", new String[]{""}));
		
		getMain().addButton(5, new SalesPackageBase(new Click(){
			public void onClick(Player player, ActionType type,Object object) {
				if(permManager.hasPermission(player, kPermission.PET_RABBIT)||permManager.hasPermission(player, kPermission.PET_ALL)){
					manager.AddPetOwner(player, "Chicken", EntityType.CHICKEN, player.getLocation());
					if(!manager.getShop().getChange_settings().contains(player))manager.getShop().getChange_settings().add(player);
					player.closeInventory();
				}else{
					player.sendMessage(Text.PREFIX.getText()+"§cDu musst das Pet im Online-Store kaufen §bShop.EpicPvP.de");
					player.closeInventory();
				}
			}
			
		}, Material.MONSTER_EGG,93, "§aChicken", new String[]{""}));
		
		getMain().fill(Material.STAINED_GLASS_PANE,(byte)7);
	}
	
	public String toString(Entity c){
		
		String sql = "ENTITYTYPE:"+(c.getPassenger()!=null&&c.getPassenger().getType()!=EntityType.PLAYER ? c.getPassenger().getType().name() : c.getType().name())+"-/-CUSTOMNAME:"+c.getCustomName()+"-/-";
		
		if(c instanceof CraftAgeable){
			CraftAgeable ca = (CraftAgeable)c;
			sql=sql+"AGE:"+ca.getAge()+"-/-";
		}else if(c instanceof Zombie){
			Zombie ca = (Zombie)c;
			sql=sql+"AGE:"+ca.isBaby()+"-/-";
			sql=sql+"VILLAGER:"+ca.isVillager()+"-/-";
		}
		
		if(c instanceof Sheep){
			Sheep s = (Sheep)c;
			sql=sql+"SHEEP:"+s.getColor().name()+"-/-";
		}else if(c instanceof Zombie||c instanceof PigZombie){
			sql=sql+"EQUIP:"+UtilInv.itemStackArrayToBase64(((Creature)c).getEquipment().getArmorContents())+"-/-";
			sql=sql+"ITEM:"+((Creature)c).getEquipment().getItemInHand().getTypeId()+"-/-";
		}else if(c instanceof Wolf){
			sql=sql+"ANGRY:"+((Wolf)c).isAngry()+"-/-";
		}else if(c instanceof Creeper){
			sql=sql+"POWERED:"+((Creeper)c).isPowered()+"-/-";
		}else if(c instanceof Slime){
			sql=sql+"SLIME_SIZE:"+((Slime)c).getSize()+"-/-";
		}else if(c instanceof Rabbit){
			sql=sql+"RABBITTYPE:"+((Rabbit)c).getRabbitType().name()+"-/-";
		}else if(c instanceof Horse){
			sql=sql+"COLOR:"+((Horse)c).getColor().name()+"-/-";
			sql=sql+"VARIANT:"+((Horse)c).getVariant().name()+"-/-";
			sql=sql+"ARMOR:"+( ((Horse)c).getInventory().getArmor()==null ? 0 : ((Horse)c).getInventory().getArmor().getTypeId() )+"-/-";
			sql=sql+"STYLE:"+((Horse)c).getStyle().name()+"-/-";
		}
		return sql;
	}
	
	public void DeletePetSettings(Player player){
		getPermManager().getMysql().Update("DELETE FROM list_pet_sky WHERE uuid='"+UtilPlayer.getRealUUID(player)+"'");
	}
	
	public void InsertPetSettings(Player player){
		if(manager.getActivePetOwners().containsKey(player.getName().toLowerCase())){
			Entity c = manager.getActivePetOwners().get(player.getName().toLowerCase());
			getPermManager().getMysql().Update("INSERT INTO list_pet_sky (uuid,pet) VALUES ('"+UtilPlayer.getRealUUID(player)+"','"+toString(c)+"');");
		}
	}
	
	public kPermission getPerm(EntityType type){
		switch(type){
		case CHICKEN: return kPermission.PET_CHICKEN;
		case CREEPER: return kPermission.PET_CREEPER;
		case COW: return kPermission.PET_COW;
		case IRON_GOLEM: return kPermission.PET_IRON_GOLEM;
		case OCELOT: return kPermission.PET_OCELOT;
		case ZOMBIE: return kPermission.PET_ZOMBIE;
		case WOLF: return kPermission.PET_WOLF;
		case SHEEP: return kPermission.PET_SHEEP;
		case PIG: return kPermission.PET_PIG;
		case PIG_ZOMBIE: return kPermission.PET_PIGZOMBIE;
		case SPIDER: return kPermission.PET_SPIDER;
		case SQUID: return kPermission.PET_SQUID;
		default:
			return kPermission.NONE;
		}
	}
	
	public void loadPetSettings(UUID uuid){
		String sql = getPermManager().getMysql().getString("SELECT `pet` FROM `list_pet_sky` WHERE uuid='"+uuid+"'");
		if(!sql.equalsIgnoreCase("null"))settings.put(uuid, sql);
	}
	
	public void loadPetSettings(Player player,String sql){
		if(!sql.equalsIgnoreCase("null")){
			int a = 1;
			String[] split = sql.split("-/-");
			if(permManager.hasPermission(player, getPerm(EntityType.valueOf( split[0].split(":")[1] ))) || permManager.hasPermission(player, kPermission.PET_ALL)){
				getManager().AddPetOwner(player, split[a].split(":")[1], ( EntityType.valueOf( split[0].split(":")[1] )==EntityType.SQUID ? EntityType.OCELOT : EntityType.valueOf( split[0].split(":")[1] ) ) , player.getLocation());
				Entity c = getManager().getActivePetOwners().get(player.getName().toLowerCase());
			
			if(EntityType.valueOf( split[0].split(":")[1] ) == EntityType.SQUID){
				((Creature)manager.GetPet(player)).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,100000*20, 2));
				c=player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.SQUID);
				manager.GetPet(player).setPassenger( c );
			}
			
			if(c instanceof CraftAgeable){
				CraftAgeable ca = (CraftAgeable)c;
				a++;
				if(split[a]!=null)ca.setAge( Integer.valueOf( split[a].split(":")[1] ) );
				if(split[a]!=null)ca.setAgeLock(true);
			}else if(c instanceof Zombie){
				Zombie ca = (Zombie)c;
				a++;
				if(split[a]!=null)ca.setBaby( Boolean.valueOf( split[a].split(":")[1] ) );
				a++;
				if(split[a]!=null)ca.setVillager( Boolean.valueOf( split[a].split(":")[1]) );
			}
			
			if(c instanceof Sheep){
				Sheep s = (Sheep)c;
				a++;
				if(split[a]!=null)s.setColor( DyeColor.valueOf( split[a].split(":")[1] ) );
				
			}else if(c instanceof Zombie||c instanceof PigZombie){
				a++;
				try {
					if(split[a]!=null)((Creature)c).getEquipment().setArmorContents( UtilInv.itemStackArrayFromBase64(split[a].split(":")[1]) );
				} catch (IOException e) {
					e.printStackTrace();
				}
				a++;
				try {
					if(split[a]!=null)((Creature)c).getEquipment().setItemInHand( new ItemStack( Integer.valueOf(split[a].split(":")[1]) ) );
				} catch (NumberFormatException e) {
				a--;
				}
			}else if(c instanceof Wolf){
				a++;
				if(split[a]!=null)((Wolf)c).setAngry( Boolean.valueOf( split[a].split(":")[1] ) );
			}else if(c instanceof Creeper){
				a++;
				if(split[a]!=null)((Creeper)c).setPowered( Boolean.valueOf( split[a].split(":")[1] ) );
			}else if(c instanceof Slime){
				a++;
				if(split[a]!=null)((Slime)c).setSize( Integer.valueOf(String.valueOf( split[a].split(":")[1]) ) );
			}else if(c instanceof Rabbit){
				a++;
				if(split[a]!=null)((Rabbit)c).setRabbitType( Type.valueOf( String.valueOf( split[a].split(":")[1] ) ) );
			}else if(c instanceof Horse){
				a++;
				if(split[a]!=null)((Horse)c).setColor( Horse.Color.valueOf( String.valueOf( split[a].split(":")[1] ) ) );
				a++;
				if(split[a]!=null)((Horse)c).setVariant( Variant.valueOf( String.valueOf( split[a].split(":")[1] ) ) );
				a++;
				if(split[a]!=null)((Horse)c).getInventory().setArmor( new ItemStack( Integer.valueOf(split[a].split(":")[1]) ) );
				a++;
				if(split[a]!=null)((Horse)c).setStyle( Horse.Style.valueOf( String.valueOf(split[a].split(":")[1] )) );
			}
			
			}else{
				DeletePetSettings(player);
			}
		}
	}
	
	Entity c;
	@EventHandler
	public void Ve(VehicleEnterEvent ev){
		if(ev.getVehicle() instanceof Horse&&ev.getEntered() instanceof Player){
			if(getManager().getActivePetOwners().containsKey(((Player)ev.getEntered()).getName().toLowerCase())){
				c=getManager().getActivePetOwners().get(((Player)ev.getEntered()).getName().toLowerCase());
				if(c.getEntityId()==ev.getVehicle().getEntityId()){
					return;
				}
			 }
			ev.setCancelled(true);
		}
	}
	
	public void loadPetSettings(Player player){
		String sql = getPermManager().getMysql().getString("SELECT `pet` FROM `list_pet_sky` WHERE uuid='"+UtilPlayer.getRealUUID(player)+"'");
		loadPetSettings(player, sql);
	}
	
	public void UpdatePetSettings(Player player){
		DeletePetSettings(player);
		InsertPetSettings(player);
	}
	
	@EventHandler
	public void Create(PetCreateEvent ev){
		if(ev.getPet() instanceof Horse){
			((Horse)ev.getPet()).getInventory().setSaddle(new ItemStack(Material.SADDLE));
		}
	}
	
	UUID player;
	@EventHandler
	public void Place(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC_3)return;
		for(int i = 0; i < settings.size(); i++){
			player=((UUID)settings.keySet().toArray()[i]);
			if(UtilPlayer.isOnline( player )){
				loadPetSettings(Bukkit.getPlayer(player), settings.get(player));
				settings.remove(player);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void Join(AsyncPlayerPreLoginEvent ev){
		loadPetSettings(UtilPlayer.getRealUUID(ev.getName(), ev.getUniqueId()));
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(change_settings.contains(ev.getPlayer())){
			change_settings.remove(ev.getPlayer());
			UpdatePetSettings(ev.getPlayer());
		}
		manager.RemovePet(ev.getPlayer(), true);
	}
	
}
