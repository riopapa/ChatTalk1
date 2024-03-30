package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.tableFolder;

import com.urrecliner.chattalk.Sub.SnackBar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TableListFile {


    public String[] read(String filename) {

        // read all lines, remove ; comments, delete if length < 2
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(
                    String.valueOf(new File(tableFolder, filename + ".txt"))));
        } catch (IOException e) {
            new SnackBar().show("ArrayTable.read", "File " + filename+ " Not Found\n"+e);
        }

        for (int lnNbr = 0; lnNbr < lines.size(); ) {   // lines.size() is varying
            String lnStr = lines.get(lnNbr).trim();
            if (lnStr.length() < 2)
                lines.remove(lnNbr);
            else {
                int pos = lnStr.lastIndexOf(";");
                if (pos > 0)
                    lines.set(lnNbr, lnStr.substring(0, pos).trim());
                lnNbr++;
            }
        }
        return lines.toArray(new String[0]);
    }

    String[] readRaw(File fullName) {

        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(
                    String.valueOf(fullName)));
        } catch (IOException e) {
            new SnackBar().show("readRawLines", "File " + fullName+ " Not Found"+e);
        }
        return lines.toArray(new String[0]);
    }
}