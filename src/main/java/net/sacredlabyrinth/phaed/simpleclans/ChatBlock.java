package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

/**
 *
 * @author phaed
 */
public class ChatBlock
{

    private static final int lineLength = 319;
    private ArrayList<Boolean> columnFlexes = new ArrayList<Boolean>();
    private ArrayList<Integer> columnSizes = new ArrayList<Integer>();
    private ArrayList<String> columnAlignments = new ArrayList<String>();
    private LinkedList<String[]> rows = new LinkedList<String[]>();
    private String color = "";
    /**
     *
     */
    public static final Logger log = Logger.getLogger("Minecraft");

    /**
     *
     * @param columnAlignment
     */
    public void setAlignment(String... columnAlignment)
    {
        columnAlignments.addAll(Arrays.asList(columnAlignment));
    }

    /**
     *
     * @param columnFlex
     */
    public void setFlexibility(boolean... columnFlex)
    {
        for (boolean flex : columnFlex) {
            columnFlexes.add(flex);
        }
    }

    /**
     *
     * @param columnPercentages
     * @param prefix
     */
    public void setColumnSizes(String prefix, double... columnPercentages)
    {
        int ll = lineLength;

        if (prefix != null) {
            ll = lineLength - (int) msgLength(prefix);
        }

        for (double percentage : columnPercentages) {
            columnSizes.add((int) Math.floor((percentage / 100) * ll));
        }
    }

    /**
     *
     * @return
     */
    public boolean hasContent()
    {
        return rows.size() > 0;
    }

    /**
     *
     * @param contents
     */
    public void addRow(String... contents)
    {
        rows.add(contents);
    }

    /**
     *
     * @return
     */
    public int size()
    {
        return rows.size();
    }

    /**
     *
     * @return
     */
    public boolean isEmpty()
    {
        return rows.size() == 0;
    }

    /**
     *
     */
    public void clear()
    {
        rows.clear();
    }

    /**
     *
     * @param player
     * @return
     */
    public boolean sendBlock(CommandSender player)
    {
        return sendBlock(player, null, 0);
    }

    /**
     *
     * @param player
     * @param prefix
     * @return
     */
    public boolean sendBlock(CommandSender player, String prefix)
    {
        return sendBlock(player, prefix, 0);
    }

    /**
     *
     * @param player
     * @param amount
     * @return
     */
    public boolean sendBlock(CommandSender player, int amount)
    {
        return sendBlock(player, null, amount);
    }

    /**
     *
     * @param player
     * @param prefix
     * @param amount
     * @return
     */
    boolean sendBlock(CommandSender player, String prefix, int amount)
    {
        if (player == null) {
            return false;
        }

        if (rows.size() == 0) {
            return false;
        }

        if (amount == 0) {
            amount = rows.size();
        }

        boolean prefix_used = prefix == null;

        String empty_prefix = ChatBlock.makeEmpty(prefix);

        // if no column sizes provided then
        // make some up based on the data

        if (columnSizes.isEmpty()) {
            // generate columns sizes

            for (int i = 0; i < rows.get(0).length; i++) {
                columnSizes.add(getMaxWidth(i) + 4);
            }
        }

        // size up all sections

        for (int i = 0; i < amount; i++) {
            if (rows.size() == 0) {
                continue;
            }

            List<String> measuredCols = new ArrayList<String>();
            String row[] = rows.pollFirst();

            for (int sid = 0; sid < row.length; sid++) {
                String col = "";
                String section = row[sid];
                double colsize = (columnSizes.size() >= (sid + 1)) ? columnSizes.get(sid) : 0;
                String align = (columnAlignments.size() >= (sid + 1)) ? columnAlignments.get(sid) : "l";

                if (align.equalsIgnoreCase("r")) {
                    if (msgLength(section) > colsize) {
                        col = cropLeftToFit(section, colsize);
                    } else if (msgLength(section) < colsize) {
                        col = paddLeftToFit(section, colsize);
                    }
                } else if (align.equalsIgnoreCase("l")) {
                    if (msgLength(section) > colsize) {
                        col = cropRightToFit(section, colsize);
                    } else if (msgLength(section) < colsize) {
                        col = paddRightToFit(section, colsize);
                    }
                } else if (align.equalsIgnoreCase("c")) {
                    if (msgLength(section) > colsize) {
                        col = cropRightToFit(section, colsize);
                    } else if (msgLength(section) < colsize) {
                        col = centerInLineOf(section, colsize);
                    }
                }

                measuredCols.add(col);
            }

            // add in spacings

            int colspacing = 12;
            int availableSpacing = colspacing;

            while (calculatedRowSize(measuredCols) < lineLength && availableSpacing > 0) {
                for (int j = 0; j < measuredCols.size(); j++) {
                    String col = measuredCols.get(j);
                    measuredCols.set(j, col + " ");

                    if (calculatedRowSize(measuredCols) >= lineLength) {
                        break;
                    }

                }

                availableSpacing -= 4;
            }

            // cut off from flexible columns if too big

            if (columnFlexes.size() == measuredCols.size()) {
                while (calculatedRowSize(measuredCols) > lineLength) {
                    boolean didFlex = false;

                    for (int j = 0; j < measuredCols.size(); j++) {
                        boolean flex = columnFlexes.get(j);

                        if (flex) {
                            String col = measuredCols.get(j);

                            if (col.length() > 0) {
                                measuredCols.set(j, col.substring(0, col.length() - 1));
                                didFlex = true;
                            }
                        }

                        if (calculatedRowSize(measuredCols) <= lineLength) {
                            break;
                        }
                    }

                    if (!didFlex) {
                        break;
                    }
                }
            }

            // concatenate final strings

            String finalString = "";

            for (String measured : measuredCols) {
                finalString += measured;
            }

            // crop and print out

            String msg = cropRightToFit((prefix_used ? empty_prefix : prefix + " ") + finalString, lineLength);

            if (color.length() > 0) {
                msg = color + msg;
            }

            if (msg.length() > 255) {
                for (String s : ChatPaginator.wordWrap(msg, lineLength)) {
                    player.sendMessage(s);
                }
            } else {
                player.sendMessage(msg);
            }

            prefix_used = true;
        }

        return rows.size() > 0;
    }

    private int calculatedRowSize(List<String> cols)
    {
        int out = 0;

        for (String col : cols) {
            out += msgLength(col);
        }

        return out;
    }

    /**
     *
     * @param col
     * @return
     */
    int getMaxWidth(int col)
    {
        double maxWidth = 0;

        for (String[] row : rows) {
            maxWidth = Math.max(maxWidth, msgLength(row[col]));
        }

        return (int) maxWidth;
    }

    /**
     *
     * @param msg
     * @return
     */
    public static String centerInLine(String msg)
    {
        return centerInLineOf(msg, lineLength);
    }

    /**
     *
     * @param msg
     * @param lineLength
     * @return
     */
    private static String centerInLineOf(String msg, double lineLength)
    {
        double length = msgLength(msg);
        double diff = lineLength - length;

        // if too big for line return it as is

        if (diff < 0) {
            return msg;
        }

        double sideSpace = diff / 2;

        // pad the left with space

        msg = paddLeftToFit(msg, lineLength - Math.floor(sideSpace));

        // padd the right with space

        msg = paddRightToFit(msg, lineLength);


        return msg;
    }

    /**
     *
     * @param str
     * @return
     */
    public static String makeEmpty(String str)
    {
        if (str == null) {
            return "";
        }

        return paddLeftToFit("", msgLength(str));
    }

    /**
     *
     * @param msg
     * @param length
     * @return
     */
    private static String cropRightToFit(String msg, double length)
    {
        if (msg == null || msg.length() == 0 || length == 0) {
            return "";
        }

        while (msgLength(msg) > length) {
            msg = msg.substring(0, msg.length() - 2);
        }

        return msg;
    }

    /**
     *
     * @param msg
     * @param length
     * @return
     */
    private static String cropLeftToFit(String msg, double length)
    {
        if (msg == null || msg.length() == 0 || length == 0) {
            return "";
        }

        while (msgLength(msg) >= length) {
            msg = msg.substring(1);
        }

        return msg;
    }

    /**
     * Padds left til the string is a certain size
     *
     * @param msg
     * @param length
     * @return
     */
    private static String paddLeftToFit(String msg, double length)
    {
        if (msgLength(msg) >= length) {
            return msg;
        }

        while (msgLength(msg) < length) {
            msg = " " + msg;
        }

        return msg;
    }

    /**
     * Padds right til the string is a certain size
     *
     * @param msg
     * @param length
     * @return
     */
    private static String paddRightToFit(String msg, double length)
    {
        if (msgLength(msg) >= length) {
            return msg;
        }

        while (msgLength(msg) < length) {
            msg += " ";
        }

        return msg;
    }

    /**
     * Finds the length on the screen of a string. Ignores colors.
     *
     * @param str
     * @return
     */
    private static double msgLength(String str)
    {
        double length = 0;
        str = cleanColors(str);

        // Loop through all the characters, skipping any color characters and their following color codes

        for (int x = 0; x < str.length(); x++) {
            int len = charLength(str.charAt(x));
            if (len > 0) {
                length += len;
            } else {
                x++;
            }
        }
        return length;
    }

    /**
     *
     * @param str
     * @return
     */
    private static String cleanColors(String str)
    {
        String patternStr = "ÃƒÂ¯Ã‚Â¿Ã‚Â½.";
        String replacementStr = "";

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);

        return matcher.replaceAll(replacementStr);
    }

    /**
     * Finds the visual length of the character on the screen.
     *
     * @param x
     * @return
     */
    private static int charLength(char x)
    {
        if ("i.:,;|!".indexOf(x) != -1) {
            return 2;
        } else if ("l'".indexOf(x) != -1) {
            return 3;
        } else if ("tI[]".indexOf(x) != -1) {
            return 4;
        } else if ("fk{}<>\"*()".indexOf(x) != -1) {
            return 5;
        } else if ("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^".indexOf(x) != -1) {
            return 6;
        } else if ("@~".indexOf(x) != -1) {
            return 7;
        } else if (x == ' ') {
            return 4;
        } else {
            return -1;
        }
    }

    /**
     * Cuts the message apart into whole words short enough to fit on one line
     *
     * @param msg
     * @return
     */
    private static String[] wordWrap(String msg)
    {
        // Split each word apart

        ArrayList<String> split = new ArrayList<String>();
        split.addAll(Arrays.asList(msg.split(" ")));

        // Create an array list for the output

        ArrayList<String> out = new ArrayList<String>();

        // While i is less than the length of the array of words

        while (!split.isEmpty()) {
            int len = 0;

            // Create an array list to hold individual words

            ArrayList<String> words = new ArrayList<String>();

            // Loop through the words finding their length and increasing
            // j, the end point for the sub string

            while (!split.isEmpty() && split.get(0) != null && len <= lineLength) {
                double wordLength = msgLength(split.get(0)) + 4;

                // If a word is too long for a line

                if (wordLength > lineLength) {
                    String[] tempArray = wordCut(len, split.remove(0));
                    words.add(tempArray[0]);
                    split.add(tempArray[1]);
                }

                // If the word is not too long to fit

                len += wordLength;

                if (len < lineLength) {
                    words.add(split.remove(0));
                }
            }
            // Merge them and add them to the output array.
            String merged = combineSplit(words.toArray(new String[words.size()])) + " ";
            out.add(merged.replaceAll("\\s+$", ""));
        }
        // Convert to an array and return

        return out.toArray(new String[out.size()]);
    }

    /**
     *
     *
     *
     * @param string
     * @return
     */
    private static String combineSplit(String[] string)
    {
        StringBuilder builder = new StringBuilder();
        for (String aString : string) {
            builder.append(aString);
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - " ".length());

        return builder.toString();
    }

    /**
     * Cuts apart a word that is too long to fit on one line
     *
     * @param lengthBefore
     * @param str
     * @return
     */
    private static String[] wordCut(int lengthBefore, String str)
    {
        int length = lengthBefore;

        // Loop through all the characters, skipping any color characters and their following color codes

        String[] output = new String[2];
        int x = 0;
        while (length < lineLength && x < str.length()) {
            int len = charLength(str.charAt(x));
            if (len > 0) {
                length += len;
            } else {
                x++;
            }
            x++;
        }
        if (x > str.length()) {
            x = str.length();
        }

        // Add the substring to the output after cutting it

        output[0] = str.substring(0, x);

        // Add the last of the string to the output.

        output[1] = str.substring(x);
        return output;
    }

    /**
     * Outputs a single line out, crops overflow
     *
     * @param receiver
     * @param msg
     */
    public static void saySingle(CommandSender receiver, String msg)
    {
        if (receiver == null) {
            return;
        }

        receiver.sendMessage(colorize(new String[]{cropRightToFit(msg, lineLength)})[0]);
    }

    /**
     * Outputs a message to a user
     *
     * @param receiver
     * @param msg
     */
    public static void sendMessage(CommandSender receiver, String msg)
    {
        if (receiver == null) {
            return;
        }

        String[] message = colorize(wordWrap(msg));

        for (String out : message) {
            receiver.sendMessage(out);
        }
    }

    /**
     * Send blank lie
     *
     * @param color
     */
    public void startColor(String color)
    {
        this.color = color;
    }

    /**
     * Send blank lie
     *
     * @param receiver
     */
    public static void sendBlank(CommandSender receiver)
    {
        if (receiver == null) {
            return;
        }

        receiver.sendMessage(" ");
    }

    /**
     * Colors each line
     *
     * @param message
     * @return
     */
    public static String[] say(String message)
    {
        return colorize(wordWrap(message));
    }

    /**
     *
     * @param message
     * @return
     */
    private static String[] colorize(String[] message)
    {
        return colorizeBase(message);
    }

    /**
     *
     * @param message
     * @return
     */
    public static String colorize(String message)
    {
        return colorizeBase((new String[]{
                    message
                }))[0];
    }

    /**
     *
     *
     * @param message
     * @return
     */
    private static String[] colorizeBase(String[] message)
    {
        if (message != null && message[0] != null && !message[0].isEmpty()) {
            // Go through each line

            String prevColor = "";
            String lastColor = "";

            int counter = 0;
            for (String msg : message) {
                // Loop through looking for a color code
                for (int x = 0; x < msg.length(); x++) {
                    // If the char is color code
                    if (msg.codePointAt(x) == 167) {
                        // advance x to the next character
                        x += 1;

                        try {
                            lastColor = ChatColor.getByChar(msg.charAt(x)) + "";
                        } catch (Exception ignored) {
                        }
                    }
                }
                // Replace the message with the colorful message

                message[counter] = prevColor + msg;
                prevColor = lastColor;
                counter++;
            }
        }

        return message;
    }
}
