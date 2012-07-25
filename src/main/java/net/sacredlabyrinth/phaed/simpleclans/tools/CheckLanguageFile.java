/*
 * Copyright (C) 2012 p000ison
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 * 
 */
package net.sacredlabyrinth.phaed.simpleclans.tools;

import java.io.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple tool to check language file and update it (code is ugly but does its job :P)
 * 
 * @author Max
 */
public class CheckLanguageFile
{

    public static void main(String[] args)
    {
        BufferedReader reader = null;
        BufferedReader readerown = null;
        BufferedWriter writer = null;
        try {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter the path to the orginal language file: ");
            String orginalPath = null;
            try {
                orginalPath = console.readLine();
            } catch (IOException e) {
            }
            
            System.out.print("Enter the path to your language file: ");
            String user = null;
            try {
                user = console.readLine();
            } catch (IOException e) {
            }

            File orginalLangFile = new File(orginalPath);
            File userLangFile = new File(user);
            File toLangFile = new File("missing.properties");

            if (!toLangFile.exists()) {
                toLangFile.createNewFile();
            }

            String line;

            reader = new BufferedReader(new FileReader(orginalLangFile));

            LinkedList<String> list = new LinkedList<String>();

            while ((line = reader.readLine()) != null) {
                list.add(line);
            }

            String lineown;

            readerown = new BufferedReader(new FileReader(userLangFile));

            LinkedList<String> listown = new LinkedList<String>();

            while ((lineown = readerown.readLine()) != null) {
                listown.add(lineown.split(" |=", 2)[0]);
            }

            writer = new BufferedWriter(new FileWriter(toLangFile));

            for (String orginal : list) {
                if (!listown.contains(orginal.split(" |=", 2)[0])) {
                    writer.write(orginal);
                    writer.newLine();
                }
            }
            
            System.out.println("missing.properties generated!");

        } catch (Exception ex) {
            Logger.getLogger(CheckLanguageFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                readerown.close();
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(CheckLanguageFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
