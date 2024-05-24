package com.woogleFX.functions;

import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.file.FileManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.functions.undoHandling.userActions.HierarchyDragAction;
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
                } else {
                    cell.setGraphic(imageView);
                }
            }
        }

    }


    public static boolean handleDragDrop(TreeTableView<EditorObject> hierarchy, TreeTableRow<EditorObject> row) {

        if (row.isEmpty()) return false;

        int fromIndex = oldDropIndex;
        int toIndex = row.getIndex();

        int direction = (int)Math.signum(toIndex - fromIndex);
        if (direction == 0) return false;

        if (direction > 0) toIndex -= 1;

        EditorObject toItem = hierarchy.getTreeItem(toIndex).getValue();
        EditorObject fromItem = hierarchy.getTreeItem(fromIndex).getValue();

        // YOU CAN'T PUT AN OBJECT INSIDE ITSELF
        if (toItem.getChildren().contains(fromItem)) return false;

        // Or inside an object that doesn't have it as a possible child
        if (Stream.of(toItem.getParent().getPossibleChildren()).noneMatch(e -> e.equals(fromItem.getType()))) return false;

        // Add the dragged item just above the item that it gets dragged to
        int indexOfToItem = toItem.getParent().getChildren().indexOf(toItem);

        fromItem.getParent().getChildren().remove(fromItem);
        fromItem.getParent().getTreeItem().getChildren().remove(fromItem.getTreeItem());

        fromItem.setParent(toItem.getParent(), indexOfToItem);

        hierarchy.getSelectionModel().select(toIndex);

        UndoManager.registerChange(new HierarchyDragAction(fromItem, HierarchyManager.getOldDropIndex(), toIndex));
        UndoManager.clearRedoActions();

        return true;

    }


    public static TreeTableRow<EditorObject> createRow(TreeTableView<EditorObject> hierarchy) {

        final TreeTableRow<EditorObject> row = new TreeTableRow<>();

        row.setOnMousePressed(event -> {
            if (hierarchy.getTreeItem(row.getIndex()) != null) {
                SelectionManager.setSelected(hierarchy.getTreeItem(row.getIndex()).getValue());
                FXPropertiesView.changeTableView(hierarchy.getTreeItem(row.getIndex()).getValue());
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    row.setContextMenu(contextMenuForEditorObject(row.getTreeItem().getValue()));
                }
            }
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

        });

        row.setOnDragExited(event -> {
            // row.setStyle("");
            row.setStyle("-fx-border-width: 0 0 0 0;");
            row.setPadding(new Insets(1, 0, 0, 0));
        });

        row.setOnDragOver(event -> {
            if (event.getDragboard().hasString() && row.getTreeItem() != null) {
                event.acceptTransferModes(TransferMode.MOVE);
                row.setStyle("-fx-border-color: #0000ff; -fx-border-width: 2 0 0 0;");
                row.setPadding(new Insets(-1, 0, 0, 0));
            }
            event.consume();
        });

        row.setOnDragDropped(event -> {
            event.setDropCompleted(event.getDragboard().hasString() &&
                    HierarchyManager.handleDragDrop(hierarchy, row));
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
