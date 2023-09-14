package com.Posiedien_Leagues_Planner;

import com.google.inject.Provides;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.plaf.synth.Region;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import net.runelite.client.util.ImageUtil;

import java.io.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.Scanner;

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
	public OverlayManager overlayManager;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	@Inject
	private WorldMapOverlay worldMapOverlay;


	private Point LastMenuOpenedPoint;

	public LeagueRegionBounds CurrentRegion = null;
	public LeagueRegionPoint LastClickedRegionPoint = null;
	@Inject
	public RegionBoundOverlay regionBoundOverlay = new RegionBoundOverlay(client, this, config);

	public LeagueRegionPoint CurrentFocusedPoint;

	void SaveRegionBounds() throws IOException
	{
		File targ = new File("RegionBoundData.csv");
		config.RegionData.exportTo(targ);
	}

	void LoadRegionBounds() throws IOException
	{
		File targ = new File("RegionBoundData.csv");
		config.RegionData.importFrom(targ);
	}

	private boolean GatherRegionBounds(WorldPointPolygon poly, ArrayList<RegionLine> regionLines, Set<UUID> VisitedPoints, LeagueRegionPoint nextPoint, LeagueRegionPoint parentPoint)
	{
		if (VisitedPoints.contains(nextPoint.GUID))
		{
			// Connected back!
			return true;
		}
		VisitedPoints.add(nextPoint.GUID);
		WorldPoint end = nextPoint.OurWorldPoint;
		poly.AddWorldPoint(end);

		if (parentPoint != null)
		{
			WorldPoint start = parentPoint.OurWorldPoint;
			regionLines.add(new RegionLine(start, end));
		}

		// Recursively visit all the points and draw a polygon if one exists
		for (LeagueRegionPoint connectedPoint : nextPoint.ConnectedPoints)
		{
			// don't go backwards
			if (parentPoint == connectedPoint)
			{
				continue;
			}

			if (GatherRegionBounds(poly, regionLines, VisitedPoints, connectedPoint, nextPoint))
			{
				// Connected back!
				return true;
			}
		}

		return false;
	}

	private void RefreshRegionBounds()
	{
		Set<UUID> VisitedPoints = new HashSet<>();
		for (LeagueRegionBounds regionDatum : config.RegionData.RegionData)
		{
			Color DrawColor = regionDatum.Type.GetRegionColor(regionDatum.Type);
			regionDatum.RegionPolygons.clear();
			regionDatum.RegionLines.clear();

			regionDatum.RegionPoints.forEach((key, value) ->
			{
				// Early out
				if (VisitedPoints.contains(value.GUID))
				{
					return;
				}

				WorldPointPolygon newPolygon = new WorldPointPolygon();

				if (GatherRegionBounds(newPolygon, regionDatum.RegionLines, VisitedPoints, value, null))
				{
					regionDatum.RegionPolygons.add(newPolygon);
				}
			});
		}
	}

	public void InitializeRegionData() throws Exception
	{
		config.RegionData.RegionData.clear();
		for (RegionType CurrentRegion : RegionType.values())
		{
			if (CurrentRegion == RegionType.NONE)
			{
				continue;
			}

			config.RegionData.RegionData.add(new LeagueRegionBounds(CurrentRegion));
		}

		LoadRegionBounds();
		// Add all the serialized markers
		for (LeagueRegionBounds LocalCurrentRegion : config.RegionData.RegionData)
		{
			LocalCurrentRegion.RegionPoints.forEach((key, value) ->
			{
				SetMarkerActivation(value, false);
			});
		}

		RefreshRegionBounds();
	}

	public LeagueRegionBounds GetRegionBounds(RegionType Type)
	{
		for (LeagueRegionBounds CurrentBounds : config.RegionData.RegionData)
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

	private static final BufferedImage BOUNDS_SELECTED = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Selected.png");
	private static final BufferedImage BOUNDS_MISTHALIN = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Misthalin.png");
	private static final BufferedImage BOUNDS_KARAMJA = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Karamja.png");
	private static final BufferedImage BOUNDS_KANDARIN = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Kandarin.png");
	private static final BufferedImage BOUNDS_ASGARNIA = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Asgarnia.png");
	private static final BufferedImage BOUNDS_FREMENNIK = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Fremennik.png");
	private static final BufferedImage BOUNDS_KOUREND = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Kourend.png");
	private static final BufferedImage BOUNDS_WILDERNESS = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Wilderness.png");
	private static final BufferedImage BOUNDS_MORYTANIA = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Morytania.png");
	private static final BufferedImage BOUNDS_TIRANNWN = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Tirannwn.png");
	private static final BufferedImage BOUNDS_DESERT = ImageUtil.getResourceStreamFromClass(PosiedienLeaguesPlannerPlugin.class, "/BoundPoint_Desert.png");

	public BufferedImage GetRegionImage(RegionType Type)
	{
		switch(Type)
		{
			case MISTHALIN:
				return BOUNDS_MISTHALIN;
			case KARAMJA:
				return BOUNDS_KARAMJA;
			case KANDARIN:
				return BOUNDS_KANDARIN;
			case ASGARNIA:
				return BOUNDS_ASGARNIA;
			case FREMENNIK:
				return BOUNDS_FREMENNIK;
			case KOUREND:
				return BOUNDS_KOUREND;
			case WILDERNESS:
				return BOUNDS_WILDERNESS;
			case MORYTANIA:
				return BOUNDS_MORYTANIA;
			case TIRANNWN:
				return BOUNDS_TIRANNWN;
			case DESERT:
				return BOUNDS_DESERT;
		}
		return MARKER_IMAGE;
	}

	@Override
	protected void startUp() throws Exception
	{
		InitializeRegionData();
		overlayManager.add(regionBoundOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		SaveRegionBounds();
		overlayManager.remove(regionBoundOverlay);
		worldMapPointManager.removeIf(x -> x.getName() != null && x.getName().contains("LP:"));
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
			SetMarkerActivation(ClickedPoint, CurrentFocusedPoint != ClickedPoint);

			RefreshRegionBounds();
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
		if (point == null)
		{
			return null;
		}

		float zoom = client.getRenderOverview().getWorldMapZoom();
		RenderOverview renderOverview = client.getRenderOverview();
		final WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(), renderOverview.getWorldMapPosition().getY(), 0);
		final Point middle = worldMapOverlay.mapWorldPointToGraphicsPoint(mapPoint);
		if (middle == null)
		{
			return null;
		}

		final int dx = (int) ((point.getX() - middle.getX()) / zoom);
		final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

		return mapPoint.dx(dx).dy(dy);
	}

	private final void SetMarkerActivation(LeagueRegionPoint RegionPoint, boolean shouldActivate)
	{
		if (shouldActivate)
		{
			if (CurrentFocusedPoint != null)
			{
				SetMarkerActivation(CurrentFocusedPoint, false);
			}

			CurrentFocusedPoint = RegionPoint;

			WorldMapPoint OldMarker = RegionPoint.OurPoint;
			worldMapPointManager.removeIf(x -> x == OldMarker);

			WorldPoint WorldPointLocation;
			if (RegionPoint.OurWorldPoint == null)
			{
				WorldPointLocation = LastDisplayedWorldPoint;
			}
			else
			{
				WorldPointLocation = RegionPoint.OurWorldPoint;
			}

			RegionPoint.OurPoint = new WorldMapPoint(WorldPointLocation, BOUNDS_SELECTED);
			RegionPoint.OurWorldPoint = WorldPointLocation;
			WorldMapPoint NewMarker = RegionPoint.OurPoint;
			NewMarker.setTarget(WorldPointLocation);
			NewMarker.setJumpOnClick(true);
			NewMarker.setName("LP: Region Bounds: " + RegionPoint.Region+ " " + WorldPointLocation + " guid:"+ RegionPoint.GUID);
			worldMapPointManager.add(NewMarker);
		}
		else
		{
			WorldMapPoint OldMarker = RegionPoint.OurPoint;
			worldMapPointManager.removeIf(x -> x == OldMarker);

			WorldPoint WorldPointLocation;
			if (RegionPoint.OurWorldPoint == null)
			{
				WorldPointLocation = LastDisplayedWorldPoint;
			}
			else
			{
				WorldPointLocation = RegionPoint.OurWorldPoint;
			}

			RegionPoint.OurPoint = new WorldMapPoint(WorldPointLocation, GetRegionImage(RegionPoint.Region));
			RegionPoint.OurWorldPoint = WorldPointLocation;
			WorldMapPoint NewMarker = RegionPoint.OurPoint;
			NewMarker.setTarget(WorldPointLocation);
			NewMarker.setJumpOnClick(true);
			NewMarker.setName("LP: Region Bounds: " + RegionPoint.Region + " " + WorldPointLocation + " guid:"+ RegionPoint.GUID);
			worldMapPointManager.add(NewMarker);
		}
	}

	private final Consumer<MenuEntry> SetNextRegionPointEntryCallback = n ->
	{
		LeagueRegionPoint LastRegionPoint = CurrentFocusedPoint;
		LeagueRegionPoint NewRegionPoint = new LeagueRegionPoint();

		UUID uuid = UUID.randomUUID();
		NewRegionPoint.GUID = uuid;
		NewRegionPoint.Region = CurrentRegion.Type;
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


		RefreshRegionBounds();
	};

	private final Consumer<MenuEntry> SetActiveRegionPointEntryCallback = n ->
	{
		SetMarkerActivation(LastClickedRegionPoint, true);


		RefreshRegionBounds();
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
		if (LastClickedRegionPoint == CurrentFocusedPoint)
		{
			CurrentFocusedPoint = null;
		}


		RefreshRegionBounds();
	};

	private final Consumer<MenuEntry> ConnectRegionPointEntryCallback = n ->
	{
		// If we are already connected, disconnect instead
		for (LeagueRegionPoint ConnectedPoint : CurrentFocusedPoint.ConnectedPoints)
		{
			if (ConnectedPoint == LastClickedRegionPoint)
			{
				ConnectedPoint.ConnectedPoints.remove(CurrentFocusedPoint);
				CurrentFocusedPoint.ConnectedPoints.remove(ConnectedPoint);

				RefreshRegionBounds();
				return;
			}
		}

		// At capacity, remove last added
		if (CurrentFocusedPoint.ConnectedPoints.size() == 2)
		{
			CurrentFocusedPoint.ConnectedPoints.get(1).ConnectedPoints.remove(CurrentFocusedPoint);
			CurrentFocusedPoint.ConnectedPoints.remove(1);
		}

		CurrentFocusedPoint.ConnectedPoints.add(LastClickedRegionPoint);

		// At capacity, remove last added
		if (LastClickedRegionPoint.ConnectedPoints.size() == 2)
		{
			LastClickedRegionPoint.ConnectedPoints.get(1).ConnectedPoints.remove(LastClickedRegionPoint);
			LastClickedRegionPoint.ConnectedPoints.remove(1);
		}

		LastClickedRegionPoint.ConnectedPoints.add(CurrentFocusedPoint);


		RefreshRegionBounds();
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
			if (CurrentRegion != null && TargetString.contains("Region Bounds: "))
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
		if (LastDisplayedWorldPoint == null)
		{
			return;
		}

		LastClickedRegionPoint = GetClickedRegionPoint(entries);
		if (CurrentFocusedPoint != null && CurrentRegion != null && CurrentRegion.Type != CurrentFocusedPoint.Region)
		{
			SetMarkerActivation(CurrentFocusedPoint, false);
			CurrentFocusedPoint = null;
		}

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
				if (CurrentFocusedPoint != LastClickedRegionPoint)
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

					if (CurrentFocusedPoint != null)
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
