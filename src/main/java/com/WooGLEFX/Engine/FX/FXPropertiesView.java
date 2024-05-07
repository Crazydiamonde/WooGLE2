package com.WooGLEFX.Engine.FX;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Functions.UndoManager;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.UserActions.AttributeChangeAction;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FXPropertiesView {

    private static TreeTableView<EditorAttribute> propertiesView;
    public static TreeTableView<EditorAttribute> getPropertiesView() {
        return propertiesView;
    }


    public static void init() {
        
        // Create the properties view.
        propertiesView = new TreeTableView<>();

        propertiesView.setPlaceholder(new Label());

        // Create the columns the properties view uses (Attribute name and attribute
        // value).
        TreeTableColumn<EditorAttribute, String> name = new TreeTableColumn<>();
        name.setGraphic(new Label("Name"));
        name.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        propertiesView.getColumns().add(name);

        TreeTableColumn<EditorAttribute, String> value = new TreeTableColumn<>();
        value.setGraphic(new Label("Value"));
        value.setCellValueFactory(param -> param.getValue().getValue().valueProperty());
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
                if (row.getTreeItem() == null || row.getTreeItem().getValue().getInput().getType() == InputField.NULL) {
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

                        if (!empty && getTableRow().getTreeItem() != null) {
                            EditorAttribute editorAttribute = getTableRow().getTreeItem().getValue();
                            if (editorAttribute.getInput().verify(editorAttribute.getObject(), item)) {
                                setStyle("-fx-text-fill: #000000ff");
                            } else {
                                setStyle("-fx-text-fill: #ff0000ff");
                            }
                        }

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

                            FXCreator.possibleAttributeValues(this).show(Main.getStage(), x, y);
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
                        super.commitEdit(
                                getTableRow().getItem().getInput().verify(getTableRow().getItem().getObject(), s) ? s
                                        : before);
                    }
                };

                cell.setOnMousePressed(event -> {

                    cell.setContextMenu(FXCreator.possibleAttributeValues(cell));
                    cell.getContextMenu().show(propertiesView, Main.getStage().getX()
                                    + Main.getSplitPane().getDividers().get(0).getPosition() * Main.getSplitPane().getWidth(),
                            100);
                    propertiesView.getSelectionModel().selectedItemProperty()
                            .addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    cell.getContextMenu().hide();
                                }
                            });

                    if (cell.getContextMenu() != null) {
                        cell.getContextMenu().show(propertiesView,
                                Main.getStage().getX() + Main.getSplitPane().getDividers().get(0).getPosition()
                                        * Main.getSplitPane().getWidth(),
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
            String oldValue = attribute.getValue();
            if (attribute.getInput().verify(attribute.getObject(), e.getNewValue())) {
                attribute.setValue(e.getNewValue());
            }

            // If the edit was actually valid:
            if (e.getNewValue().equals("") || attribute.getInput().verify(attribute.getObject(), e.getNewValue())) {

                // Push an attribute change to the undo buffer.
                UndoManager.registerChange(new AttributeChangeAction(Main.getSelected(), attribute.getName(), oldValue,
                        attribute.getValue()));
                UndoManager.clearRedoActions();

                // If we have edited the name or ID of the object, change the object's "Name or
                // ID" value.
                if (attribute.getName().equals("name") || attribute.getName().equals("id")) {
                    Main.getSelected().setNameAttribute(attribute);
                }

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
    }


}
