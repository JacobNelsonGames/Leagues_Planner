package com.Posiedien_Leagues_Planner;

import java.awt.*;

public enum RegionType
{
    NONE,
    MISTHALIN,
    KARAMJA,
    KANDARIN,
    ASGARNIA,
    FREMENNIK,
    KOUREND,
    WILDERNESS,
    MORYTANIA,
    TIRANNWN,
    DESERT;

    public Color GetRegionColor(RegionType Type)
    {
        switch (Type)
        {
            case MISTHALIN:
                return new Color(255, 2, 2, 100);
            case KARAMJA:
                return new Color(46, 108, 23, 100);
            case KANDARIN:
                return new Color(231, 143, 10, 100);
            case ASGARNIA:
                return new Color(46, 59, 234, 100);
            case FREMENNIK:
                return new Color(121, 67, 3, 100);
            case KOUREND:
                return new Color(31, 224, 179, 100);
            case WILDERNESS:
                return new Color(94, 14, 14, 100);
            case MORYTANIA:
                return new Color(102, 3, 114, 100);
            case TIRANNWN:
                return new Color(130, 255, 105, 100);
            case DESERT:
                return new Color(255, 226, 1, 100);
        }

        return Color.WHITE;
    }
}
