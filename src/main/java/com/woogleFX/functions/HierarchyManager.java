package com.woogleFX.functions;

import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.file.FileManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.functions.undoHandling.userActions.HierarchyDragAction;
import com.woogleFX.structures.WorldLevel;
import com.worldOfGoo.addin.Addin;
import com.worldOfGoo.level.Level;
import com.worldOfGoo.level.Vertex;
import com.worldOfGoo.resrc.*;
import com.worldOfGoo.scene.Scene;
import com.worldOfGoo.text.TextStrings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class HierarchyManager {

    public static Image getObjectIcon(EditorObject editorObject) {
        try {
            return FileManager.getIcon("ObjectIcons\\" + editorObject.getIcon() + ".png");
        } catch (FileNotFoundException e) {
            return null;
        }
    }


    private static int oldDropIndex;
    public static int getOldDropIndex() {
        return oldDropIndex;
    }
    public static void setOldDropIndex(int oldDropIndex) {
        HierarchyManager.oldDropIndex = oldDropIndex;
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

                imageView = new ImageView(getObjectIcon(cell.getTableRow().getItem()));

                // If the cell's EditorObject is invalid, display its graphic with a warning
                // symbol.
                // Otherwise, just display its graphic.

                boolean valid = true;

                EditorObject editorObject = cell.getTableRow().getItem();

                for (EditorAttribute attribute : editorObject.getAttributes()) {
                    if (attribute.stringValue().isEmpty()) {
                        if (!InputField.verify(editorObject, attribute.getType(), attribute.getDefaultValue()) && attribute.getRequiredInFile()) return;
                    } else if (!InputField.verify(editorObject, attribute.getType(), attribute.actualValue())) {
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


    public static boolean handleDragDrop(TreeTableView<EditorObject> hierarchy, int toIndex) {

        // TODO: give the user a way to choose if they want an object to be a child of the object above it

        if (toIndex == oldDropIndex) return false;

        EditorObject toItem = hierarchy.getTreeItem(toIndex).getValue();
        EditorObject fromItem = hierarchy.getTreeItem(oldDropIndex).getValue();

        EditorObject absoluteParent = fromItem;
        while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();

        WorldLevel level = LevelManager.getLevel();

        ArrayList<EditorObject> list;
        if (absoluteParent instanceof Scene) list = level.getScene();
        else if (absoluteParent instanceof Level) list = level.getLevel();
        else if (absoluteParent instanceof ResourceManifest) list = level.getResrc();
        else if (absoluteParent instanceof Addin) list = level.getAddin();
        else if (absoluteParent instanceof TextStrings) list = level.getText();
        else return false;

        int indexOfToItemInList = list.indexOf(toItem);

        // YOU CAN'T PUT AN OBJECT INSIDE ITSELF
        if (toItem.getChildren().contains(fromItem)) return false;

        // Or inside an object that doesn't have it as a possible child
        if (Stream.of(toItem.getParent().getPossibleChildren()).noneMatch(e -> e.equals(fromItem.getType()))) return false;

        // Or above every SetDefaults (meaning at position 2) if it's a resource
        if ((fromItem instanceof ResrcImage || fromItem instanceof Sound || fromItem instanceof Font) && toIndex == 2) return false;

        // Or anywhere that would put a resource at position 2 if it's a SetDefaults
        if (fromItem instanceof SetDefaults && (oldDropIndex == 2 && !(hierarchy.getTreeItem(3).getValue() instanceof SetDefaults))) return false;

        // Add the dragged item just above the item that it gets dragged to
        int indexOfToItem = toItem.getParent().getChildren().indexOf(toItem);

        fromItem.getParent().getChildren().remove(fromItem);
        fromItem.getParent().getTreeItem().getChildren().remove(fromItem.getTreeItem());

        fromItem.setParent(toItem.getParent(), indexOfToItem);

        list.remove(fromItem);
        list.add(indexOfToItemInList, fromItem);

        if (fromItem.getParent() instanceof Resources) LevelManager.getLevel().reAssignSetDefaultsToAllResources();
        else if (fromItem instanceof Vertex) fromItem.getParent().update();

        hierarchy.getSelectionModel().select(toIndex);
        hierarchy.refresh();

        return true;

    }


    public static TreeTableRow<EditorObject> createRow(TreeTableView<EditorObject> hierarchy) {

        TreeTableRow<EditorObject> row = new TreeTableRow<>();

        row.setOnMousePressed(event -> {

            if (row.getTreeItem() == null) return;

            SelectionManager.setSelected(row.getTreeItem().getValue());
            FXPropertiesView.changeTableView(row.getTreeItem().getValue());

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
            oldDropIndex = row.getIndex();
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
                    row.setPadding(new Insets(0, 0, 0, 0));
                } else {
                    row.setStyle("-fx-border-color: #a0a0ff; -fx-border-width: 2 0 0 0;");
                    row.setTranslateY(-1);
                    row.setPadding(new Insets(0, 0, 0, 0));
                }

                row.toFront();

                event.acceptTransferModes(TransferMode.MOVE);

            }
            event.consume();
        });

        row.setOnDragDropped(event -> {
            dragSourceRow.setId("notDragTarget");
            if (!row.isEmpty()) {
                int toIndex = row.getIndex();
                if (event.getY() + row.getTranslateY() >= 9) toIndex += 1;
                if (toIndex > oldDropIndex) toIndex -= 1;
                boolean completed = handleDragDrop(hierarchy, toIndex);
                event.setDropCompleted(event.getDragboard().hasString() && completed);

                if (completed) UndoManager.registerChange(new HierarchyDragAction(FXHierarchy.getHierarchy().getTreeItem(oldDropIndex).getValue(), oldDropIndex, toIndex));

            }
            event.consume();
        });

        row.setPadding(new Insets(1, 0, 0, 0));

        return row;

    }


    public static ContextMenu contextMenuForEditorObject(EditorObject object) {

        // Create the content menu.
        ContextMenu menu = new ContextMenu();

        // For every object that can be created as a child of this object:
        for (String childToAdd : object.getPossibleChildren()) {

            // Create a menu item representing creating this child.
            MenuItem addItemItem = new MenuItem("Add " + childToAdd);

            // Attempt to set graphics for this menu item.
            EditorObject temporaryGraphicObject = ObjectCreator.create("_" + childToAdd, null);
            if (temporaryGraphicObject != null) addItemItem.setGraphic(new ImageView(getObjectIcon(temporaryGraphicObject)));

            // Set the item's action to creating the child, with the object as its parent.
            addItemItem.setOnAction(event -> ObjectAdder.addObject(childToAdd, object));

            menu.getItems().add(addItemItem);
        }

        return menu;

    }

}
