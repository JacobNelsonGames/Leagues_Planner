package com.Posiedien_Leagues_Planner;

import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import javax.sound.sampled.Line;
import java.awt.*;
import java.util.*;

public class LeagueRegionBounds
{
    public RegionType Type;

    // Basically a linked list of points
    public HashMap<UUID, LeagueRegionPoint> RegionPoints = new HashMap<UUID, LeagueRegionPoint>();

    public ArrayList<WorldPointPolygon> RegionPolygons = new ArrayList<>();
    public ArrayList<RegionLine> RegionLines = new ArrayList<>();

    public LeagueRegionBounds(RegionType currentRegion)
    {
        Type = currentRegion;
    }
}
