package com.woogleFX.engine.fx.hierarchy;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.file.FileManager;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.HierarchyDragAction;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.worldOfGoo.level.*;
import com.worldOfGoo.resrc.*;
import com.worldOfGoo2.level._2_Level_BallInstance;
import com.worldOfGoo2.level._2_Level_Item;
import com.worldOfGoo2.level._2_Level_TerrainGroup;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class HierarchyManager {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyManager.class);


    public static javafx.scene.image.Image getObjectIcon(Class<? extends EditorObject> type, EditorObject object) {

        String attributeManifestPath = "/" + type.getName().replace('.', '/') + ".png";

        // Special case: Terrain BallInstances
        if (object instanceof _2_Level_BallInstance && object.getAttribute("type").stringValue().equals("Terrain")) {
            attributeManifestPath = "/com/worldOfGoo2/level/TerrainBallInstance.png";
        }

        // Special case: Items
        if (object instanceof _2_Level_Item) {
            attributeManifestPath = "/com/worldOfGoo2/_itemIcons/" + object.getAttribute("type").stringValue() + ".png";
        }

        InputStream inputStream = type.getResourceAsStream(attributeManifestPath);
        if (inputStream == null) return null;
        try {
            BufferedImage image = ImageIO.read(inputStream);
            return SwingFXUtils.toFXImage(image, null);
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }


    }


    private static TreeTableRow<EditorObject> dragSourceRow;
    public static TreeTableRow<EditorObject> getDragSourceRow() {
        return dragSourceRow;
    }


    public static void updateNameCell(TreeTableCell<EditorObject, String> cell, String item, boolean empty) {

        if (empty) {
            // If this is an empty cell, set its text and graphic to empty.
            // This prevents the cell from retaining other cells' information.
            cell.setText(null);
            cell.setGraphic(null);
        } else {
            // setTextFill(Paint.valueOf("FFFFFFFF"));
            // Update this cell's text.
            cell.setText(item);
            // Override the default padding that ruins the text.
            cell.setPadding(new Insets(-2, 0, 0, 3));
        }

    }


    public static void updateElementCell(TreeTableCell<EditorObject, String> cell, String item, boolean empty) {

        if (empty) {
            // If this is an empty cell, set its text and graphic to empty.
            // This prevents the cell from retaining other cells' information.
            cell.setText(null);
            cell.setGraphic(null);
        } else {
            // Update this cell's text.
            cell.setText(item);
            // Override the default padding that ruins the text.
            cell.setPadding(new Insets(-2, 0, 0, 3));

            if (cell.getTableRow().getItem() != null) {
                ImageView imageView;

                imageView = new ImageView(getObjectIcon(cell.getTableRow().getItem().getClass(), cell.getTableRow().getItem()));
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);


                if (cell.getTableRow().getItem().getType().equals("Item")) {
                    cell.setText(cell.getTableRow().getItem().getAttribute("type").stringValue());
                }

                // If the cell's EditorObject is invalid, display its graphic with a warning symbol.
                // Otherwise, just display its graphic.

                boolean valid = true;

                EditorObject editorObject = cell.getTableRow().getItem();

                for (EditorAttribute attribute : editorObject.getAttributes()) {
                    if (attribute.stringValue().isEmpty()) {
                        if (!attribute.getType().verify(editorObject, attribute.getDefaultValue(), attribute.getRequired())) {
                            valid = false;
                        }
                    } else if (!attribute.getType().verify(editorObject, attribute.actualValue(), attribute.getRequired())) {
                        valid = false;
                    }
                }

                if (!valid) {
                    ImageView failedImg = new ImageView(FileManager.getFailedImage());
                    cell.setGraphic(new StackPane(imageView, failedImg));
                    cell.setStyle("-fx-text-fill: red");
                } else {
                    cell.setGraphic(imageView);
                    cell.setStyle("-fx-text-fill: black");
                }
            }
        }

    }


    private static boolean checkIsDropValid(TreeTableView<EditorObject> hierarchy, EditorObject fromParent, int fromIndex, EditorObject toParent, int toIndex) {

        if (toParent == fromParent && toIndex == fromIndex) return false;

        EditorObject fromItem = fromParent.getChildren().get(fromIndex);

        // YOU CAN'T PUT AN OBJECT INSIDE ITSELF
        if (fromItem == toParent) return false;

        // Or inside an object that doesn't have it as a possible child
        if (Stream.of(toParent.getPossibleChildren()).noneMatch(e -> e.equals(fromItem.getClass())))
            return false;

        // Or above every SetDefaults (meaning at position 2) if it's a resource
        if ((fromItem instanceof Image || fromItem instanceof Sound || fromItem instanceof font) && toIndex == 2)
            return false;

        // Or anywhere that would put a resource at position 2 if it's a SetDefaults
        if (fromItem instanceof SetDefaults && (fromIndex == 2 && !(hierarchy.getTreeItem(3).getValue() instanceof SetDefaults)))
            return false;

        return true;

    }


    public static boolean handleDragDrop(TreeTableView<EditorObject> hierarchy, EditorObject fromParent, int fromIndex, EditorObject toParent, int toIndex) {

        // TODO: give the user a way to choose if they want an object to be a child of the object above it

        // System.out.println(fromParent + ", " + fromIndex + ", " + toParent + ", " + toIndex);

        EditorObject fromItem = fromParent.getChildren().get(fromIndex);
        EditorObject toItem = (toParent.getChildren().size() <= toIndex) ? null : toParent.getChildren().get(toIndex);

        // System.out.println(fromItem + ", " + toItem);

        if (!checkIsDropValid(hierarchy, fromParent, fromIndex, toParent, toIndex)) return false;

        fromParent.getChildren().remove(fromItem);
        fromParent.getTreeItem().getChildren().remove(fromItem.getTreeItem());
        fromItem.setParent(toParent, toIndex + (toParent == fromParent && toIndex > fromIndex ? -1 : 0), toIndex + (toParent == fromParent && toIndex > fromIndex ? -1 : 0));

        hierarchy.getSelectionModel().select(toIndex);
        hierarchy.refresh();

        int i = FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().getSelectedIndex();
        int total = FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getTabs().size();
        FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select((i + 1) % total);
        FXHierarchySwitcherButtons.getHierarchySwitcherButtons().getSelectionModel().select(i);

        handlePostDragEvents(fromItem, toItem);

        return true;

    }


    private static void handlePostDragEvents(EditorObject fromItem, EditorObject toItem) {

        if (fromItem.getParent() instanceof Resources) AssetManager.getAsset().reAssignSetDefaultsToAllResources();
        else if (fromItem instanceof Vertex) fromItem.getParent().update();

        if (fromItem instanceof _2_Level_TerrainGroup && toItem instanceof _2_Level_TerrainGroup) {
            for (EditorObject ball : ((WOG2Level)AssetManager.getAsset()).getLevel().getChildren("balls"))
                ball.setAttribute("terrainGroup", ball.getAttribute("terrainGroup").stringValue()); // TODO2: there must be a better way
        }

    }


    public static TreeTableRow<EditorObject> createRow(TreeTableView<EditorObject> hierarchy) {

        TreeTableRow<EditorObject> row = new TreeTableRow<>();

        row.setOnMousePressed(event -> {
            if (row.getTreeItem() == null) return;
            if (event.getButton().equals(MouseButton.SECONDARY)) row.setContextMenu(contextMenuForEditorObject(row.getTreeItem().getValue()));
        });

        row.setOnDragDetected(event -> {
            TreeItem<EditorObject> selected2 = hierarchy.getSelectionModel().getSelectedItem();
            if (selected2 == null) return;

            if (row.getTreeItem() == null) return;

            Dragboard db = hierarchy.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(selected2.getValue().getClass().getName());
            db.setContent(content);
            event.consume();

            dragSourceRow = row;

            row.setId("dragTarget");

        });

        row.setOnDragExited(event -> {
            // row.setStyle("");
            row.setStyle("-fx-border-width: 0 0 0 0;");
            row.setTranslateY(0);
            row.setPadding(new Insets(1, 0, 0, 0));
        });

        row.setOnDragOver(event -> {
            if (event.getDragboard().hasString() && row.getTreeItem() != null) {

                if (event.getY() + row.getTranslateY() >= 9) {
                    row.setStyle("-fx-border-color: #a0a0ff; -fx-border-width: 0 0 2 0;");
                    row.setTranslateY(1);
                } else {
                    row.setStyle("-fx-border-color: #a0a0ff; -fx-border-width: 2 0 0 0;");
                    row.setTranslateY(-1);
                }
                row.setPadding(new Insets(0.5, 0, -0.5, 0));

                row.toFront();

                event.acceptTransferModes(TransferMode.MOVE);

            }
            event.consume();
        });

        row.setOnDragDropped(event -> {
            dragSourceRow.setId("notDragTarget");
            if (!row.isEmpty()) {
                EditorObject object = FXHierarchy.getHierarchy().getTreeItem(dragSourceRow.getIndex()).getValue();

                EditorObject fromParent = object.getParent();
                EditorObject toParent = row.getItem().getParent();
                if (fromParent == null || toParent == null) return;
                int fromIndex = fromParent.getChildren().indexOf(object);
                int toIndex = toParent.getChildren().indexOf(row.getItem());

                if (event.getY() + row.getTranslateY() >= 9) {
                    toIndex += 1;
                }

                boolean completed = handleDragDrop(hierarchy, fromParent, fromIndex, toParent, toIndex);
                event.setDropCompleted(event.getDragboard().hasString() && completed);

                if (completed) {
                    hierarchy.getSelectionModel().clearSelection();
                    hierarchy.getSelectionModel().select(FXHierarchy.getHierarchy().getTreeItem(toIndex));
                    UndoManager.registerChange(new HierarchyDragAction(object, fromParent, fromIndex, toParent, toIndex));
                }

            }
            event.consume();
        });

        row.setPadding(new Insets(1, 0, 0, 0));

        return row;

    }


    public static ContextMenu contextMenuForEditorObject(EditorObject object) {

        // Create the content menu.
        ContextMenu menu = new ContextMenu();

        menu.getItems().addAll(object.getAdditionalContextMenuItems());

        // For every object that can be created as a child of this object:
        int i = 0;
        for (Class<? extends EditorObject> childToAdd : object.getPossibleChildren()) {

            // Create a menu item representing creating this child.
            MenuItem addItemItem = new MenuItem(" Add " + EditorObject.getEditorObjectName(childToAdd));

            // Attempt to set graphics for this menu item.
            addItemItem.setGraphic(new ImageView(getObjectIcon(childToAdd, null)));

            // Set the item's action to creating the child, with the object as its parent.
            int finalI = i;
            addItemItem.setOnAction(event -> {
                if (object.getVersion() == GameVersion.VERSION_WOG2)
                    ObjectAdder.addObject2(childToAdd, object.getPossibleChildrenTypeIDs()[finalI], object);
                else ObjectAdder.addObject(childToAdd, object, true);
            });
            i++;

            menu.getItems().add(addItemItem);
        }

        return menu;

    }

}
