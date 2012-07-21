package net.sacredlabyrinth.phaed.simpleclans.beta;

import java.util.ArrayList;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.beta.BasicCommand;
import net.sacredlabyrinth.phaed.simpleclans.beta.Command;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Max
 */
public class HelpCommand extends BasicCommand
{

    private static final int CMDS_PER_PAGE = 4;
    private SimpleClans plugin;

    public HelpCommand(SimpleClans plugin)
    {
        super("Help");
        this.plugin = plugin;
        setDescription("Displays the help menu");
        setUsage("/clan help §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("clan", "help");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
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
            if (command.isShownOnHelpMenu()) {
                if (command.getPermission() == null || sender.hasPermission(command.getPermission())) {
                    commands.add(command);
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
        sender.sendMessage("§c-----[ " + "§fDreamZ Help <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        if (end > commands.size()) {
            end = commands.size();
        }
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);
            sender.sendMessage("   §b" + cmd.getUsage());
        }

 //       sender.sendMessage("§cFor more info on a particular command, type §f/<command> ?");

        return true;
    }
}
