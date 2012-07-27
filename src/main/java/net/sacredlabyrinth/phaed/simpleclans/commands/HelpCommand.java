package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Max
 */
public class HelpCommand extends GenericConsoleCommand
{

    private static final int CMDS_PER_PAGE = 12;
    private SimpleClans plugin;

    public HelpCommand(SimpleClans plugin)
    {
        super("Help");
        this.plugin = plugin;
        setUsages(String.format("/%s help §8[page#]", plugin.getSettingsManager().getCommandClan()));
        setArgumentRange(0, 1);
        setIdentifiers("simpleclans", plugin.getSettingsManager().getCommandClan(), "help");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {
            }
        }

        List<Command> sortCommands = plugin.getCommandManager().getCommands();
        List<Command> commands = new ArrayList<Command>();

        ClanPlayer cp = null;
        if (sender instanceof Player) {
            cp = plugin.getClanManager().getClanPlayer(sender.getName());
        }

        // Build list of permitted commands
        for (Command command : sortCommands) {
            if (command.getMenu(cp, sender) != null) {
                if (sender instanceof Player) {
                    commands.add(command);
                } else {
                    if (command instanceof GenericConsoleCommand) {
                        commands.add(command);
                    }
                }
            }
        }

        int numPages = commands.size() / CMDS_PER_PAGE;
        if (commands.size() % CMDS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }
        sender.sendMessage(plugin.getSettingsManager().getServerName() + " <" + (page + 1) + "/" + numPages + "> §7" + plugin.getLang("clan.commands"));
        int start = page * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        if (end > commands.size()) {
            end = commands.size();
        }
        StringBuilder menu = new StringBuilder();
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);

            String commandMenu = cmd.getMenu(cp, sender);

            menu.append("   §b").append(commandMenu).append("\n");


//            String[] commandMenu = cmd.getMenu();
//            if (commandMenu != null) {
//                for (String usage : commandMenu) {
//                    menu.append("   §b").append(usage).append("\n");
//                }
//            }
        }
        sender.sendMessage(menu.toString());
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        return MessageFormat.format("/{0} help §8[page#]", plugin.getSettingsManager().getCommandClan());
    }
}
