package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class CreateCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public CreateCommand(SimpleClans plugin)
    {
        super("CreateCommand");
        this.plugin = plugin;
        setArgumentRange(2, 20);
        setUsages(MessageFormat.format(plugin.getLang("usage.create"), plugin.getSettingsManager().getCommandClan()) + "\n" + ChatColor.RED + plugin.getLang("example.clan.create"));
        setIdentifiers(plugin.getLang("create.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp == null) {
            if (plugin.getPermissionsManager().has(sender, "simpleclans.leader.create")) {
                if (plugin.getSettingsManager().isePurchaseCreation()) {
                    return MessageFormat.format(plugin.getLang("0.create.tag.name.1.purchase.a.new.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                } else {
                    return MessageFormat.format(plugin.getLang("0.create.tag.name.1.create.a.new.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                }
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.create")) {
            String tag = args[0];
            String cleanTag = Helper.cleanTag(args[0]);

            String name = Helper.toMessage(Helper.removeFirst(args));

            boolean bypass = plugin.getPermissionsManager().has(player, "simpleclans.mod.bypass");

            if (bypass || cleanTag.length() <= plugin.getSettingsManager().getTagMaxLength()) {

                if (bypass || cleanTag.length() > plugin.getSettingsManager().getTagMinLength()) {

                    if (bypass || !plugin.getSettingsManager().hasDisallowedColor(tag)) {

                        if (bypass || Helper.stripColors(name).length() <= plugin.getSettingsManager().getClanMaxLength()) {

                            if (bypass || Helper.stripColors(name).length() > plugin.getSettingsManager().getClanMinLength()) {

                                if (cleanTag.matches("[0-9a-zA-Z]*")) {

                                    if (!name.contains("&")) {

                                        if (bypass || !plugin.getSettingsManager().isDisallowedWord(cleanTag.toLowerCase())) {
                                            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                                            if (cp == null) {
                                                if (!plugin.getClanManager().isClan(cleanTag)) {
                                                    if (plugin.getClanManager().purchaseCreation(player)) {
                                                        plugin.getClanManager().createClan(player, tag, name);

                                                        Clan clan = plugin.getClanManager().getClan(tag);
                                                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.created"), name));
                                                        plugin.getStorageManager().updateClan(clan);

                                                        if (plugin.getSettingsManager().isRequireVerification()) {
                                                            boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

                                                            if (!verified) {
                                                                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("get.your.clan.verified.to.access.advanced.features"));
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.with.this.tag.already.exists"));
                                                }
                                            } else {
                                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("you.must.first.resign"), cp.getClan().getName()));
                                            }
                                        } else {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("that.tag.name.is.disallowed"));
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.name.cannot.contain.color.codes"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.name.must.be.longer.than.characters"), plugin.getSettingsManager().getClanMinLength()));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.name.cannot.be.longer.than.characters"), plugin.getSettingsManager().getClanMaxLength()));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.tag.cannot.contain.the.following.colors"), plugin.getSettingsManager().getDisallowedColorString()));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.tag.must.be.longer.than.characters"), plugin.getSettingsManager().getTagMinLength()));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.tag.cannot.be.longer.than.characters"), plugin.getSettingsManager().getTagMaxLength()));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
