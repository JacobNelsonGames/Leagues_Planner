package com.Posiedien_Leagues_Planner;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.PluginDescriptor;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CustomMenuEntry implements MenuEntry
{
    String OptionString;
    String TargetString;
    int LocalIdentifier;
    MenuAction LocalMenuAction;
    int LocalParam0;
    int LocalParam1;
    boolean LocalIsForceLeftClick;
    boolean LocalIsDeprioritized;
    MenuEntry ParentEntry;
    boolean LocalIsItemOp;

    @Override
    public String getOption()
    {
        return OptionString;
    }

    @Override
    public MenuEntry setOption(String option)
    {
        OptionString = option;
        return this;
    }

    @Override
    public String getTarget()
    {
        return TargetString;
    }

    @Override
    public MenuEntry setTarget(String target)
    {
        TargetString = target;
        return this;
    }

    @Override
    public int getIdentifier()
    {
        return LocalIdentifier;
    }

    @Override
    public MenuEntry setIdentifier(int identifier)
    {
        LocalIdentifier = identifier;
        return this;
    }

    @Override
    public MenuAction getType()
    {
        return LocalMenuAction;
    }

    @Override
    public MenuEntry setType(MenuAction type)
    {
        LocalMenuAction = type;
        return this;
    }

    @Override
    public int getParam0()
    {
        return LocalParam0;
    }

    @Override
    public MenuEntry setParam0(int param0)
    {
        LocalParam0 = param0;
        return this;
    }

    @Override
    public int getParam1()
    {
        return LocalParam1;
    }

    @Override
    public MenuEntry setParam1(int param1)
    {
        LocalParam1 = param1;
        return this;
    }

    @Override
    public boolean isForceLeftClick()
    {
        return LocalIsForceLeftClick;
    }

    @Override
    public MenuEntry setForceLeftClick(boolean forceLeftClick)
    {
        LocalIsForceLeftClick = forceLeftClick;
        return this;
    }

    @Override
    public boolean isDeprioritized()
    {
        return LocalIsDeprioritized;
    }

    @Override
    public MenuEntry setDeprioritized(boolean deprioritized)
    {
        LocalIsDeprioritized = deprioritized;
        return this;
    }

    @Override
    public MenuEntry onClick(Consumer<MenuEntry> callback)
    {
        return this;
    }

    @Override
    public MenuEntry setParent(MenuEntry parent)
    {
        ParentEntry = parent;
        return this;
    }

    @Nullable
    @Override
    public MenuEntry getParent()
    {
        return ParentEntry;
    }

    @Override
    public boolean isItemOp()
    {
        return LocalIsItemOp;
    }

    @Override
    public int getItemOp()
    {
        return 0;
    }

    @Override
    public int getItemId()
    {
        return -1;
    }

    @Nullable
    @Override
    public Widget getWidget()
    {
        return null;
    }

    @Nullable
    @Override
    public NPC getNpc()
    {
        return null;
    }

    @Nullable
    @Override
    public Player getPlayer()
    {
        return null;
    }

    @Nullable
    @Override
    public Actor getActor()
    {
        return null;
    }
}
