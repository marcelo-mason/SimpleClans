package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.util.ArrayList;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Max
 */
public class HelpCommand extends GenericCommand
{

    private static final int CMDS_PER_PAGE = 9;
    private SimpleClans plugin;

    public HelpCommand(SimpleClans plugin)
    {
        super("Help");
        this.plugin = plugin;
        setUsage(String.format("/%s help §8[page#]", plugin.getSettingsManager().getCommandClan()));
        setArgumentRange(0, 1);
        setMenuName(null);
        setIdentifiers(plugin.getSettingsManager().getCommandClan(), "help");
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

        List<Command> sortCommands = plugin.getBetaCommandManager().getCommands();
        List<Command> commands = new ArrayList<Command>();

        // Build list of permitted commands
        for (Command command : sortCommands) {
            commands.add(command);
        }

        int numPages = commands.size() / CMDS_PER_PAGE;
        if (commands.size() % CMDS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }
        sender.sendMessage("§c-----[ " + plugin.getSettingsManager().getServerName() + " <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        if (end > commands.size()) {
            end = commands.size();
        }
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);
            sender.sendMessage("   §b" + cmd.getUsage());
        }
    }
}
