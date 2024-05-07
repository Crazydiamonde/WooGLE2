package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Functions.UndoManager;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.UserActions.HierarchyDragAction;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.io.FileNotFoundException;

public class FXHierarchy {

    private static TreeTableView<EditorObject> hierarchy;
    public static TreeTableView<EditorObject> getHierarchy() {
        return hierarchy;
    }


    public static void init() {

        // Create the TreeTableView.
        hierarchy = new TreeTableView<>();

        hierarchy.setPlaceholder(new Label());

        // Create the columns the hierarchy uses ("Element" and its "ID or Name")
        TreeTableColumn<EditorObject, String> hierarchyElements = new TreeTableColumn<>();
        hierarchyElements.setGraphic(new Label("Elements"));
        hierarchyElements.setCellValueFactory(new TreeItemPropertyValueFactory<>("realName"));
        hierarchy.getColumns().add(hierarchyElements);
        hierarchyElements.setPrefWidth(200);

        TreeTableColumn<EditorObject, String> hierarchyNames = new TreeTableColumn<>();
        hierarchyNames.setGraphic(new Label("ID or Name"));

        hierarchyNames.setCellValueFactory(param -> {
            if (param.getValue().getValue().getObjName() != null) {
                if (param.getValue().getValue().getObjName2() != null
                        && !param.getValue().getValue().getObjName2().getValue().equals("")) {
                    return param.getValue().getValue().getObjName().valueProperty().concat(",")
                            .concat(param.getValue().getValue().getObjName2().valueProperty());
                } else {
                    return param.getValue().getValue().getObjName().valueProperty();
                }
            } else {
                return new EditorAttribute(param.getValue().getValue(), "AAAAA", "AAAAA", "AAAAA",
                        new InputField("AAAAA", InputField.ANY), false).valueProperty();
            }
        });

        hierarchy.getColumns().add(hierarchyNames);

        hierarchyElements.setCellFactory(column -> new TreeTableCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // If this is an empty cell, set its text and graphic to empty.
                    // This prevents the cell from retaining other cells' information.
                    setText(null);
                    setGraphic(null);
                } else {
                    // Update this cell's text.
                    setText(item);
                    // Override the default padding that ruins the text.
                    setPadding(new Insets(0, 0, 0, 0));

                    if (getTableRow().getItem() != null) {
                        ImageView imageView;

                        try {
                            imageView = new ImageView(
                                    FileManager.getObjectIcon(getTableRow().getItem().getClass().getSimpleName()));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        // If the cell's EditorObject is invalid, display its graphic with a warning
                        // symbol.
                        // Otherwise, just display its graphic.
                        if (!getTableRow().getItem().isValid()) {
                            ImageView failedImg = new ImageView(FileManager.getFailedImage());
                            setGraphic(new StackPane(imageView, failedImg));
                        } else {
                            setGraphic(imageView);
                        }
                    }
                }
            }
        });

        hierarchyNames.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // If this is an empty cell, set its text and graphic to empty.
                    // This prevents the cell from retaining other cells' information.
                    setText(null);
                    setGraphic(null);
                } else {
                    // setTextFill(Paint.valueOf("FFFFFFFF"));
                    // Update this cell's text.
                    setText(item);
                    // Override the default padding that ruins the text.
                    setPadding(new Insets(0, 0, 0, 0));
                }
            }
        });

        // If a cell is clicked from the hierarchy, update the selected object and
        // properties view.
        hierarchy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Main.setSelected(newValue.getValue());
                Main.changeTableView(newValue.getValue());
            }
        });

        // Make the rows small.
        hierarchy.setFixedCellSize(18);

        hierarchy.setRowFactory(treeTableView -> {
            final TreeTableRow<EditorObject> row = new TreeTableRow<>();

            row.setOnMousePressed(event -> {
                if (hierarchy.getTreeItem(row.getIndex()) != null) {
                    Main.setSelected(hierarchy.getTreeItem(row.getIndex()).getValue());
                    Main.changeTableView(hierarchy.getTreeItem(row.getIndex()).getValue());
                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        try {
                            row.setContextMenu(FXCreator.contextMenuForEditorObject(row.getTreeItem().getValue()));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

            row.setOnDragDetected(event -> {
                TreeItem<EditorObject> selected2 = hierarchy.getSelectionModel().getSelectedItem();
                if (selected2 != null) {
                    Dragboard db = hierarchy.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selected2.getValue().getClass().getName());
                    db.setContent(content);
                    Main.setMoving(row.getItem());
                    Main.setOldDropIndex(row.getIndex());
                    event.consume();
                }
            });

            row.setOnDragExited(event -> {
                // row.setStyle("");
            });

            row.setOnDragOver(event -> {
                if (event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    // row.setStyle("-fx-font-size: 12pt, -fx-background-color: #D0F0FFFF");
                }
                event.consume();
            });

            row.setOnDragDropped(event -> {
                boolean success = false;
                if (event.getDragboard().hasString()) {
                    // Don't allow drag-and-drop onto element with children
                    // TODO: This doesn't work and also you should be able to do so
                    if (!row.isEmpty() && row.getTreeItem().getChildren().size() == 0) {
                        int dropIndex = row.getIndex();
                        int curIndex = Main.getOldDropIndex();
                        if (dropIndex > curIndex) {
                            // Dragged the item downwards; shift all of the items up
                            while (curIndex < dropIndex) {
                                hierarchy.getTreeItem(curIndex).setValue(hierarchy.getTreeItem(curIndex + 1).getValue());
                                curIndex++;
                            }
                        } else {
                            // Dragged the item upwards; shift all of the items down
                            while (curIndex > dropIndex) {
                                hierarchy.getTreeItem(curIndex).setValue(hierarchy.getTreeItem(curIndex - 1).getValue());
                                curIndex--;
                            }
                        }
                        hierarchy.getTreeItem(dropIndex).setValue(Main.getMoving());
                        hierarchy.getSelectionModel().select(dropIndex);

                        success = true;

                        UndoManager.registerChange(new HierarchyDragAction(Main.getMoving(), Main.getOldDropIndex(), dropIndex));
                        UndoManager.clearRedoActions();
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            return row;
        });

        hierarchy.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);

        hierarchyNames.prefWidthProperty().bind(hierarchy.widthProperty().subtract(hierarchyElements.widthProperty()));

        // hierarchy.getStyleClass().add("column-header");

    }


}
