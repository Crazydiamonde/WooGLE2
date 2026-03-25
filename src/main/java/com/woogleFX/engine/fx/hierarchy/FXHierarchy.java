package com.woogleFX.engine.fx.hierarchy;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.AssetManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class FXHierarchy {

    private static final TreeTableView<EditorObject> hierarchy = new TreeTableView<>();
    public static TreeTableView<EditorObject> getHierarchy() {
        return hierarchy;
    }


    public static void init() {

        hierarchy.setPlaceholder(new Label());

        // TODO: this
        /*
        TreeTableColumn<EditorObject, Boolean> hierarchyHidden = new TreeTableColumn<>();
        hierarchyHidden.setPrefWidth(20);
        hierarchyHidden.setMinWidth(20);
        hierarchyHidden.setMaxWidth(20);
        hierarchyHidden.setResizable(false);
        hierarchyHidden.setSortable(false);
        hierarchyHidden.setReorderable(false);
        hierarchy.getColumns().add(hierarchyHidden);
         */

        // Create the columns the hierarchy uses ("Element" and its "ID or Name")
        TreeTableColumn<EditorObject, String> hierarchyElements = new TreeTableColumn<>();
        hierarchyElements.setGraphic(new Label("Element"));
        hierarchyElements.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        hierarchy.getColumns().add(hierarchyElements);
        hierarchyElements.setSortable(false);
        hierarchyElements.setReorderable(false);

        TreeTableColumn<EditorObject, String> hierarchyNames = new TreeTableColumn<>();
        hierarchyNames.setGraphic(new Label("ID or Name"));
        hierarchyNames.setSortable(false);
        hierarchyNames.setReorderable(false);

        hierarchyNames.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));

        hierarchy.getColumns().add(hierarchyNames);

        hierarchyElements.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                HierarchyManager.updateElementCell(this, item, empty);
            }
        });

        hierarchyNames.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                HierarchyManager.updateNameCell(this, item, empty);
            }
        });

        // If a cell is clicked from the hierarchy, update the selected object and
        // properties view.
        hierarchy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null || newValue.getValue() == null || hierarchy.getSelectionModel().getSelectedItems().isEmpty() ||
                    hierarchy.getSelectionModel().getSelectedItems().get(0) == null) return;

            EditorObject editorObject = newValue.getValue();

            TabPane hierarchySwitcherButtons = FXHierarchySwitcherButtons.getHierarchySwitcherButtons();

            hierarchySwitcherButtons.getSelectionModel().select(AssetManager.getAsset().getTabForObject(editorObject));

            EditorObject[] selectedNow = new EditorObject[hierarchy.getSelectionModel().getSelectedItems().size()];
            for (int i = 0; i < selectedNow.length; i++)
                selectedNow[i] = hierarchy.getSelectionModel().getSelectedItems().get(i).getValue();

            AssetManager.getAsset().setSelectedDiscreetly(selectedNow);

            FXPropertiesView.changeTableView(selectedNow);

        });

        // Make the rows small.
        hierarchy.setFixedCellSize(18);

        hierarchy.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hierarchy.setRowFactory(treeTableView -> HierarchyManager.createRow(hierarchy));

        hierarchy.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        hierarchy.setPrefHeight(FXStage.getStage().getHeight() * 0.4);

        hierarchy.setId("hierarchy");

    }


    public static void scrollTo(EditorObject editorObject) {

        FXHierarchy.getHierarchy().scrollTo(FXHierarchy.getHierarchy().getRow(editorObject.getTreeItem()) - (int)(FXHierarchy.getHierarchy().getHeight() / FXHierarchy.getHierarchy().getFixedCellSize() / 2));

    }

}
