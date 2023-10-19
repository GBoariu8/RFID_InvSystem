package com.example.rfid_inventorysystem.Hardware.TagsHandling;

import java.util.Map;

public interface TagsHandling {
    boolean readNewTag();
    String getOldEPC();
    Map<String, Object> getTagInfo();
}
