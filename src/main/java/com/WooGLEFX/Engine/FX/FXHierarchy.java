package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Functions.ObjectAdder;
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


    private static EditorObject moving;
    private static int oldDropIndex;


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
                    return param.getValue().getValue().getObjName().getValueProperty().concat(",")
                            .concat(param.getValue().getValue().getObjName2().getValueProperty());
                } else {
                    return param.getValue().getValue().getObjName().getValueProperty();
                }
            } else {
                return new EditorAttribute("AAAAA", InputField.ANY).getValueProperty();
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
                        try {
                            row.setContextMenu(contextMenuForEditorObject(row.getTreeItem().getValue()));
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
                    if (!row.isEmpty() && row.getTreeItem().getChildren().size() == 0) {
                        int dropIndex = row.getIndex();
                        int curIndex = oldDropIndex;
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

        hierarchySwitcherButtons();

    }

    private static TabPane hierarchySwitcherButtons;
    public static TabPane getHierarchySwitcherButtons() {
        return hierarchySwitcherButtons;
    }


    public static void hierarchySwitcherButtons() {

        // Create and customize the parent container.
        hierarchySwitcherButtons = new TabPane();

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
                    FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getScene().get(0).getTreeItem());
                    FXHierarchy.getHierarchy().refresh();
                    FXHierarchy.getHierarchy().getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Scene");
                    FXHierarchy.getHierarchy().setShowRoot(true);
                } else if (t1 == levelSelectButton) {
                    FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getLevel().get(0).getTreeItem());
                    FXHierarchy.getHierarchy().refresh();
                    FXHierarchy.getHierarchy().getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Level");
                    FXHierarchy.getHierarchy().setShowRoot(true);
                } else if (t1 == resrcSelectButton) {
                    FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getResourcesObject().getChildren().get(0).getTreeItem());
                    FXHierarchy.getHierarchy().refresh();
                    FXHierarchy.getHierarchy().getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Resrc");
                    FXHierarchy.getHierarchy().setShowRoot(true);
                } else if (t1 == textSelectButton) {
                    FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getTextObject().getTreeItem());
                    FXHierarchy.getHierarchy().refresh();
                    FXHierarchy.getHierarchy().getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Text");
                    FXHierarchy.getHierarchy().setShowRoot(true);
                } else if (t1 == addinSelectButton) {
                    FXHierarchy.getHierarchy().setRoot(LevelManager.getLevel().getAddinObject().getTreeItem());
                    FXHierarchy.getHierarchy().refresh();
                    FXHierarchy.getHierarchy().getRoot().setExpanded(true);
                    LevelManager.getLevel().setCurrentlySelectedSection("Addin");
                    FXHierarchy.getHierarchy().setShowRoot(true);
                }
            }
        });

    }

    public static ContextMenu contextMenuForEditorObject(EditorObject object) throws FileNotFoundException {

        // Create the content menu.
        ContextMenu menu = new ContextMenu();

        // For every object that can be created as a child of this object:
        for (String childToAdd : object.getPossibleChildren()) {

            // Create a menu item representing creating this child.
            MenuItem addItemItem = new MenuItem("Add " + childToAdd);

            // Attempt to set graphics for this menu item.
            addItemItem.setGraphic(new ImageView(FileManager.getObjectIcon(childToAdd)));

            // Set the item's action to creating the child, with the object as its parent.
            addItemItem.setOnAction(event -> {
                switch (childToAdd) {
                    case "BallInstance" -> ObjectAdder.addBall(object, "");
                    case "Strand" -> ObjectAdder.addStrand(object);
                    case "camera" -> ObjectAdder.addCamera(object);
                    case "poi" -> ObjectAdder.addPoi(object);
                    case "music" -> ObjectAdder.addMusic(object);
                    case "loopsound" -> ObjectAdder.addLoopsound(object);
                    case "linearforcefield" -> ObjectAdder.addLinearForcefield(object);
                    case "radialforcefield" -> ObjectAdder.addRadialForcefield(object);
                    case "particles" -> ObjectAdder.addParticle(object);
                    case "SceneLayer" -> ObjectAdder.addSceneLayer(object);
                    case "buttongroup" -> ObjectAdder.addButtongroup(object);
                    case "button" -> ObjectAdder.addButton(object);
                    case "circle" -> ObjectAdder.addCircle(object);
                    case "rectangle" -> ObjectAdder.addRectangle(object);
                    case "hinge" -> ObjectAdder.addHinge(object);
                    case "compositegeom" -> ObjectAdder.addCompositegeom(object);
                    case "label" -> ObjectAdder.addLabel(object);
                    case "line" -> ObjectAdder.addLine(object);
                    case "motor" -> ObjectAdder.addMotor(object);
                    case "slider" -> ObjectAdder.addSlider(object);
                    case "endoncollision" -> ObjectAdder.addEndoncollision(object);
                    case "endonnogeom" -> ObjectAdder.addEndonnogeom(object);
                    case "endonmessage" -> ObjectAdder.addEndonmessage(object);
                    case "targetheight" -> ObjectAdder.addTargetheight(object);
                    case "fire" -> ObjectAdder.addFire(object);
                    case "levelexit" -> ObjectAdder.addLevelexit(object);
                    case "pipe" -> ObjectAdder.addPipe(object);
                    case "signpost" -> ObjectAdder.addSign(object);
                    case "textstring" -> ObjectAdder.addString(object);
                    case "resrcimage" -> ObjectAdder.addResrcImage(object);
                    case "sound" -> ObjectAdder.addSound(object);
                    case "setdefaults" -> ObjectAdder.addSetDefaults(object);
                    case "Vertex" -> ObjectAdder.addPipeVertex(object);
                    default -> throw new RuntimeException("Unknown child type: " + childToAdd);
                }
            });

            menu.getItems().add(addItemItem);
        }

        return menu;
    }


}