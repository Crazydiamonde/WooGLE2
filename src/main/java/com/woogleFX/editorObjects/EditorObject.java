package com.woogleFX.editorObjects;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.*;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.engine.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.assets.GameVersion;

import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EditorObject {

    public static final Logger logger = LoggerFactory.getLogger(EditorObject.class);

    /** The parent of this object. */
    private EditorObject parent;
    public final EditorObject getParent() {
        return parent;
    }
    public final void setParent(EditorObject parent, int row, int treeTableRow) {
        this.parent = parent;
        if (parent == null) return;
        parent.getChildren().add(row, this);
        if (parent.attributeExists(typeID) &&
                (parent.getAttribute2(typeID).getType() == InputField._2_CHILD_HIDDEN
                || parent.getAttribute2(typeID).getType() == InputField._2_LIST_CHILD_HIDDEN)) return;
        parent.getTreeItem().getChildren().add(treeTableRow, getTreeItem());
    }
    public final void setParent(EditorObject p) {
        setParent(p, p == null ? 0 : p.getChildren().size(), p == null ? 0 : p.getTreeItem().getChildren().size());
    }

    /** The children of this object. */
    private final ArrayList<EditorObject> children = new ArrayList<>();
    public final ArrayList<EditorObject> getChildren() {
        return children;
    }

    /** The name that World of Goo assigns to this object.
     * This is the same for every object of the same type. */
    private final String type;
    public final String getType() {
        return type;
    }

    /** The version of this object. */
    private final GameVersion version;
    public final GameVersion getVersion() {
        return version;
    }


    private Asset asset;
    public final Asset getAsset() {
        return asset;
    }

    private static final Map<Class<? extends EditorObject>, AttributeManifest> attributeManifestMap = new HashMap<>();
    public static String getEditorObjectName(Class<? extends EditorObject> tClass) {
        if (attributeManifestMap.containsKey(tClass))
            return attributeManifestMap.get(tClass).getLocalName();
        else {
            try {
                // fuck you! TODO: make this sane
                return ((EditorObject)tClass.getConstructors()[0].newInstance(null, null)).getType();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public EditorObject(EditorObject parent, GameVersion version) {
        this.parent = parent;
        this.version = version;

        AttributeManifest attributeManifest;
        if (attributeManifestMap.containsKey(getClass())) {
            attributeManifest = attributeManifestMap.get(getClass());
        } else {
            String attributeManifestPath = "/" + getClass().getName().replace('.', '/') + ".xml";
            InputStream inputStream = getClass().getResourceAsStream(attributeManifestPath);
            try {
                attributeManifest = new XmlMapper().readValue(inputStream, AttributeManifest.class);
            } catch (IOException e) {
                System.out.println(attributeManifestPath);
                logger.error("", e);
                type = "INVALID";
                return;
            }
            attributeManifestMap.put(getClass(), attributeManifest);
        }
        type = attributeManifest.getLocalName();
        ArrayList<EditorAttribute> attributes1 = new ArrayList<>();
        for (EditorAttribute editorAttribute : attributeManifest.getAttributes()) {
            EditorAttribute realAttribute = new EditorAttribute();
            realAttribute.setDefaultValue(editorAttribute.getDefaultValue());
            realAttribute.setObject(this);
            realAttribute.setChildAlias(editorAttribute.getChildAlias());
            realAttribute.setValue(editorAttribute.actualValue());
            realAttribute.setName(editorAttribute.getName());
            realAttribute.setType(editorAttribute.getType());
            realAttribute.setRequired(editorAttribute.getRequired());
            attributes1.add(realAttribute);
        }
        setAttributes(attributes1.toArray(new EditorAttribute[0]));
        if (attributeManifest.getMetaAttributes() != null)
            for (MetaEditorAttribute metaEditorAttribute : attributeManifest.getMetaAttributes()) {
            MetaEditorAttribute mine = new MetaEditorAttribute();
            mine.setName(metaEditorAttribute.getName());
            mine.setOpenByDefault(metaEditorAttribute.getOpenByDefault());
            mine.setChildren(new ArrayList<>());
            for (MetaEditorAttribute child : metaEditorAttribute.getChildren()) {
                MetaEditorAttribute mine2 = new MetaEditorAttribute();
                mine2.setName(child.getName());
                mine2.setOpenByDefault(child.getOpenByDefault());
                mine2.setChildren(new ArrayList<>());
                mine.getChildren().add(mine2);
                for (MetaEditorAttribute child2 : child.getChildren()) {
                    MetaEditorAttribute mine3 = new MetaEditorAttribute();
                    mine3.setName(child2.getName());
                    mine3.setOpenByDefault(child2.getOpenByDefault());
                    mine3.setChildren(new ArrayList<>());
                    mine2.getChildren().add(mine3);
                }
            }
            metaAttributes.add(mine);
        }
    }


    private final TreeItem<EditorObject> treeItem = new TreeItem<>(this);
    public final TreeItem<EditorObject> getTreeItem() {
        return treeItem;
    }


    private final ArrayList<ObjectComponent> objectComponents = new ArrayList<>();
    public final ObjectComponent[] getObjectComponents() {
        return objectComponents.toArray(new ObjectComponent[0]);
    }
    
    public final void addObjectComponent(ObjectComponent c) {
        objectComponents.add(c);
    }
    
    public final void addObjectComponents(List<ObjectComponent> c) {
        objectComponents.addAll(c);
    }
    
    public final void removeObjectComponent(ObjectComponent c) {
        objectComponents.remove(c);
        // TODO: is there a better way to do this?
        if (Arrays.stream(AssetManager.getAsset().getSelectedComponents()).anyMatch(e -> e == c)) {
            ObjectComponent[] components = new ObjectComponent[AssetManager.getAsset().getSelectedComponents().length - 1];
            int i = 0;
            for (ObjectComponent objectComponent : AssetManager.getAsset().getSelectedComponents()) {
                if (objectComponent == c) continue;
                components[i] = objectComponent;
                i++;
            }
            AssetManager.getAsset().setSelectedComponents(components);
        }
    }
    
    public final void clearObjectComponents() {
        for (ObjectComponent objectComponent : objectComponents.toArray(ObjectComponent[]::new)) {
            removeObjectComponent(objectComponent);
        }
    }
    
    public final boolean containsObjectComponent(ObjectComponent c) {
        return objectComponents.contains(c);
    }


    public String getName() {
        return "";
    }


    public void update() {
    }

    public void frameUpdate(double deltaTime) {
    }


    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return (Class<? extends EditorObject>[]) new Class[0];
    }


    public List<FXMenu.EditorMenuItem> getAdditionalContextMenuItems() {
        return new ArrayList<>();
    }


    public String[] getPossibleChildrenTypeIDs() {
        return new String[0];
    }


    /** The EditorAttributes for this object.
     * All of these are added when the object is created. */
    private EditorAttribute[] attributes = new EditorAttribute[0];
    public final EditorAttribute[] getAttributes(){
        return this.attributes;
    }
    public final boolean attributeExists(String name) {
        return Arrays.stream(attributes).anyMatch(e -> e.getName().equals(name));
    }
    public final synchronized EditorAttribute getAttribute(String name) {
        AttributeAdapter attributeAdapter2 = attributeAdapters.get(name);
        if (attributeAdapter2 != null) return attributeAdapter2.getValue();
        AttributeAdapter[] array = attributeAdapters.values().toArray(new AttributeAdapter[0]);
        for (AttributeAdapter attributeAdapter : array) if (attributeAdapter.name.equals(name))
        {
            return attributeAdapter.getValue();
        }
        for (EditorAttribute attribute : attributes) if (attribute.getName().equals(name)) return attribute;
        logger.error("Accessed invalid attribute " + name + " (for " + getType() + ")");
        Thread.dumpStack();
        return EditorAttribute.NULL;
    }
    public final EditorAttribute getAttribute2(String name) {
        for (EditorAttribute attribute : attributes) if (attribute.getName().equals(name)) return attribute;
        logger.error("Accessed invalid attribute " + name + " (for " + getType() + ")");
        Thread.dumpStack();
        return EditorAttribute.NULL;
    }
    public final void setAttribute(String name, Object value) {
        for (AttributeAdapter attributeAdapter : attributeAdapters.values()) if (attributeAdapter.name.equals(name))
        {
            attributeAdapter.setValue(value.toString());
            return;
        }
        getAttribute(name).setValue(String.valueOf(value));
    }
    public final void setAttribute2(String name, Object value) {
        getAttribute2(name).setValue(String.valueOf(value));
    }
    public final void setAttributes(EditorAttribute[] attributes) {
        this.attributes = attributes;
    }

    /** The meta attributes of the object.
     * These control how attributes are displayed to the user. */
    private ArrayList<MetaEditorAttribute> metaAttributes = new ArrayList<>();
    public final ArrayList<MetaEditorAttribute> getMetaAttributes() {
        return metaAttributes;
    }
    public final void setMetaAttributes(ArrayList<MetaEditorAttribute> meta) {
        this.metaAttributes = meta;
    }


    private final Map<String, AttributeAdapter> attributeAdapters = new HashMap<>();
    public final Map<String, AttributeAdapter> getAttributeAdapters() {
        return attributeAdapters;
    }
    public final void addAttributeAdapter(String name, AttributeAdapter attributeAdapter) {
        attributeAdapters.put(name, attributeAdapter);
    }

    public final EditorObject getChild(String attributeName) {
        for (EditorObject child : children)
            if (child.getTypeID().equals(attributeName))
                return child;
        throw new RuntimeException("Could not find child: " + attributeName);
    }
    
    public final List<EditorObject> getChildren(String attributeName) {
        return children.stream().filter(e -> e != null && e.getTypeID().equals(attributeName)).toList();
    }

    /** The object's name as a JSON child.
     * This is kind of a workaround for supporting JSON with the current very XML system. */
    private String typeID = "";
    public final String getTypeID() {
        return typeID;
    }
    public final void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    /** Called when the object is first created. */
    public void onLoaded(Asset asset) {
        this.asset = asset;
    }

    /** Gets called when the object is created.
     * Don't create other objects in this, instead, create ObjectCreationActions
     * and the ObjectManager will create them automatically.
     * (This is done to preserve the objects' original positions in the scene tree) */
    public List<ObjectCreationAction> onCreate() {
        return new ArrayList<>();
    }
    
    /** Gets called when the object is deleted.
     * Don't delete other objects in this, instead, create ObjectDestructionActions
     * and the ObjectManager will delete them automatically.
     * (This is done to preserve the objects' original positions in the scene tree) */
    public List<ObjectDestructionAction> onDelete() {
        List<ObjectDestructionAction> objectDestructionActions = new ArrayList<>();
        for (EditorObject child : children) {
            objectDestructionActions.addAll(child.onDelete());
            objectDestructionActions.add(new ObjectDestructionAction(child));
        }
        return objectDestructionActions;
    }

    public void createRequiredChildren() {

    }


    public void addAllChildren(Collection<EditorObject> editorObjects) {
        editorObjects.add(this);
        for (EditorObject child : children) child.addAllChildren(editorObjects);
    }

}
