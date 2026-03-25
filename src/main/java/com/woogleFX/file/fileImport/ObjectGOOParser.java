package com.woogleFX.file.fileImport;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.assets.GameVersion;

import java.util.ArrayList;
import java.util.Stack;

/** Opens the custom World of Goo 2 file format. */
public class ObjectGOOParser {

    private static class AssetObject {

    }

    private static class AssetStringObject extends AssetObject {

        String value;

        AssetStringObject(String value) {
            this.value = value;
        }

    }

    private static class AssetAssetObject extends AssetObject {

        final EditorObject value;

        AssetAssetObject(EditorObject value) {
            this.value = value;
        }

    }

    private static class AssetArrayObject extends AssetObject {

        final ArrayList<AssetObject> value;

        public AssetArrayObject(ArrayList<AssetObject> value) {
            this.value = value;
        }

    }


    private static AssetObject readString(Stack<String> tokens) {
        String token = tokens.remove(0);
        if (!token.equals("\"")) {
            tokens.remove(0);
        } else {
            token = "";
        }
        return new AssetStringObject(token);
    }


    private static AssetObject readList(Class<? extends EditorObject> type, Stack<String> tokens, String typeId, EditorObject parent) {

        ArrayList<AssetObject> assetObjects = new ArrayList<>();
        while (true) {

            String token = tokens.remove(0);

            switch (token) {

                case "]" -> {
                    return new AssetArrayObject(assetObjects);
                }

                case "[" -> assetObjects.add(readList(type, tokens, typeId, parent));

                case "\"" -> assetObjects.add(readString(tokens));

                case "{" -> {
                    if (type != null) assetObjects.add(readAsset(type, tokens, typeId, parent));
                }

                case "," -> {

                }

                default -> assetObjects.add(new AssetStringObject(token));

            }

        }

    }

    private static AssetAssetObject readAsset(Class<? extends EditorObject> type, Stack<String> tokens, String typeId, EditorObject parent) {

        EditorObject asset = ObjectCreator.create(type, parent, typeId, GameVersion.VERSION_WOG2);

        String name = null;

        while (true) {

            String token = tokens.remove(0);

            switch (token) {

                case "}" -> {
                    return new AssetAssetObject(asset);
                }

                case "[" -> {
                    Class<? extends EditorObject> typeClass = null;
                    for (EditorAttribute editorAttribute : asset.getAttributes()) {
                        if (editorAttribute.getChildAlias() != null && editorAttribute.getName().equals(name)) {
                            typeClass = editorAttribute.getChildAlias();
                            break;
                        }
                    }
                    StringBuilder total = new StringBuilder();
                    for (AssetObject assetObject : ((AssetArrayObject) readList(typeClass, tokens, name, asset)).value) {
                        if (assetObject instanceof AssetStringObject assetStringObject) {
                            total.append(assetStringObject.value).append(",");
                        }
                    }
                    if (!total.isEmpty()) {
                        if (name != null) {
                            asset.setAttribute2(name, total.substring(0, total.length() - 1));
                        }
                    }
                    name = null;
                }

                case "\"" -> {
                    if (name != null) {
                        asset.setAttribute2(name, ((AssetStringObject)readString(tokens)).value);
                        name = null;
                    } else {
                        name = tokens.remove(0);
                        tokens.remove(0);
                    }
                }

                case "{" -> {
                    Class<? extends EditorObject> typeClass = null;
                    for (EditorAttribute editorAttribute : asset.getAttributes()) {
                        if (editorAttribute.getChildAlias() != null && editorAttribute.getName().equals(name)) {
                            typeClass = editorAttribute.getChildAlias();
                            break;
                        }
                    }
                    if (typeClass != null) {
                        readAsset(typeClass, tokens, name, asset);
                    }
                    name = null;
                }

                case ":", "," -> {

                }

                default -> {
                    if (name != null) {
                        asset.setAttribute2(name, token);
                    }
                    name = null;
                }

            }

        }

    }


    @SuppressWarnings("unchecked")
    public static <T extends EditorObject> T read(Class<T> assetType, String text, String typeId) {

        StringBuilder currentWord = new StringBuilder();

        Stack<String> tokens = new Stack<>();

        for (int index = 0; index < text.length(); index++) {

            char c = text.charAt(index);

            if (c == '{' || c == '}' || c == '[' || c == ']' || c == ':' || c == ',' || c == '"') {
                if (!currentWord.isEmpty()) tokens.add(currentWord.toString());
                currentWord = new StringBuilder();
                tokens.add(String.valueOf(c));
            } else {
                if (c != '\n' && c != '\t' && c != ' ' && c != '\r') {
                    currentWord.append(c);
                }
            }

        }

        tokens.remove(0);

        AssetAssetObject asset = readAsset(assetType, tokens, typeId, null);

        return (T) asset.value;

    }

}
