package com.woogleFX.engine.fx.propertiesView;

import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.assets.wog1.particle.WOG1Particle;
import com.woogleFX.assets.wog2.WOG2Item.WOG2Item;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.gui.BackgroundViewer;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.AttributeChangeAction;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.resrc.Image;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PropertiesViewManager {

    public static TreeTableRow<EditorAttribute[]> createRow() {

        final TreeTableRow<EditorAttribute[]> row = new TreeTableRow<>() {
            @Override
            protected void updateItem(EditorAttribute[] editorAttributes, boolean b) {
                super.updateItem(editorAttributes, b);
                if (editorAttributes == null || editorAttributes.length == 0 || editorAttributes[0].getDescription().isEmpty()) setTooltip(null);
                else setTooltip(new Tooltip(editorAttributes[0].getDescription()));
            }
        };

        // Manually change the row's font size to prevent clipping.
        row.setStyle("-fx-font-size: 11");

        row.hoverProperty().addListener((observable) -> {
            if (row.isHover() && row.getItem() != null) {
                // If the user is hovering over this row, select it.
                FXPropertiesView.getPropertiesView().getSelectionModel().select(row.getIndex());
            } else {
                // If the user is not hovering over this row, deselect all rows.
                // This works because hovering off a row is processed before hovering onto
                // another row.
                FXPropertiesView.getPropertiesView().getSelectionModel().clearSelection();
            }
        });

        row.pressedProperty().addListener((observable, oldValue, newValue) -> {
            // If we are editing a cell with null content or an uneditable cell, cancel the
            // edit.
            if (row.getTreeItem() == null || row.getTreeItem().getValue()[0].getType() == null) {
                Platform.runLater(() -> FXPropertiesView.getPropertiesView().edit(-1, null));
            }
        });

        row.setId("notDragTarget");

        return row;
    }

    public static boolean[] shouldRunOnClick = { true };

    public static TreeTableCell<EditorAttribute[], String> createValueCell() {

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
            TextFieldTreeTableCell<EditorAttribute[], String> cell = new TextFieldTreeTableCell<>(stringConverter) {
                private TextField textField;
                private ContextMenu contextMenu;

                @Override
                public void startEdit() {
                    super.startEdit();
                    if (textField == null) {
                        createTextField();
                    }
                    setText(null);
                    setGraphic(textField);
                    textField.setText(getItem());
                    textField.selectAll();
                    textField.requestFocus();
                    before = getItem();

                    if (getTableRow().getItem() != null) {
                        if (shouldRunOnClick[0]) getTableRow().getItem()[0].onClick();
                        if (getTableRow().getItem()[0].getName().equalsIgnoreCase("backgroundId")) {
                            new BackgroundViewer(GameVersion.VERSION_WOG2, this).start(new Stage());
                        }
                    }

                    Bounds bounds = localToScreen(getBoundsInLocal());
                    if (bounds == null) return;

                    double x = bounds.getMinX();
                    double y = bounds.getMinY() + 18;

                    if (contextMenu != null) contextMenu.hide();
                    contextMenu = possibleAttributeValues(this, getItem(), AssetManager.getAsset().getVersion());


                    if (contextMenu.getItems().isEmpty() || ((VBox) ((ScrollPane) contextMenu.getItems().get(0).getGraphic()).getContent()).getChildren().isEmpty())
                        return;

                    contextMenu.show(FXStage.getStage(), x, y);

                    contextMenu.requestFocus();

                    TextField textField = new TextField();
                    textField.setText(getItem());
                    textField.selectAll();
                }



                @Override
                public void cancelEdit() {
                    if (textField != null) {
                        String text = textField.getText();
                        if (getTableRow().getItem() == null) return;
                        InputField type = getTableRow().getItem()[0].getType();
                        EditorObject object = getTableRow().getItem()[0].getObject();
                        if (type.verify(object, text, getTableRow().getItem()[0].getRequired())) {
                            setItem(text);
                            getTableRow().getItem()[0].setValue(text);
                        }
                    }
                    super.cancelEdit();
                    setText(getItem());
                    setGraphic(null);
                    if (getContextMenu() != null) {
                        getContextMenu().hide();
                    }
                }



                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (isEditing()) {
                            if (textField != null) {
                                textField.setText(getItem());
                            }
                            setText(null);
                            setGraphic(textField);
                        } else {
                            setText(getItem());
                            setGraphic(null);


                            if (getTableRow().getItem() != null && getTableRow().getItem()[0].getType() != null) {

                                if (!getTableRow().getItem()[0].getType().verify(getTableRow().getItem()[0].getObject(), getItem(), getTableRow().getItem()[0].getRequired())) {
                                    setStyle("-fx-font-weight: normal;-fx-text-fill: #ff0000ff");
                                } else if (getTableRow().getItem()[0].getType().hasSpecialClickBehavior(getTableRow().getItem()[0].getObject(), getItem())) {
                                    setStyle("-fx-font-weight: bold;-fx-text-fill: #000000ff");
                                } else {
                                    setStyle("-fx-font-weight: normal;-fx-text-fill: #000000ff");
                                }
                            } else {
                                setStyle("-fx-font-weight: normal;-fx-text-fill: #000000ff");
                            }

                        }
                    }

                    // Override the default padding that ruins the text.
                    setPadding(new Insets(0, 0, 0, 2));


                    // default text!
                    if (getText() == null || getText().isEmpty()) {

                        setStyle("-fx-font-weight: normal;-fx-text-fill: #c0c0c0ff");

                        if (getTableRow().getItem() != null && getTableRow().getItem().length > 0) {
                            EditorAttribute editorAttribute = getTableRow().getItem()[0];
                            if (editorAttribute.getDefaultValue() != null) {
                                setText(editorAttribute.getDefaultValue());
                            }
                        }

                    }

                }

                private void createTextField() {
                    textField = new TextField(getItem());
                    textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
                    TextFieldTreeTableCell<EditorAttribute[], String> cell = this;
                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (contextMenu == null) return;
                        contextMenu.hide();
                        contextMenu = possibleAttributeValues(cell, newValue, AssetManager.getAsset().getVersion());

                        Bounds bounds = localToScreen(getBoundsInLocal());

                        if (bounds == null) return;

                        double x = bounds.getMinX();
                        double y = bounds.getMinY() + 18;

                        contextMenu.show(FXStage.getStage(), x, y);

                        contextMenu.requestFocus();
                    });
                    textField.setOnAction(event -> commitEdit(textField.getText()));
                    textField.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ESCAPE) {
                            // cancel the edit for real
                            textField = null;
                            cancelEdit();
                            updateItem(getItem(), isEmpty());
                        }
                    });
                }

                private String before;

                @Override
                public void commitEdit(String s) {
                    if (getTableRow().getItem() == null) return;
                    InputField type = getTableRow().getItem()[0].getType();
                    EditorObject object = getTableRow().getItem()[0].getObject();
                    super.commitEdit(type.verify(object, s, getTableRow().getItem()[0].getRequired()) ? s : before);
                }


            };

            cell.setOnMousePressed(e -> {
                if (e.isControlDown()) {
                    if (cell.getTableRow().getItem() != null && cell.getTableRow().getItem().length == 1) {
                        EditorAttribute attribute = cell.getTableRow().getItem()[0];
                        if (attribute.getType().hasSpecialClickBehavior(attribute.getObject(), attribute.stringValue())) {
                            attribute.getType().onDoubleClick(attribute.getObject(), attribute.stringValue());
                            return;
                        }
                    }
                }
                if (!cell.isEmpty()) cell.startEdit();
                e.consume();
            });

            return cell;
        }

    private static ContextMenu possibleAttributeValues(TextFieldTreeTableCell<EditorAttribute[], String> cell, String currentText, GameVersion version) {
        ContextMenu contextMenu = new ContextMenu();
        EditorAttribute attribute = cell.getTableRow().getItem()[0];
        if (attribute == null || attribute.getType() == null) {
            return contextMenu;
        }

        VBox vBox = new VBox();

        String[] possibleValues = attribute.getType().getPossibleValues(attribute);

        boolean isCurrentTextInAny = Arrays.stream(possibleValues).anyMatch(e -> e.contains(currentText));

        for (String childName : possibleValues) {
            if (isCurrentTextInAny && !childName.contains(currentText)) continue;
            Button setImageItem = new Button(childName);
            configureButton(setImageItem);
            if (attribute.getType() == InputField._1_IMAGE) {
                // Add thumbnail of the image to the menu item
                try {
                    ImageView graphic = new ImageView(ResourceManager.getImage(AssetManager.getAsset().getResources(), childName, version));
                    graphic.setFitHeight(18);
                    // Set width depending on height
                    graphic.setFitWidth(graphic.getImage().getWidth() * 18 / graphic.getImage().getHeight());
                    setImageItem.setGraphic(graphic);
                } catch (Exception ignored) {

                }
            }
            setImageItem.setOnAction(event -> {
                UndoManager.registerChange(new AttributeChangeAction(attribute,
                        attribute.stringValue(), childName));
                attribute.setValue(childName);
                if (contextMenu.isFocused()) {
                    cell.commitEdit(attribute.stringValue());
                }
            });
            vBox.getChildren().add(setImageItem);
        }

        /*

        switch (attribute.getType()) {

            case _2_BALL_TYPE, _2_BALL_TYPE_USERVAR -> {
                List<String> ballNames = WOG1Ball.assetSelector.getItems(version);
                for (String ballName : ballNames) {
                    if (!ballName.toLowerCase().contains(currentText.toLowerCase())) continue;
                    Button setImageItem = new Button(ballName);
                    configureButton(setImageItem);
                    setImageItem.setOnAction(event -> {
                        UndoManager.registerChange(new AttributeChangeAction(attribute,
                                attribute.stringValue(), ballName));
                        attribute.setValue(ballName);
                        if (contextMenu.isFocused()) {
                            cell.commitEdit(ballName);
                        }
                    });
                    vBox.getChildren().add(setImageItem);
                }
            }

            case _2_ITEM_TYPE -> {
                List<String> particleNames = WOG2Item.assetSelector.getItems(version);
                for (String itemName : particleNames) {
                    if (!itemName.toLowerCase().contains(currentText.toLowerCase())) continue;
                    Button setImageItem = new Button(itemName);
                    configureButton(setImageItem);
                    setImageItem.setOnAction(event -> {
                        UndoManager.registerChange(new AttributeChangeAction(attribute,
                                attribute.stringValue(), itemName));
                        attribute.getObject().setAttribute(attribute.getName(), itemName);
                        if (contextMenu.isFocused()) {
                            cell.commitEdit(attribute.stringValue());
                        }
                    });
                    vBox.getChildren().add(setImageItem);
                }
            }

            case _2_TERRAIN_GROUP_TYPE -> {

                // TODO2:

                /*
                for (String itemType : ItemHelper.terrainTypeNameMap.values()) {
                    if (!itemType.toLowerCase().contains(currentText.toLowerCase())) continue;
                    Button setImageItem;
                    if (terrainTypeElements.containsKey(itemType)) {
                        setImageItem = terrainTypeElements.get(itemType);

                        setImageItem.setOnAction(event -> {
                            UndoManager.registerChange(new AttributeChangeAction(attribute,
                                    attribute.stringValue(), itemType));
                            attribute.getObject().setAttribute(attribute.getName(), itemType);
                            if (contextMenu.isFocused()) {
                                cell.commitEdit(attribute.stringValue());
                                TerrainHelper.terrainColorCache.clear();
                            }
                        });

                    } else {
                        setImageItem = new Button(itemType);

                        configureButton(setImageItem);

                        setImageItem.setOnAction(event -> {
                            UndoManager.registerChange(new AttributeChangeAction(attribute,
                                    attribute.stringValue(), itemType));
                            attribute.getObject().setAttribute(attribute.getName(), itemType);
                            if (contextMenu.isFocused()) {
                                cell.commitEdit(attribute.stringValue());
                                TerrainHelper.terrainColorCache.clear();
                            }
                        });
                        ImageView imageView = TerrainHelper.terrainPreviewImage(itemType);
                        if (imageView != null && imageView.getImage() != null) {
                            setImageItem.setGraphic(imageView);
                            terrainTypeElements.put(itemType, setImageItem);
                        }
                    }
                    vBox.getChildren().add(setImageItem);
                }

                 */
/*
            }

            case _2_MUSIC_ID -> {
                Set<String> musicIds = GlobalResourceManager.getSequelMusic().keySet();

                for (String musicId : musicIds.stream().sorted().toArray(String[]::new)) {
                    if (!musicId.toLowerCase().contains(currentText.toLowerCase()))
                        continue;

                    Button setImageItem = new Button(musicId);
                    configureButton(setImageItem);

                    setImageItem.setOnAction(event -> {
                        UndoManager.registerChange(new AttributeChangeAction(attribute,
                                attribute.stringValue(), musicId));
                        attribute.getObject().setAttribute(attribute.getName(), musicId);
                        if (contextMenu.isFocused()) {
                            cell.commitEdit(attribute.stringValue());
                        }
                    });

                    vBox.getChildren().add(setImageItem);
                }
            }

            case _2_AMBIENCE_ID -> {
                Set<String> musicIds = GlobalResourceManager.getSequelAmbience().keySet();

                for (String musicId : musicIds.stream().sorted().toArray(String[]::new)) {
                    if (!musicId.toLowerCase().contains(currentText.toLowerCase()))
                        continue;

                    Button setImageItem = new Button(musicId);
                    configureButton(setImageItem);

                    setImageItem.setOnAction(event -> {
                        UndoManager.registerChange(new AttributeChangeAction(attribute,
                                attribute.stringValue(), musicId));
                        attribute.getObject().setAttribute(attribute.getName(), musicId);
                        if (contextMenu.isFocused()) {
                            cell.commitEdit(attribute.stringValue());
                        }
                    });

                    vBox.getChildren().add(setImageItem);
                }
            }

        }
 */

        vBox.setPrefWidth(300);
        vBox.setMaxHeight(100);

        ScrollPane pane = new ScrollPane(vBox);
        pane.setPadding(new Insets(0, -12, 0, 0));
        pane.setMaxWidth(300);
        pane.setPrefWidth(300);
        pane.setMaxHeight(300);

        MenuItem menuItem = new MenuItem();
        menuItem.setGraphic(pane);
        contextMenu.getItems().add(menuItem);

        menuItem.setId("contextMenu");

        pane.setId("contextMenu");

        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contextMenu.setId("contextMenu");

        return contextMenu;

    }




    private static void configureButton(Button button) {

        button.setMnemonicParsing(false);
        button.setMinWidth(300);
        button.setMaxWidth(300);
        button.setPrefWidth(300);
        button.setMinHeight(18);
        button.setMaxHeight(18);
        button.setPrefHeight(18);
        button.setStyle("-fx-padding: 0 0 0 2;");
        button.setId("contextMenu");
        button.setAlignment(Pos.CENTER_LEFT);

    }

}
