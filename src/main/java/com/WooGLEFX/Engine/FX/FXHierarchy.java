package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.EditorObjects.ObjectCreators.ObjectCreator;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.EditorObjects.ObjectCreators.ObjectAdder;
import com.WooGLEFX.Functions.UndoHandling.UndoManager;
import com.WooGLEFX.EditorObjects.EditorAttribute;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Functions.UndoHandling.UserActions.HierarchyDragAction;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.io.FileNotFoundException;

public class FXHierarchy {

    private static final TreeTableView<EditorObject> hierarchy = new TreeTableView<>();
    public static TreeTableView<EditorObject> getHierarchy() {
        return hierarchy;
    }


    private static EditorObject moving;
    private static int oldDropIndex;


    private static Image getObjectIcon(EditorObject editorObject) {
        try {
            return FileManager.getIcon("ObjectIcons\\" + editorObject.getIcon() + ".png");
        } catch (FileNotFoundException e) {
            return null;
        }
    }


    public static void init() {

        hierarchy.setPlaceholder(new Label());

        // Create the columns the hierarchy uses ("Element" and its "ID or Name")
        TreeTableColumn<EditorObject, String> hierarchyElements = new TreeTableColumn<>();
        hierarchyElements.setGraphic(new Label("Elements"));
        hierarchyElements.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        hierarchy.getColumns().add(hierarchyElements);
        hierarchyElements.setPrefWidth(200);

        TreeTableColumn<EditorObject, String> hierarchyNames = new TreeTableColumn<>();
        hierarchyNames.setGraphic(new Label("ID or Name"));

        hierarchyNames.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));

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

                        imageView = new ImageView(getObjectIcon(getTableRow().getItem()));

                        // If the cell's EditorObject is invalid, display its graphic with a warning
                        // symbol.
                        // Otherwise, just display its graphic.

                        boolean valid = true;

                        EditorObject editorObject = getTableRow().getItem();

                        for (EditorAttribute attribute : editorObject.getAttributes()) {
                            if (attribute.stringValue().isEmpty()) {
                                if (!InputField.verify(editorObject, attribute.getType(), attribute.getDefaultValue()) && attribute.getRequiredInFile()) return;
                            } else if (!InputField.verify(editorObject, attribute.getType(), attribute.actualValue())) {
                                valid = false;
                            }
                        }

                        if (!valid) {
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
                SelectionManager.setSelected(newValue.getValue());
                FXPropertiesView.changeTableView(newValue.getValue());
            }
        });

        // Make the rows small.
        hierarchy.setFixedCellSize(18);

        hierarchy.setRowFactory(treeTableView -> {
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
                if (selected2 != null) {
                    Dragboard db = hierarchy.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selected2.getValue().getClass().getName());
                    db.setContent(content);
                    moving = row.getItem();
                    oldDropIndex = row.getIndex();
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
                    if (!row.isEmpty() && row.getTreeItem().getChildren().isEmpty()) {
                        int dropIndex = row.getIndex();
                        int curIndex = oldDropIndex;
                        if (dropIndex > curIndex) {
                            // Dragged the item downwards; shift all the items up
                            while (curIndex < dropIndex) {
                                hierarchy.getTreeItem(curIndex).setValue(hierarchy.getTreeItem(curIndex + 1).getValue());
                                curIndex++;
                            }
                        } else {
                            // Dragged the item upwards; shift all the items down
                            while (curIndex > dropIndex) {
                                hierarchy.getTreeItem(curIndex).setValue(hierarchy.getTreeItem(curIndex - 1).getValue());
                                curIndex--;
                            }
                        }
                        hierarchy.getTreeItem(dropIndex).setValue(moving);
                        hierarchy.getSelectionModel().select(dropIndex);

                        success = true;

                        UndoManager.registerChange(new HierarchyDragAction(moving, oldDropIndex, dropIndex));
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

        hierarchy.setPrefHeight(FXStage.getStage().getHeight() * 0.4);

        hierarchySwitcherButtons();

    }

    private static final TabPane hierarchySwitcherButtons = new TabPane();
    public static TabPane getHierarchySwitcherButtons() {
        return hierarchySwitcherButtons;
    }


    public static void hierarchySwitcherButtons() {

        // Create the three buttons.
        Tab sceneSelectButton = new Tab("Scene");
        Tab levelSelectButton = new Tab("Level");
        Tab resrcSelectButton = new Tab("Resrc");
        Tab textSelectButton = new Tab("Text");
        Tab addinSelectButton = new Tab("Addin");

        hierarchySwitcherButtons.getTabs().addAll(sceneSelectButton, levelSelectButton, resrcSelectButton, textSelectButton,
                addinSelectButton);
        hierarchySwitcherButtons.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        hierarchySwitcherButtons.setMinHeight(30);
        hierarchySwitcherButtons.setMaxHeight(30);
        hierarchySwitcherButtons.setPrefHeight(30);
        hierarchySwitcherButtons.setPadding(new Insets(-6, -6, -6, -6));

        hierarchySwitcherButtons.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            if (LevelManager.getLevel() != null) {
                if (t1 == sceneSelectButton) {
                    hierarchy.setRoot(LevelManager.getLevel().getScene().get(0).getTreeItem());
                    hierarchy.refresh();
                    hierarchy.getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Scene");
                    hierarchy.setShowRoot(true);
                } else if (t1 == levelSelectButton) {
                    hierarchy.setRoot(LevelManager.getLevel().getLevel().get(0).getTreeItem());
                    hierarchy.refresh();
                    hierarchy.getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Level");
                    hierarchy.setShowRoot(true);
                } else if (t1 == resrcSelectButton) {
                    hierarchy.setRoot(LevelManager.getLevel().getResourcesObject().getChildren().get(0).getTreeItem());
                    hierarchy.refresh();
                    hierarchy.getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Resrc");
                    hierarchy.setShowRoot(true);
                } else if (t1 == textSelectButton) {
                    hierarchy.setRoot(LevelManager.getLevel().getTextObject().getTreeItem());
                    hierarchy.refresh();
                    hierarchy.getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Text");
                    hierarchy.setShowRoot(true);
                } else if (t1 == addinSelectButton) {
                    hierarchy.setRoot(LevelManager.getLevel().getAddinObject().getTreeItem());
                    hierarchy.refresh();
                    hierarchy.getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Addin");
                    hierarchy.setShowRoot(true);
                }
            }
        });

        hierarchy.sort();

    }

    public static ContextMenu contextMenuForEditorObject(EditorObject object) {

        // Create the content menu.
        ContextMenu menu = new ContextMenu();

        // For every object that can be created as a child of this object:
        for (String childToAdd : object.getPossibleChildren()) {

            // Create a menu item representing creating this child.
            MenuItem addItemItem = new MenuItem("Add " + childToAdd);

            // Attempt to set graphics for this menu item.
            addItemItem.setGraphic(new ImageView(getObjectIcon(ObjectCreator.create(childToAdd, null))));

            // Set the item's action to creating the child, with the object as its parent.
            addItemItem.setOnAction(event -> ObjectAdder.addObject(childToAdd));

            menu.getItems().add(addItemItem);
        }

        return menu;
    }


}
