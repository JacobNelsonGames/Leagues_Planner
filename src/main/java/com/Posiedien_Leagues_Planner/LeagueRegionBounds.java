package com.Posiedien_Leagues_Planner;

import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import java.util.*;

public class LeagueRegionBounds
{
    public String RegionName;

    // Basically a linked list of points
    public List<LeagueRegionPoint> RegionPoints;

    public LeagueRegionPoint CurrentFocusedPoint;

}
