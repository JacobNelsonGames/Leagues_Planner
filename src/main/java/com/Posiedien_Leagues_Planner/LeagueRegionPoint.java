package com.Posiedien_Leagues_Planner;

import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeagueRegionPoint
{

    public UUID GUID;
    public ArrayList<LeagueRegionPoint> ConnectedPoints = new ArrayList<>();

    public WorldMapPoint OurPoint;

}
