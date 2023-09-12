package com.Posiedien_Leagues_Planner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("LeaguePlanner")
public interface LeaguesPlannerConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Posiedien's Leagues Planner",
		description = "This plugin is for setting up your perfect league's plan"
	)
	default String greeting()
	{
		return "";
	}
}
