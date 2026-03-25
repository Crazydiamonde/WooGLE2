package com.woogleFX.engine.fx.propertiesView;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.undoHandling.UndoManager;
import com.woogleFX.engine.undoHandling.userActions.AttributeChangeAction;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;

public class FXPropertiesView {

    private static final TreeTableView<EditorAttribute[]> propertiesView = new TreeTableView<>();
    public static TreeTableView<EditorAttribute[]> getPropertiesView() {
        return propertiesView;
    }



    public static void init() {

        propertiesView.prefHeightProperty().bind(FXContainers.getViewPane().heightProperty().subtract(FXPropertiesView.getPropertiesView().layoutYProperty()));

        // This seems to break when the user double-clicks on a hierarchy item with children (like those in "Addin").
        // TODO: figure out how to fix this or make some equivalent placeholder display
        // Oh or not? Maybe it's fine
        propertiesView.setPlaceholder(new Label("Select an element to edit"));

        // Create the columns the properties view uses (Attribute name and attribute value).
        TreeTableColumn<EditorAttribute[], String> name = new TreeTableColumn<>();
        name.setGraphic(new Label("Name"));
        // they'll all have the same name
        name.setCellValueFactory(param -> param.getValue().getValue()[0].getNameProperty());
        name.setSortable(false);
        name.setReorderable(false);
        propertiesView.getColumns().add(name);

        TreeTableColumn<EditorAttribute[], String> value = new TreeTableColumn<>();
        value.setGraphic(new Label("Value"));
        value.setCellValueFactory(param -> new SimpleStringProperty(){
            @Override
            public void setValue(String s) {
                for (EditorAttribute editorAttribute : param.getValue().getValue()) {
                    editorAttribute.getValueProperty().setValue(s);
                }
            }

            @Override
            public String getValue() {
                String value = null;
                for (EditorAttribute editorAttribute : param.getValue().getValue()) {
                    if (value == null) value = editorAttribute.actualValue();
                    else if (!editorAttribute.actualValue().equals(value)) return "";
                }
                return value;
            }
        });
        value.setSortable(false);
        value.setReorderable(false);
        propertiesView.getColumns().add(value);

        // Limit the width of the "names" column.
        //name.setPrefWidth(200);

        // Hide the empty "root" attribute. This allows all of its children to be
        // displayed at once at the top of the TreeTableView.
        propertiesView.setShowRoot(false);

        // For each row of the properties view:
        propertiesView.setRowFactory(tableView -> PropertiesViewManager.createRow());
        name.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    // If this is an empty cell, set its text and graphic to empty.
                    // This prevents the cell from retaining other cells' information.
                    setText(null);
                    return;
                }

                // Update this cell's text.
                setText(item);
                // Override the default padding that ruins the text.
                setPadding(new Insets(0, 0, 0, 0));

                if (getTableRow().getTreeItem() == null) return;
                EditorAttribute attribute = getTableRow().getTreeItem().getValue()[0];

                boolean valid = true;

                if (attribute.stringValue().isEmpty()) {
                    if (attribute.getType() != null && !attribute.getType().verify(attribute.getObject(), attribute.getDefaultValue(), attribute.getRequired())) {
                        valid = false;
                    }
                } else if (attribute.getType() != null && !attribute.getType().verify(attribute.getObject(), attribute.actualValue(), attribute.getRequired())) {
                    valid = false;
                }
                if (valid) {
                    setStyle("-fx-text-fill: #000000ff");
                } else {
                    setStyle("-fx-text-fill: #ff0000ff");
                }
            }
        });

        value.setCellFactory(e -> PropertiesViewManager.createValueCell());

        // value.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        value.setOnEditCommit(e -> {
            // When editing a cell:

            // Change the actual attribute to reflect the edit.
            ArrayList<AttributeChangeAction> attributeChangeActions = new ArrayList<>();
            for (EditorAttribute attribute : propertiesView.getTreeItem(e.getTreeTablePosition().getRow()).getValue()) {
                String oldValue = attribute.stringValue();
                if (attribute.getType().verify(attribute.getObject(), e.getNewValue(), attribute.getRequired())) {
                    if (attribute.getType() == InputField._2_BALL_TYPE_USERVAR) {
                        attribute.getObject().setAttribute(attribute.getObject().getName(), e.getNewValue());
                    } else {
                        attribute.getObject().setAttribute(attribute.getName(), e.getNewValue());
                    }
                }

                // If the edit was actually valid:
                if (e.getNewValue().isEmpty() || attribute.getType().verify(attribute.getObject(), e.getNewValue(), attribute.getRequired())) {

                    // Push an attribute change to the undo buffer.
                    attributeChangeActions.add(new AttributeChangeAction(attribute, oldValue, e.getNewValue()));
                    attribute.setValue(e.getNewValue());

                    // If we have edited the name or ID of the object, change the object's "Name or
                    // ID" value.
                    propertiesView.refresh();

                } else {
                    // Reset the attribute.
                    attribute.setValue(oldValue);
                    // If the user entered an invalid value, refresh to clear the edit.
                    propertiesView.refresh();
                }
            }

            if (!attributeChangeActions.isEmpty()) {
                UndoManager.registerChange(attributeChangeActions.toArray(AttributeChangeAction[]::new));
            }

            FXHierarchy.getHierarchy().refresh();

        });

        // Adjust the row height to fit more attributes on screen at once.
        // This currently breaks the text.
        propertiesView.setFixedCellSize(18);

        // Set the "value" column and the entire TreeTableView to be editable.
        value.setEditable(true);
        propertiesView.setEditable(true);

        propertiesView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        propertiesView.setRoot(new TreeItem<>(new EditorAttribute[]{ EditorAttribute.NULL }));

    }


    public static TreeItem<EditorAttribute[]> makePropertiesViewTreeItem(EditorObject[] selectionList) {

        if (selectionList.length == 0) return new TreeItem<>(new EditorAttribute[0]);

        EditorObject object = selectionList[0];

        // If some of the objects are of different types, don't fill out the properties view. That would just be weird.
        if (!Arrays.stream(selectionList).allMatch(e -> e.getType().equals(object.getType()))) return null;

        // Create the root tree item.
        TreeItem<EditorAttribute[]> treeItem = new TreeItem<>(new EditorAttribute[]{ EditorAttribute.NULL });

        // Loop over the object's meta attributes.
        for (MetaEditorAttribute metaEditorAttribute : object.getMetaAttributes()) {
            recursiveMetaAttributeAdd(metaEditorAttribute, treeItem, selectionList);
        }

        return treeItem;

    }


    private static void recursiveMetaAttributeAdd(MetaEditorAttribute metaEditorAttribute, TreeItem<EditorAttribute[]> parent, EditorObject[] objects) {

        // Find the object's EditorAttribute associated with this meta attribute
        // (sharing the same name).
        EditorAttribute[] attributes = new EditorAttribute[objects.length];
        for (int i = 0; i < objects.length; i++) {
            EditorObject object = objects[i];
            if (object.attributeExists(metaEditorAttribute.getName())) {
                attributes[i] = object.getAttribute(metaEditorAttribute.getName());
            } else {
                // If no such attribute exists, this attribute is instead the name of a category
                // of attributes.
                // In this case, create a dummy attribute with no value.
                attributes[i] = new EditorAttribute(metaEditorAttribute.getName(), null, object);
            }
            attributes[i].setDescription(metaEditorAttribute.getDescription());
            if (attributes[i].getType() == InputField._2_LIST_CHILD || attributes[i].getType() == InputField._2_LIST_CHILD_HIDDEN) return;
        }
        TreeItem<EditorAttribute[]> thisAttribute = new TreeItem<>(attributes);

        // If this attribute is set to be open by default, set its tree item to open.
        if (metaEditorAttribute.getOpenByDefault()) {
            thisAttribute.setExpanded(true);
        }

        // If this attribute represents a category of attributes, it will have children.
        // Add the children's TreeItems as children of the category's TreeItem.
        for (MetaEditorAttribute child : metaEditorAttribute.getChildren()) {
            recursiveMetaAttributeAdd(child, thisAttribute, objects);
        }

        // Add the attribute's TreeItem as a child of the root's TreeItem.
        parent.getChildren().add(thisAttribute);


    }


    public static void changeTableView(EditorObject[] selectionList) {
        propertiesView.setRoot(makePropertiesViewTreeItem(selectionList));
    }


}
