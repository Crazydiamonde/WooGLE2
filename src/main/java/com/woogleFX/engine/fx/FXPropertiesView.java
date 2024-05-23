package com.woogleFX.engine.fx;

import com.woogleFX.engine.SelectionManager;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.functions.LevelManager;
import com.woogleFX.file.resourceManagers.ParticleManager;
import com.woogleFX.functions.undoHandling.UndoManager;
import com.woogleFX.editorObjects.EditorAttribute;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.simpleStructures.MetaEditorAttribute;
import com.woogleFX.functions.undoHandling.userActions.AttributeChangeAction;
import com.worldOfGoo.resrc.ResrcImage;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.File;

public class FXPropertiesView {

    private static final TreeTableView<EditorAttribute> propertiesView = new TreeTableView<>();
    public static TreeTableView<EditorAttribute> getPropertiesView() {
        return propertiesView;
    }


    public static void init() {

        propertiesView.setPlaceholder(new Label());

        // Create the columns the properties view uses (Attribute name and attribute
        // value).
        TreeTableColumn<EditorAttribute, String> name = new TreeTableColumn<>();
        name.setGraphic(new Label("Name"));
        name.setCellValueFactory(param -> param.getValue().getValue().getNameProperty());
        propertiesView.getColumns().add(name);

        TreeTableColumn<EditorAttribute, String> value = new TreeTableColumn<>();
        value.setGraphic(new Label("Value"));
        value.setCellValueFactory(param -> param.getValue().getValue().getValueProperty());
        propertiesView.getColumns().add(value);

        // Limit the width of the "names" column.
        name.setPrefWidth(200);

        // Hide the empty "root" attribute. This allows all of its children to be
        // displayed at once at the top of the TreeTableView.
        propertiesView.setShowRoot(false);

        // For each row of the properties view:
        propertiesView.setRowFactory(tableView -> {
            final TreeTableRow<EditorAttribute> row = new TreeTableRow<>();

            // Manually change the row's font size to prevent clipping.
            row.setStyle("-fx-font-size: 11");

            row.hoverProperty().addListener((observable) -> {
                if (row.isHover() && row.getItem() != null) {
                    // If the user is hovering over this row, select it.
                    propertiesView.getSelectionModel().select(row.getIndex());
                } else {
                    // If the user is not hovering over this row, deselect all rows.
                    // This works because hovering off a row is processed before hovering onto
                    // another row.
                    propertiesView.getSelectionModel().clearSelection();
                }
            });

            row.pressedProperty().addListener((observable, oldValue, newValue) -> {
                // If we are editing a cell with null content or an uneditable cell, cancel the
                // edit.
                if (row.getTreeItem() == null || row.getTreeItem().getValue().getType() == InputField.NULL) {
                    Platform.runLater(() -> propertiesView.edit(-1, null));
                }
            });

            return row;
        });

        name.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // If this is an empty cell, set its text and graphic to empty.
                    // This prevents the cell from retaining other cells' information.
                    setText(null);
                } else {

                    if (getTableRow().getTreeItem() != null) {
                        EditorAttribute editorAttribute = getTableRow().getTreeItem().getValue();
                        if (InputField.verify(SelectionManager.getSelected(), editorAttribute.getType(), editorAttribute.stringValue())) {
                            setStyle("-fx-text-fill: #000000ff");
                        } else {
                            setStyle("-fx-text-fill: #ff0000ff");
                        }
                    }

                    // Update this cell's text.
                    setText(item);
                    // Override the default padding that ruins the text.
                    setPadding(new Insets(0, 0, 0, 0));
                }
            }
        });

        value.setCellFactory(new Callback<>() {
            @Override
            public TreeTableCell<EditorAttribute, String> call(
                    TreeTableColumn<EditorAttribute, String> editorAttributeStringTreeTableColumn) {
                StringConverter<String> stringConverter = new StringConverter<>() {
                    @Override
                    public String toString(String s) {
                        return s;
                    }

                    @Override
                    public String fromString(String s) {
                        return s;
                    }
                };
                TextFieldTreeTableCell<EditorAttribute, String> cell = new TextFieldTreeTableCell<>(stringConverter) {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        // Override the default padding that ruins the text.
                        setPadding(new Insets(0, 0, 0, 2));
                    }

                    private String before;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        before = getItem();

                        Bounds bounds = localToScreen(getBoundsInLocal());

                        if (bounds != null) {

                            double x = bounds.getMinX();
                            double y = bounds.getMinY() + 18;

                            possibleAttributeValues(this).show(FXStage.getStage(), x, y);
                        }
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        if (getContextMenu() != null) {
                            getContextMenu().hide();
                        }
                    }

                    @Override
                    public void commitEdit(String s) {
                        int type = getTableRow().getItem().getType();
                        super.commitEdit(InputField.verify(SelectionManager.getSelected(), type, s) ? s : before);
                    }
                };

                cell.setOnMousePressed(event -> {

                    cell.setContextMenu(possibleAttributeValues(cell));
                    cell.getContextMenu().show(propertiesView, FXStage.getStage().getX()
                                    + FXContainers.getSplitPane().getDividers().get(0).getPosition() * FXContainers.getSplitPane().getWidth(),
                            100);
                    propertiesView.getSelectionModel().selectedItemProperty()
                            .addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    cell.getContextMenu().hide();
                                }
                            });

                    if (cell.getContextMenu() != null) {
                        cell.getContextMenu().show(propertiesView,
                                FXStage.getStage().getX() + FXContainers.getSplitPane().getDividers().get(0).getPosition()
                                        * FXContainers.getSplitPane().getWidth(),
                                100);
                    }
                    if (!cell.isSelected() && cell.getContextMenu() != null) {
                        cell.getContextMenu().hide();
                    }
                });

                return cell;
            }
        });

        // value.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        value.setOnEditCommit(e -> {
            // When editing a cell:

            // Change the actual attribute to reflect the edit.
            EditorAttribute attribute = propertiesView.getTreeItem(e.getTreeTablePosition().getRow()).getValue();
            String oldValue = attribute.stringValue();
            if (InputField.verify(SelectionManager.getSelected(), attribute.getType(), e.getNewValue())) {
                attribute.setValue(e.getNewValue());
            }

            // If the edit was actually valid:
            if (e.getNewValue().isEmpty() || InputField.verify(SelectionManager.getSelected(), attribute.getType(), e.getNewValue())) {

                // Push an attribute change to the undo buffer.
                UndoManager.registerChange(new AttributeChangeAction(SelectionManager.getSelected(), attribute.getName(), oldValue,
                        attribute.stringValue()));
                UndoManager.clearRedoActions();

                // If we have edited the name or ID of the object, change the object's "Name or
                // ID" value.

            } else {
                // Reset the attribute.
                attribute.setValue(oldValue);
                // If the user entered an invalid value, refresh to clear the edit.
                propertiesView.refresh();
            }

            FXHierarchy.getHierarchy().refresh();

        });

        // Adjust the row height to fit more attributes on screen at once.
        // This currently breaks the text.
        propertiesView.setFixedCellSize(18);

        // Set the "value" column and the entire TreeTableView to be editable.
        value.setEditable(true);
        propertiesView.setEditable(true);

        // Set the "value" column to extend to the very edge of the TreeTableView.
        value.prefWidthProperty().bind(propertiesView.widthProperty().subtract(name.widthProperty()));

        propertiesView.prefWidthProperty().bind(FXHierarchy.getHierarchy().widthProperty());
        propertiesView.setRoot(new TreeItem<>(EditorAttribute.NULL));

    }


    public static TreeItem<EditorAttribute> makePropertiesViewTreeItem(EditorObject object) {

        // Create the root tree item.
        TreeItem<EditorAttribute> treeItem = new TreeItem<>(EditorAttribute.NULL);

        // Loop over the object's meta attributes.
        for (MetaEditorAttribute metaEditorAttribute : object.getMetaAttributes()) {

            // Find the object's EditorAttribute associated with this meta attribute
            // (sharing the same name).
            EditorAttribute attribute;
            if (object.attributeExists(metaEditorAttribute.getName())) {
                attribute = object.getAttribute(metaEditorAttribute.getName());
            } else {
                // If no such attribute exists, this attribute is instead the name of a category
                // of attributes.
                // In this case, create a dummy attribute with no value.
                attribute = new EditorAttribute(metaEditorAttribute.getName(), InputField.NULL);
            }
            TreeItem<EditorAttribute> thisAttribute = new TreeItem<>(attribute);

            // If this attribute is set to be open by default, set its tree item to open.
            if (metaEditorAttribute.getOpenByDefault()) {
                thisAttribute.setExpanded(true);
            }

            // If this attribute represents a category of attributes, it will have children.
            // Add the children's TreeItems as children of the category's TreeItem.
            for (MetaEditorAttribute childAttribute : metaEditorAttribute.getChildren()) {
                thisAttribute.getChildren().add(new TreeItem<>(object.getAttribute(childAttribute.getName())));
            }

            // Add the attribute's TreeItem as a child of the root's TreeItem.
            treeItem.getChildren().add(thisAttribute);
        }

        return treeItem;
    }

    public static ContextMenu possibleAttributeValues(TextFieldTreeTableCell<EditorAttribute, String> cell) {
        ContextMenu contextMenu = new ContextMenu();
        EditorAttribute attribute = cell.getTableRow().getItem();
        if (attribute == null) {
            return contextMenu;
        }

        switch (attribute.getType()) {
            case InputField.IMAGE, InputField.IMAGE_REQUIRED -> {
                for (EditorObject resource : LevelManager.getLevel().getResources()) {
                    if (resource instanceof ResrcImage) {
                        MenuItem setImageItem = new MenuItem();

                        Label label = new Label(resource.getAttribute("id").stringValue());
                        label.setMaxHeight(17);
                        label.setMinHeight(17);
                        label.setPrefHeight(17);
                        label.setStyle("-fx-font-size: 11");
                        label.setPadding(new Insets(0, 0, 0, 0));

                        // Add thumbnail of the image to the menu item
                        try {
                            ImageView graphic = new ImageView(GlobalResourceManager.getImage(resource.getAttribute("id").stringValue(), LevelManager.getVersion()));
                            graphic.setFitHeight(17);
                            // Set width depending on height
                            graphic.setFitWidth(graphic.getImage().getWidth() * 17 / graphic.getImage().getHeight());
                            label.setGraphic(graphic);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        setImageItem.setGraphic(label);

                        setImageItem.setOnAction(event -> {
                            UndoManager.registerChange(new AttributeChangeAction(SelectionManager.getSelected(), attribute.getName(),
                                    attribute.stringValue(), resource.getAttribute("id").stringValue()));
                            UndoManager.clearRedoActions();
                            attribute.setValue(resource.getAttribute("id").stringValue());
                            if (contextMenu.isFocused()) {
                                cell.commitEdit(attribute.stringValue());
                            }
                        });

                        contextMenu.getItems().add(setImageItem);
                    }
                }
            }
            case InputField.BALL -> {
                String path = LevelManager.getVersion() == GameVersion.NEW ? FileManager.getNewWOGdir()
                        : FileManager.getOldWOGdir();
                File[] ballFiles = new File(path + "\\res\\balls").listFiles();
                if (ballFiles != null) {
                    for (File ballFile : ballFiles) {
                        MenuItem setImageItem = new MenuItem(ballFile.getName());

                        setImageItem.setOnAction(event -> {
                            UndoManager.registerChange(new AttributeChangeAction(SelectionManager.getSelected(), attribute.getName(),
                                    attribute.stringValue(), ballFile.getName()));
                            UndoManager.clearRedoActions();
                            attribute.setValue(ballFile.getName());
                            if (contextMenu.isFocused()) {
                                cell.commitEdit(attribute.stringValue());
                            }
                        });

                        contextMenu.getItems().add(setImageItem);
                    }
                }
            }
            case InputField.PARTICLES -> {
                for (String particleType : ParticleManager.getSortedParticleNames()) {
                    MenuItem setImageItem = new MenuItem(particleType);

                    setImageItem.setOnAction(event -> {
                        UndoManager.registerChange(new AttributeChangeAction(SelectionManager.getSelected(), attribute.getName(),
                                attribute.stringValue(), particleType));
                        UndoManager.clearRedoActions();
                        attribute.setValue(particleType);
                        if (contextMenu.isFocused()) {
                            cell.commitEdit(attribute.stringValue());
                        }
                    });

                    contextMenu.getItems().add(setImageItem);
                }
            }
        }

        return contextMenu;
    }


    public static void changeTableView(EditorObject obj) {
        if (obj == null) {
            FXPropertiesView.getPropertiesView().setRoot(null);
        } else {
            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(obj));
        }
    }


}