package eu.epicpvp.kSkyblock.World.Inventory.Buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kSkyblock.World.Island.IslandPermission;
import eu.epicpvp.kSkyblock.World.Island.Member;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryYesNo;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventoryCopy;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

public class MemberButton extends ButtonCopy{

	public MemberButton(int slot,int index,InventoryCopy memberPage, SkyBlockManager manager) {
		super(new Click() {
			
			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
					
				if(is!=null){
					if(is.getMember().size() > index){
						LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(((Member)is.getMember().values().toArray()[index]).getPlayerId());
							
						((InventoryPageBase)object).setItem(slot, UtilItem.RenameItem(UtilItem.Head(loadedplayer.getName()), "§e"+loadedplayer.getName()));
					}
				}
			}
		}, new Click() {
			
			@Override
			public void onClick(Player player, ActionType type, Object object) {
				if(((ItemStack)object).getTypeId() != 101){
						Island is = manager.getIsland(player);
						
						if(is!=null){
							if(is.getMember().size() > index){
								Member m = ((Member)is.getMember().values().toArray()[index]);
								String playername = UtilServer.getClient().getPlayerAndLoad(m.getPlayerId()).getName();
								InventoryPageBase option = new InventoryPageBase(InventorySize._54, "Options");
								option.setItem(4, UtilItem.Item(UtilItem.Head(playername),new String[]{" ","§7Hier kannst du die Rechte dieses","§7Spielers bearbeiten."," "},"§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§e "+playername));
								option.addButton(0, new ButtonOpenInventoryCopy(memberPage,UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
								option.addButton(8, new ButtonBase(new Click(){

									@Override
									public void onClick(Player player, ActionType type, Object object) {
										InventoryYesNo q = new InventoryYesNo("Kick Spieler", new Click(){

											@Override
											public void onClick(Player player, ActionType type, Object object) {
												Player target = UtilPlayer.searchExact(m.getPlayerId());
												player.closeInventory();
												m.getIsland().removeMember(m.getPlayerId());
												
												if(target!=null){
													target.sendMessage(TranslationHandler.getPrefixAndText(target, "SKYBLOCK_MEMBER_KICK",player.getName()));
													player.sendMessage(TranslationHandler.getPrefixAndText(player, "SKYBLOCK_MEMBER_KICK_SELF", target.getName()));
												}else{
													player.sendMessage(TranslationHandler.getPrefixAndText(player, "SKYBLOCK_MEMBER_KICK_SELF",UtilServer.getClient().getPlayerAndLoad(m.getPlayerId()).getName()));
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
									
								}, UtilItem.Item(new ItemStack(351,1,(byte)1),new String[]{" ","§7Hier kannst du diesen Spieler","§7von deiner Insel kicken."," "}, "§cSpieler von der Insel kicken")));
								
								for(IslandPermission perm : IslandPermission.values()){
									option.setItem(perm.getSlot(), perm.getItem());
									
									option.addButton(perm.getSlot()+9, new ButtonBase(new Click(){

										@Override
										public void onClick(Player player, ActionType type, Object object) {
										if(m.getPermissions().contains(perm)){
												m.remove(perm);
												option.setItem(perm.getSlot()+9, UtilItem.RenameItem(new ItemStack(351,1,(byte)8), "§cOff"));
											}else{
												m.add(perm);
												option.setItem(perm.getSlot()+9, UtilItem.RenameItem(new ItemStack(351,1,(byte)10), "§aOn"));
											}
										}
											
									}, UtilItem.RenameItem(new ItemStack(351,1,(byte) (m.getPermissions().contains(perm) ? 10 : 8) ), (m.getPermissions().contains(perm) ? "§aOn" : "§cOff") )));
								}
								
								UtilInv.getBase().addAnother(option);
								player.openInventory(option);
							}
					}
				}
			}
		}, UtilItem.RenameItem(new ItemStack(101), "§7Leer"));
	}

}
