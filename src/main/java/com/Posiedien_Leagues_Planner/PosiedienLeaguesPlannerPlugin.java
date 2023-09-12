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
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import java.awt.image.BufferedImage;
import net.runelite.client.util.ImageUtil;

import java.util.List;
import java.util.*;
import java.util.function.Consumer;


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

	@Inject
	private WorldMapPointManager worldMapPointManager;

	@Inject
	private WorldMapOverlay worldMapOverlay;


	private Point LastMenuOpenedPoint;

	public List<LeagueRegionBounds> RegionBounds;

	public LeagueRegionBounds CurrentRegion;



	public WorldPoint LastDisplayedWorldPoint;

	private static final BufferedImage MARKER_IMAGE = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/marker.png");

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
		//log.info("TEST! 3");
		LastMenuOpenedPoint = client.getMouseCanvasPosition();
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

		if (map.getBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			addMenuEntries(event);
		}
	}

	private WorldPoint CalculateMapPoint(Point point)
	{
		float zoom = client.getRenderOverview().getWorldMapZoom();
		RenderOverview renderOverview = client.getRenderOverview();
		final WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(), renderOverview.getWorldMapPosition().getY(), 0);
		final Point middle = worldMapOverlay.mapWorldPointToGraphicsPoint(mapPoint);

		final int dx = (int) ((point.getX() - middle.getX()) / zoom);
		final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

		return mapPoint.dx(dx).dy(dy);
	}

	private final Consumer<MenuEntry> SetNextRegionPointEntryCallback = n ->
	{
		//worldMapPointManager.removeIf(x -> x == CurrentMarker);

		//CurrentMarker = new WorldMapPoint(LastDisplayedWorldPoint, MARKER_IMAGE);
		//CurrentMarker.setTarget(CurrentMarker.getWorldPoint());
		//CurrentMarker.setJumpOnClick(true);
		//CurrentMarker.setName("Custom Coordinate: " + LastDisplayedWorldPoint);
		//worldMapPointManager.add(CurrentMarker);
	};

	private final Consumer<MenuEntry> SetActiveRegionPointEntryCallback = n ->
	{
	};

	private final Consumer<MenuEntry> DeleteRegionPointEntryCallback = n ->
	{
	};

	private void addMenuEntries(MenuEntryAdded event)
	{
		List<MenuEntry> entries = new LinkedList<>(Arrays.asList(client.getMenuEntries()));

		LastDisplayedWorldPoint = CalculateMapPoint(client.isMenuOpen() ? LastMenuOpenedPoint : client.getMouseCanvasPosition());

		String nextOption = "Set Next Region Point";
		String finalNextOption1 = nextOption;
		if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption1)))
		{
			MenuEntry SetNextRegionPointEntry = client.createMenuEntry(-1);
			SetNextRegionPointEntry.setOption(nextOption);
			SetNextRegionPointEntry.setTarget(event.getTarget());
			SetNextRegionPointEntry.setType(MenuAction.RUNELITE);
			SetNextRegionPointEntry.onClick(this.SetNextRegionPointEntryCallback);
			entries.add(0, SetNextRegionPointEntry);
		}

		nextOption = "Set Active Region Point";
		String finalNextOption2 = nextOption;
		if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption2)))
		{
			MenuEntry SetActiveRegionPointEntry = client.createMenuEntry(-1);
			SetActiveRegionPointEntry.setOption(nextOption);
			SetActiveRegionPointEntry.setTarget(event.getTarget());
			SetActiveRegionPointEntry.setType(MenuAction.RUNELITE);
			SetActiveRegionPointEntry.onClick(this.SetActiveRegionPointEntryCallback);
			entries.add(0, SetActiveRegionPointEntry);
		}

		nextOption = "Delete Region Point";
		String finalNextOption3 = nextOption;
		if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption3)))
		{
			MenuEntry DeleteRegionPointEntry = client.createMenuEntry(-1);
			DeleteRegionPointEntry.setOption(nextOption);
			DeleteRegionPointEntry.setTarget(event.getTarget());
			DeleteRegionPointEntry.setType(MenuAction.RUNELITE);
			DeleteRegionPointEntry.onClick(this.DeleteRegionPointEntryCallback);
			entries.add(0, DeleteRegionPointEntry);
		}


		nextOption = "Map Coordinate: " + LastDisplayedWorldPoint;
		String finalNextOption = nextOption;
		if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption)))
		{
			MenuEntry MapCoordEntry = client.createMenuEntry(-1);
			MapCoordEntry.setOption(nextOption);
			MapCoordEntry.setTarget(event.getTarget());
			MapCoordEntry.setType(MenuAction.RUNELITE);
			entries.add(0, MapCoordEntry);
		}


		client.setMenuEntries(entries.toArray(new MenuEntry[0]));
	}
}
