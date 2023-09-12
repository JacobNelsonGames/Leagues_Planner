package com.Posiedien_Leagues_Planner;

import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import java.util.*;

public class LeagueRegionBounds
{
    public RegionType Type;

    // Basically a linked list of points
    public HashMap<UUID, LeagueRegionPoint> RegionPoints = new HashMap<UUID, LeagueRegionPoint>();

    public LeagueRegionPoint CurrentFocusedPoint;

    public LeagueRegionBounds(RegionType currentRegion)
    {
        Type = currentRegion;
    }
}
