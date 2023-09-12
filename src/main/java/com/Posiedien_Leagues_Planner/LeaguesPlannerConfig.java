package com.Posiedien_Leagues_Planner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;


import java.util.ArrayList;
import java.util.List;

@ConfigGroup("LeaguePlanner")
public interface LeaguesPlannerConfig extends Config
{
	public ArrayList<LeagueRegionBounds> RegionData = new ArrayList<>();

	@ConfigItem(
		keyName = "Blank",
		name = "Posiedien's Leagues Planner",
		description = "This plugin is for setting up your perfect league's plan"
	)
	default String Blank()
	{
		return "Blank Test";
	}

	@ConfigItem(
			keyName = "GetEditRegion",
			name = "Edit Region Bounds",
			description = "Current region bounds we want to edit"
	)
	default RegionType GetEditRegion()
	{
		return RegionType.NONE;
	}
}
