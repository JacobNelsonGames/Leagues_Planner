package com.Posiedien_Leagues_Planner;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.GameTick;

import net.runelite.api.*;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.List;
import java.util.*;

@Slf4j
@PluginDescriptor(
		name = "Posiedien Leagues Planner"
)


public class PosiedienLeaguesPlannerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private LeaguesPlannerConfig config;

	@Override
	protected void startUp() throws Exception
	{
		//log.info("Posiedien's Leagues Planner started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		//log.info("Posiedien Leagues Planner stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Posiedien Leagues Planner says " + config.greeting(), null);
		}
	}

	@Provides
	LeaguesPlannerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LeaguesPlannerConfig.class);
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		//log.info("TEST!");
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		//log.info("TEST 2!");
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
		if (map == null)
		{
			return;
		}

		//log.info("TEST! 3");
		addMenuEntry(event, "Show Coordinate");
	}

	private void addMenuEntry(MenuEntryAdded event, String option)
	{
		List<MenuEntry> entries = new LinkedList<>(Arrays.asList(client.getMenuEntries()));

		if (entries.stream().anyMatch(e -> e.getOption().equals(option))) {
			return;
		}



		MenuEntry entry = new CustomMenuEntry();
		entry.setOption(option);
		entry.setTarget(event.getTarget());
		entry.setType(MenuAction.of(MenuAction.RUNELITE.getId()));
		entries.add(0, entry);

		client.setMenuEntries(entries.toArray(new MenuEntry[0]));
	}
}
