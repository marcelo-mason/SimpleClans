package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class VitalsCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public VitalsCommand(SimpleClans plugin)
    {
        super("Vitals");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.vitals"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("vitals.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isTrusted() && cp.getClan().isVerified() && plugin.getPermissionsManager().has(sender, "simpleclans.member.vitals")) {
                return MessageFormat.format(plugin.getLang("0.vitals.1.view.your.clan.member.s.vitals"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.vitals")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {

                        ChatBlock chatBlock = new ChatBlock();
                        ChatBlock.sendBlank(player);
                        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor + " " + plugin.getLang("vitals") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                        ChatBlock.sendBlank(player);
                        ChatBlock.sendMessage(player, headColor + plugin.getLang("weapons") + ": " + MessageFormat.format(plugin.getLang("0.s.sword.1.2.b.bow.3.4.a.arrow"), ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE));
                        ChatBlock.sendMessage(player, headColor + plugin.getLang("materials") + ": " + ChatColor.AQUA + plugin.getLang("diamond") + ChatColor.DARK_GRAY + ", " + ChatColor.YELLOW + plugin.getLang("gold") + ChatColor.DARK_GRAY + ", " + ChatColor.GRAY + plugin.getLang("stone") + ChatColor.DARK_GRAY + ", " + ChatColor.WHITE + plugin.getLang("iron") + ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + plugin.getLang("wood"));

                        ChatBlock.sendBlank(player);

                        chatBlock.setFlexibility(true, false, false, false, false, false);
                        chatBlock.setAlignment("l", "l", "l", "c", "c", "c");

                        chatBlock.addRow("  " + headColor + plugin.getLang("name"), plugin.getLang("health"), plugin.getLang("hunger"), plugin.getLang("food"), plugin.getLang("armor"), plugin.getLang("weapons"));

                        List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getLeaders());
                        members.addAll(Helper.stripOffLinePlayers(clan.getNonLeaders()));

                        for (ClanPlayer cpm : members) {
                            Player p = plugin.getServer().getPlayer(cpm.getName());

                            if (p != null) {
                                String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cpm.getName();
                                String health = plugin.getClanManager().getHealthString(p.getHealth());
                                String hunger = plugin.getClanManager().getHungerString(p.getFoodLevel());
                                String armor = plugin.getClanManager().getArmorString(p.getInventory());
                                String weapons = plugin.getClanManager().getWeaponString(p.getInventory());
                                String food = plugin.getClanManager().getFoodString(p.getInventory());

                                chatBlock.addRow("  " + name, ChatColor.RED + health, hunger, ChatColor.WHITE + food, armor, weapons);
                            }
                        }

                        chatBlock.addRow(" -- Allies -- ", "", "", "", "", "");

                        Set<ClanPlayer> allAllyMembers = clan.getAllAllyMembers();

                        for (ClanPlayer cpm : allAllyMembers) {
                            Player p = plugin.getServer().getPlayer(cpm.getName());

                            if (p != null) {
                                String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cpm.getName();
                                String health = plugin.getClanManager().getHealthString(p.getHealth());
                                String hunger = plugin.getClanManager().getHungerString(p.getFoodLevel());
                                String armor = plugin.getClanManager().getArmorString(p.getInventory());
                                String weapons = plugin.getClanManager().getWeaponString(p.getInventory());
                                String food = plugin.getClanManager().getFoodString(p.getInventory());

                                chatBlock.addRow("  " + name, ChatColor.RED + health, hunger, ChatColor.WHITE + food, armor, weapons);
                            }
                        }

                        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                        if (more) {
                            plugin.getStorageManager().addChatBlock(player, chatBlock);
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
                        }

                        ChatBlock.sendBlank(player);

                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.vitals"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
