package com.woogleFX.file.fileImport;
import com.woogleFX.assets.Asset;
import com.woogleFX.assets.AssetLoader;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.gui.AssetSelector;
import com.woogleFX.file.FileManager;
import com.woogleFX.engine.fx.PaletteManager;
import com.woogleFX.assets.GameVersion;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class PropertiesOpener extends DefaultHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "oldWOG" -> {
                if (!attributes.getValue(attributes.getIndex("filepath")).isEmpty()) {
                    FileManager.setOldWOG1dir(attributes.getValue(attributes.getIndex("filepath")));
                }
            }
            case "newWOG" -> {
                if (!attributes.getValue(attributes.getIndex("filepath")).isEmpty()) {
                    FileManager.setNewWOG1dir(attributes.getValue(attributes.getIndex("filepath")));
                }
            }
            case "WOG2" -> {
                if (!attributes.getValue(attributes.getIndex("filepath")).isEmpty()) {
                    FileManager.setWog2dir(attributes.getValue(attributes.getIndex("filepath")));
                }
            }
            case "Ball" -> {
                PaletteManager.addPaletteBall(attributes.getValue(attributes.getIndex("ball")));
                PaletteManager.addPaletteVersion(switch(attributes.getValue(attributes.getIndex("version"))) {
                    case "1.3" -> GameVersion.VERSION_WOG1_OLD;
                    case "1.5" -> GameVersion.VERSION_WOG1_NEW;
                    default -> GameVersion.VERSION_WOG2;
                });
            }
            case "Asset" -> {
                String type = attributes.getValue("type");
                String name = attributes.getValue("name");
                String file = attributes.getValue("file");
                String version = attributes.getValue("version");
                try {
                    AssetManager.addRecentlyOpenedAsset(new AssetManager.AssetDescription((Class<? extends Asset>) Class.forName(type), new File(file), name, switch(version) {
                        case "1.3" -> GameVersion.VERSION_WOG1_OLD;
                        case "1.5" -> GameVersion.VERSION_WOG1_NEW;
                        default -> GameVersion.VERSION_WOG2;
                    }));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
    }

    @Override
    public void characters(char[] ch, int start, int length) {
    }
}