package com.Posiedien_Leagues_Planner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class FullRegionData
{
    public ArrayList<LeagueRegionBounds> RegionData = new ArrayList<>();

    public void exportTo(File file) throws IOException
    {
        try (FileWriter fw = new FileWriter(file))
        {
            fw.write(ExportData());
        }
    }

    // Fixup all of our linkage
    private void FixupAfterImport()
    {
        for (LeagueRegionBounds Region : RegionData)
        {
            for (HashMap.Entry<UUID, LeagueRegionPoint> entry : Region.RegionPoints.entrySet())
            {
                entry.getValue().ConnectedPoints.clear();
                for (UUID ConnectedGUID : entry.getValue().ConnectedPointGUIDs)
                {
                    entry.getValue().ConnectedPoints.add(Region.RegionPoints.get(ConnectedGUID));
                }
            }
        }
    }

    public void importFrom(File file)
    {
        try (Scanner sc = new Scanner(file))
        {
            sc.useDelimiter(",");
            ImportData(sc);
        } catch (IOException ignored)
        {
        }

        FixupAfterImport();
    }
    public String ExportData()
    {
        StringBuilder Converted = new StringBuilder();

        Converted.append(RegionData.size());
        Converted.append(",");

        for (LeagueRegionBounds Region : RegionData)
        {
            Converted.append(Region.ExportData());
        }
        
        return Converted.toString();
    }

    public void ImportData(Scanner sc)
    {
        RegionData.clear();
        int RegionSize = Integer.parseInt(sc.next());

        for (int i = 0; i < RegionSize; ++i)
        {
            LeagueRegionBounds NewRegionBounds = new LeagueRegionBounds();
            NewRegionBounds.ImportData(sc);
            RegionData.add(NewRegionBounds);
        }
    }
}
