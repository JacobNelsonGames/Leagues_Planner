package com.Posiedien_Leagues_Planner;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;

import net.runelite.api.*;
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

	public LeagueRegionBounds CurrentRegion = null;
	public LeagueRegionPoint LastClickedRegionPoint = null;


	public void InitializeRegionData()
	{
		config.RegionData.clear();
		for (RegionType CurrentRegion : RegionType.values())
		{
			if (CurrentRegion == RegionType.NONE)
			{
				continue;
			}

			config.RegionData.add(new LeagueRegionBounds(CurrentRegion));
		}
	}

	public LeagueRegionBounds GetRegionBounds(RegionType Type)
	{
		for (LeagueRegionBounds CurrentBounds : config.RegionData)
		{
			if (CurrentBounds.Type == Type)
			{
				return CurrentBounds;
			}
		}

		return null;
	}


	public WorldPoint LastDisplayedWorldPoint;

	private static final BufferedImage MARKER_IMAGE = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/marker.png");
	private static final BufferedImage ACTIVE_MARKER_IMAGE = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/activemarker.png");

	@Override
	protected void startUp() throws Exception
	{
		InitializeRegionData();
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
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Posiedien Leagues Planner says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
		if (map == null)
		{
			return;
		}

		if (!map.getBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			return;
		}

		LeagueRegionPoint ClickedPoint = GetClickedRegionPoint(Collections.singletonList(event.getMenuEntry()));
		if (ClickedPoint != null && event.getMenuOption().contains("Focus on"))
		{
			// Toggle activation
			SetMarkerActivation(ClickedPoint, CurrentRegion.CurrentFocusedPoint != ClickedPoint);
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
		final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
		if (map == null)
		{
			return;
		}

		if (!map.getBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			return;
		}

		LastMenuOpenedPoint = client.getMouseCanvasPosition();
		CurrentRegion = GetRegionBounds(config.GetEditRegion());
		AddRightClickMenuEntries(event);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		//log.info("TEST 2!");
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
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

	private final void SetMarkerActivation(LeagueRegionPoint RegionPoint, boolean shouldActivate)
	{
		if (shouldActivate)
		{
			if (CurrentRegion.CurrentFocusedPoint != null)
			{
				SetMarkerActivation(CurrentRegion.CurrentFocusedPoint, false);
			}

			CurrentRegion.CurrentFocusedPoint = RegionPoint;

			WorldMapPoint OldMarker = RegionPoint.OurPoint;
			worldMapPointManager.removeIf(x -> x == OldMarker);

			WorldPoint WorldPointLocation;
			if (OldMarker == null)
			{
				WorldPointLocation = LastDisplayedWorldPoint;
			}
			else
			{
				WorldPointLocation = RegionPoint.OurPoint.getWorldPoint();
			}

			RegionPoint.OurPoint = new WorldMapPoint(WorldPointLocation, ACTIVE_MARKER_IMAGE);
			WorldMapPoint NewMarker = RegionPoint.OurPoint;
			NewMarker.setTarget(WorldPointLocation);
			NewMarker.setJumpOnClick(true);
			NewMarker.setName("Region Bounds: " + CurrentRegion.Type + " " + WorldPointLocation + " guid:"+ RegionPoint.GUID);
			worldMapPointManager.add(NewMarker);

		}
		else
		{
			WorldMapPoint OldMarker = RegionPoint.OurPoint;
			worldMapPointManager.removeIf(x -> x == OldMarker);

			WorldPoint WorldPointLocation;
			if (OldMarker == null)
			{
				WorldPointLocation = LastDisplayedWorldPoint;
			}
			else
			{
				WorldPointLocation = RegionPoint.OurPoint.getWorldPoint();
			}

			RegionPoint.OurPoint = new WorldMapPoint(WorldPointLocation, MARKER_IMAGE);
			WorldMapPoint NewMarker = RegionPoint.OurPoint;
			NewMarker.setTarget(WorldPointLocation);
			NewMarker.setJumpOnClick(true);
			NewMarker.setName("Region Bounds: " + CurrentRegion.Type + " " + WorldPointLocation + " guid:"+ RegionPoint.GUID);
			worldMapPointManager.add(NewMarker);
		}
	}

	private final Consumer<MenuEntry> SetNextRegionPointEntryCallback = n ->
	{
		LeagueRegionPoint LastRegionPoint = CurrentRegion.CurrentFocusedPoint;
		LeagueRegionPoint NewRegionPoint = new LeagueRegionPoint();

		UUID uuid = UUID.randomUUID();
		NewRegionPoint.GUID = uuid;
		CurrentRegion.RegionPoints.put(NewRegionPoint.GUID, NewRegionPoint);

		SetMarkerActivation(NewRegionPoint, true);

		if (LastRegionPoint != null)
		{
			// At capacity, remove last added
			if (LastRegionPoint.ConnectedPoints.size() == 2)
			{
				LastRegionPoint.ConnectedPoints.get(1).ConnectedPoints.remove(LastRegionPoint);
				LastRegionPoint.ConnectedPoints.remove(1);
			}

			LastRegionPoint.ConnectedPoints.add(NewRegionPoint);
			NewRegionPoint.ConnectedPoints.add(LastRegionPoint);
		}

	};

	private final Consumer<MenuEntry> SetActiveRegionPointEntryCallback = n ->
	{
		SetMarkerActivation(LastClickedRegionPoint, true);
	};

	private final Consumer<MenuEntry> DeleteRegionPointEntryCallback = n ->
	{
		// Remove connections
		for (LeagueRegionPoint ConnectedPoint : LastClickedRegionPoint.ConnectedPoints)
		{
			ConnectedPoint.ConnectedPoints.remove(LastClickedRegionPoint);
		}
		LastClickedRegionPoint.ConnectedPoints.clear();

		worldMapPointManager.removeIf(x -> x == LastClickedRegionPoint.OurPoint);

		CurrentRegion.RegionPoints.remove(LastClickedRegionPoint.GUID);
		if (LastClickedRegionPoint == CurrentRegion.CurrentFocusedPoint)
		{
			CurrentRegion.CurrentFocusedPoint = null;
		}
	};

	private final Consumer<MenuEntry> ConnectRegionPointEntryCallback = n ->
	{
		// If we are already connected, disconnect instead
		for (LeagueRegionPoint ConnectedPoint : CurrentRegion.CurrentFocusedPoint.ConnectedPoints)
		{
			if (ConnectedPoint == LastClickedRegionPoint)
			{
				ConnectedPoint.ConnectedPoints.remove(CurrentRegion.CurrentFocusedPoint);
				CurrentRegion.CurrentFocusedPoint.ConnectedPoints.remove(ConnectedPoint);
				return;
			}
		}

		// At capacity, remove last added
		if (CurrentRegion.CurrentFocusedPoint.ConnectedPoints.size() == 2)
		{
			CurrentRegion.CurrentFocusedPoint.ConnectedPoints.get(1).ConnectedPoints.remove(CurrentRegion.CurrentFocusedPoint);
			CurrentRegion.CurrentFocusedPoint.ConnectedPoints.remove(1);
		}

		CurrentRegion.CurrentFocusedPoint.ConnectedPoints.add(LastClickedRegionPoint);

		// At capacity, remove last added
		if (LastClickedRegionPoint.ConnectedPoints.size() == 2)
		{
			LastClickedRegionPoint.ConnectedPoints.get(1).ConnectedPoints.remove(LastClickedRegionPoint);
			LastClickedRegionPoint.ConnectedPoints.remove(1);
		}

		LastClickedRegionPoint.ConnectedPoints.add(CurrentRegion.CurrentFocusedPoint);
	};

	private final LeagueRegionPoint GetClickedRegionPoint(List<MenuEntry> entries)
	{
		for (MenuEntry CurrentEntry : entries)
		{
			if (!CurrentEntry.getOption().contains("Focus"))
			{
				continue;
			}

			String TargetString = CurrentEntry.getTarget();
			if (TargetString.contains("Region Bounds: "))
			{
				int index = TargetString.indexOf("guid:");

				// Guids are 36 indices long
				String SubString = TargetString.substring(index + 5, index + 5 + 36);

				UUID FoundGUID = UUID.fromString(SubString);
				LeagueRegionPoint FoundPoint = CurrentRegion.RegionPoints.get(FoundGUID);
				if (FoundPoint != null)
				{
					return FoundPoint;
				}
			}
		}

		return null;
	}

	private void AddRightClickMenuEntries(MenuOpened event)
	{
		List<MenuEntry> entries = new LinkedList<>(Arrays.asList(event.getMenuEntries()));

		LastDisplayedWorldPoint = CalculateMapPoint(client.isMenuOpen() ? LastMenuOpenedPoint : client.getMouseCanvasPosition());
		LastClickedRegionPoint = GetClickedRegionPoint(entries);

		String nextOption = null;
		if (config.GetEditRegion() != RegionType.NONE)
		{
			if (LastClickedRegionPoint == null)
			{
				nextOption = "Set Next Region Point";
				String finalNextOption1 = nextOption;
				if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption1)))
				{
					MenuEntry SetNextRegionPointEntry = client.createMenuEntry(-1);
					SetNextRegionPointEntry.setOption(nextOption);
					SetNextRegionPointEntry.setType(MenuAction.RUNELITE);
					SetNextRegionPointEntry.onClick(this.SetNextRegionPointEntryCallback);
					entries.add(0, SetNextRegionPointEntry);
				}
			}
			else
			{
				if (CurrentRegion.CurrentFocusedPoint != LastClickedRegionPoint)
				{
					nextOption = "Set Active Region Point";
					String finalNextOption2 = nextOption;
					if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption2)))
					{
						MenuEntry SetActiveRegionPointEntry = client.createMenuEntry(-1);
						SetActiveRegionPointEntry.setOption(nextOption);
						SetActiveRegionPointEntry.setType(MenuAction.RUNELITE);
						SetActiveRegionPointEntry.onClick(this.SetActiveRegionPointEntryCallback);
						entries.add(0, SetActiveRegionPointEntry);
					}

					if (CurrentRegion.CurrentFocusedPoint != null)
					{
						nextOption = "Connect Region Point";
						String finalNextOption4 = nextOption;
						if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption4)))
						{
							MenuEntry ConnectRegionPointEntry = client.createMenuEntry(-1);
							ConnectRegionPointEntry.setOption(nextOption);
							ConnectRegionPointEntry.setType(MenuAction.RUNELITE);
							ConnectRegionPointEntry.onClick(this.ConnectRegionPointEntryCallback);
							entries.add(0, ConnectRegionPointEntry);
						}
					}
				}

				nextOption = "Delete Region Point";
				String finalNextOption3 = nextOption;
				if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption3)))
				{
					MenuEntry DeleteRegionPointEntry = client.createMenuEntry(-1);
					DeleteRegionPointEntry.setOption(nextOption);
					DeleteRegionPointEntry.setType(MenuAction.RUNELITE);
					DeleteRegionPointEntry.onClick(this.DeleteRegionPointEntryCallback);
					entries.add(0, DeleteRegionPointEntry);
				}

			}
		}

		if (config.GetEditRegion() == RegionType.NONE)
		{
			nextOption = "Map Coordinate: " + LastDisplayedWorldPoint;
			String finalNextOption = nextOption;
			if (entries.stream().noneMatch(e -> e.getOption().equals(finalNextOption)))
			{
				MenuEntry MapCoordEntry = client.createMenuEntry(-1);
				MapCoordEntry.setOption(nextOption);
				MapCoordEntry.setType(MenuAction.RUNELITE);
				entries.add(0, MapCoordEntry);
			}
		}

		client.setMenuEntries(entries.toArray(new MenuEntry[0]));
	}
}
