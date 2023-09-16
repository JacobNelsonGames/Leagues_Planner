package com.Posiedien_Leagues_Planner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ConfigGroup("LeaguePlanner")
public interface LeaguesPlannerConfig extends Config
{
	public FullRegionData RegionData = new FullRegionData();

	@ConfigSection(
			position = 1,
			name = "Editor",
			description = "Editor functions"
	)
	String editorSection = "editorSection";

	@ConfigItem(
			keyName = "GetEditRegion",
			name = "Edit Region Bounds",
			description = "Current region bounds we want to edit",
			section = editorSection
	)
	default RegionType GetEditRegion()
	{
		return RegionType.NONE;
	}

	@ConfigItem(
			keyName = "DebugColorAlpha",
			name = "Debug Color Alpha (0-255)",
			description = "Alpha value for any debug color (value 0-255)",
			section = editorSection
	)
	default int DebugColorAlpha()
	{
		return 40;
	}

	@ConfigItem(
			keyName = "DebugColorDisabledAlpha",
			name = "Debug Color Disabled Alpha (0-255)",
			description = "Alpha value for disabled debug color (value 0-255)",
			section = editorSection
	)
	default int DebugColorDisabledAlpha()
	{
		return 255;
	}

	@ConfigSection(
			position = 2,
			name = "Region Plugin Colors",
			description = "The colors to use for our regions"
	)
	String regionColors = "regionColors";

	@ConfigItem(
			keyName = "MisthalinColor",
			name = "Misthalin Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color MisthalinColor()
	{
		return new Color(255, 2, 2, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "KaramjaColor",
			name = "Karamja Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color KaramjaColor()
	{
		return new Color(46, 108, 23, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "KandarinColor",
			name = "Kandarin Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color KandarinColor()
	{
		return new Color(231, 143, 10, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "AsgarniaColor",
			name = "Asgarnia Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color AsgarniaColor()
	{
		return new Color(46, 59, 234, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "FremennikColor",
			name = "Fremennik Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color FremennikColor()
	{
		return new Color(121, 67, 3, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "KourendColor",
			name = "Kourend Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color KourendColor()
	{
		return new Color(31, 224, 179, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "WildernessColor",
			name = "Wilderness Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color WildernessColor()
	{
		return new Color(94, 14, 14, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "MorytaniaColor",
			name = "Morytania Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color MorytaniaColor()
	{
		return new Color(102, 3, 114, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "TirannwnColor",
			name = "Tirannwn Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color TirannwnColor()
	{
		return new Color(130, 255, 105, DebugColorAlpha());
	}

	@ConfigItem(
			keyName = "DesertColor",
			name = "Desert Color",
			description = "The color for the region",
			section = regionColors
	)
	default Color DesertColor()
	{
		return new Color(255, 226, 1, DebugColorAlpha());
	}


	@ConfigSection(
			position = 3,
			name = "Regions Unlocked",
			description = "The regions we currently have unlocked"
	)
	String regionUnlock = "regionUnlock";

	@ConfigItem(
			keyName = "MisthalinUnlocked",
			name = "Misthalin Unlocked",
			description = "Whether or not Misthalin is unlocked",
			section = regionUnlock
	)
	default boolean MisthalinUnlocked()
	{
		return true;
	}

	@ConfigItem(
			keyName = "KaramjaUnlocked",
			name = "Karamja Unlocked",
			description = "Whether or not Karamja is unlocked",
			section = regionUnlock
	)
	default boolean KaramjaUnlocked()
	{
		return true;
	}

	@ConfigItem(
			keyName = "KandarinUnlocked",
			name = "Kandarin Unlocked",
			description = "Whether or not Kandarin is unlocked",
			section = regionUnlock
	)
	default boolean KandarinUnlocked()
	{
		return false;
	}

	@ConfigItem(
			keyName = "AsgarniaUnlocked",
			name = "Asgarnia Unlocked",
			description = "Whether or not Asgarnia is unlocked",
			section = regionUnlock
	)
	default boolean AsgarniaUnlocked()
	{
		return false;
	}

	@ConfigItem(
			keyName = "FremennikUnlocked",
			name = "Fremennik Unlocked",
			description = "Whether or not Fremennik is unlocked",
			section = regionUnlock
	)
	default boolean FremennikUnlocked()
	{
		return false;
	}

	@ConfigItem(
			keyName = "KourendUnlocked",
			name = "Kourend Unlocked",
			description = "Whether or not Kourend is unlocked",
			section = regionUnlock
	)
	default boolean KourendUnlocked()
	{
		return false;
	}

	@ConfigItem(
			keyName = "WildernessUnlocked",
			name = "Wilderness Unlocked",
			description = "Whether or not Wilderness is unlocked",
			section = regionUnlock
	)
	default boolean WildernessUnlocked()
	{
		return false;
	}

	@ConfigItem(
			keyName = "MorytaniaUnlocked",
			name = "Morytania Unlocked",
			description = "Whether or not Morytania is unlocked",
			section = regionUnlock
	)
	default boolean MorytaniaUnlocked()
	{
		return false;
	}

	@ConfigItem(
			keyName = "TirannwnUnlocked",
			name = "Tirannwn Unlocked",
			description = "Whether or not Tirannwn is unlocked",
			section = regionUnlock
	)
	default boolean TirannwnUnlocked()
	{
		return false;
	}

	@ConfigItem(
			keyName = "DesertUnlocked",
			name = "Desert Unlocked",
			description = "Whether or not Desert is unlocked",
			section = regionUnlock
	)
	default boolean DesertUnlocked()
	{
		return false;
	}
}
