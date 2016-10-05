package eu.epicpvp.kSkyblock.World.Inventory.Buttons;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.Island.IslandPermission;
import eu.epicpvp.kSkyblock.World.Island.Member;
import eu.epicpvp.kSkyblock.World.Island.kPlayer;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryYesNo;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonForMultiButtonsCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonMultiCopy;
import eu.epicpvp.kcore.TeleportManager.Teleporter;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

public class InviteButton extends ButtonMultiCopy{

	public InviteButton(InventoryPageBase page,int slot,int index, SkyBlockManager manager) {
		super(new ButtonForMultiButtonsCopy[]{new ButtonForMultiButtonsCopy(page, slot+9, new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				if(((ItemStack)object).getTypeId() == 101)return;
				kPlayer kplayer = manager.getPlayers().get(UtilPlayer.getPlayerId(player));
				if(kplayer!=null){
					if(kplayer.getMemberList().size() > index){
						player.closeInventory();
						Member m = ((Member)kplayer.getMemberList().values().toArray()[index]);
						if(!m.getPermissions().contains(IslandPermission.TELEPORT) && !UtilPlayer.isOnline(m.getIsland().getPlayerId())){
							return;
						}
						manager.getInstance().getTeleport().getTeleport().add(new Teleporter(player, m.getIsland().getHome(), 3));
					}
				}
			}
			
		}, 
		UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §6Teleportiere dich zu der §eInsel§6."),
		new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				kPlayer kplayer = manager.getPlayers().get(UtilPlayer.getPlayerId(player));
				if(kplayer!=null){
					if(kplayer.getMemberList().size() > index){
						Member m = ((Member)kplayer.getMemberList().values().toArray()[index]);
						LoadedPlayer owner = UtilServer.getClient().getPlayerAndLoad(m.getIsland().getPlayerId());
						ArrayList<String> list = new ArrayList<>();
						list.add("§7"+Zeichen.POINT.getIcon()+"§a Mitglieder der Insel§7:");
						
						for(Member m1 : m.getIsland().getMember().values()){
							LoadedPlayer mp = UtilServer.getClient().getPlayerAndLoad(m1.getPlayerId());
							list.add("§7"+Zeichen.LINE.getIcon()+"§e "+mp.getName());
						}
						
						((InventoryPageBase)object).setItem(slot, UtilItem.Item(UtilItem.Head(owner.getName()), list, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Inselowner:§e "+owner.getName()));
						return;
					}
				}
				((InventoryPageBase)object).setItem(slot, UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)3), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Nicht belegt."));
				((InventoryPageBase)object).setItem(slot+9, UtilItem.RenameItem(new ItemStack(101), " "));
			}
			
		}),
				new ButtonForMultiButtonsCopy(page, slot+18, new Click(){

					@Override
					public void onClick(Player player, ActionType type, Object object) {
						if(((ItemStack)object).getTypeId() == 101)return;
						InventoryYesNo q = new InventoryYesNo("Sicher?", new Click(){

							@Override
							public void onClick(Player player, ActionType type, Object object) {
								kPlayer kplayer = manager.getPlayers().get(UtilPlayer.getPlayerId(player));
								if(kplayer!=null){
									if(kplayer.getMemberList().size() > index){
										Member m = ((Member)kplayer.getMemberList().values().toArray()[index]);
										String playername = UtilServer.getClient().getPlayerAndLoad(m.getIsland().getPlayerId()).getName();
										player.sendMessage(TranslationHandler.getPrefixAndText(player, "SKYBLOCK_MEMBER_LEAVE_SELF",playername));
										m.delete();
										player.closeInventory();
										
										if(UtilPlayer.isOnline(playername)){
											Player owner = Bukkit.getPlayer(playername);
											if(owner!=null&&owner.isOnline()){
												owner.sendMessage(TranslationHandler.getPrefixAndText(owner, "SKYBLOCK_MEMBER_LEAVE",player.getName()));
											}
										}
									}
								}
							}
							
						}, new Click(){

							@Override
							public void onClick(Player player, ActionType type, Object object) {
								player.closeInventory();
							}
							
						});
						UtilInv.getBase().addAnother(q);
						player.openInventory(q);
					}
					
				}, 
				UtilItem.RenameItem(new ItemStack(351,1,(byte)1), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §cKlicke, um die Insel zu verlassen."),
				new Click(){

					@Override
					public void onClick(Player player, ActionType type, Object object) {
						kPlayer kplayer = manager.getPlayers().get(UtilPlayer.getPlayerId(player));
						if(kplayer!=null){
							if(kplayer.getMemberList().size() > index){
								return;
							}
						}
						((InventoryPageBase)object).setItem(slot+18, UtilItem.RenameItem(new ItemStack(101), " "));
					}
					
				})});
	}



}
