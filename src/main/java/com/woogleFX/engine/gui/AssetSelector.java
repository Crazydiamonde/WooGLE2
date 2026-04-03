package com.woogleFX.engine.gui;

import com.woogleFX.assets.Asset;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.assetSelectPane.FXAssetSelectPane;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.AssetLoader;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AssetSelector<T extends Asset> extends Application {

    private final Map<Pair<String, GameVersion>, T> importedAssets = new HashMap<>();


    private final String title;
    public String getTitle() {
        return title;
    }
    public AssetSelector(String title) {
        this.title = title;
    }


    private Label selectedLabel;


    private void rebuildAssetSelectBox(VBox assetSelectBox, ArrayList<Label> labels, String searchField, int filterState, GameVersion version) {

        assetSelectBox.getChildren().clear();

        int i = 0;
        for (Label label : labels) {
            if (label.getText().toLowerCase().contains(searchField.toLowerCase()) || searchField.isEmpty()) {
                if (filterState == 0 && !isOriginal(label.getText(), version)) continue;
                if (filterState == 1 && isOriginal(label.getText(), version)) continue;
                assetSelectBox.getChildren().add(label);
                if (i % 2 == 0) {
                    label.setStyle("-fx-background-color: #f0f0f0");
                    label.setId("-fx-background-color: #f0f0f0");
                }
                else {
                    label.setStyle("-fx-background-color: #e4e4e4");
                    label.setId("-fx-background-color: #e4e4e4");
                }
                label.setPrefWidth(400);
                i++;
            }
        }

    }


    private void buildStage(Stage stage, GameVersion version, boolean forNew) {

        VBox allEncompassingBox = new VBox();

        if (forNew) {

            TextField enterNameField = new TextField();

            Button doneButton = new Button("Done");

            doneButton.setOnAction(actionEvent -> {
                // TODO: check if an asset with this name already exists
                String name = enterNameField.getText();
                T newAsset = newInstance(name, version);
                AssetManager.setAsset(newAsset);
                FXEditorButtons.updateAllButtons();
                FXMenu.updateAllButtons();
                FXAssetSelectPane.getAssetSelectPane().setMinHeight(30);
                FXAssetSelectPane.getAssetSelectPane().setMaxHeight(30);
                AssetLoader.finishOpeningAsset(newAsset);

                stage.close();

            });

            allEncompassingBox.getChildren().addAll(enterNameField, doneButton);

            stage.setTitle("Set Name for New " + getTitle() + "...");

        } else {

            VBox assetSelectBox = new VBox();

            ArrayList<Label> labels = new ArrayList<>();

            TextField searchField = new TextField();


            ComboBox<String> filter = new ComboBox<>();

            filter.getItems().addAll("Original Only", "Customizable Only", "All Assets");

            filter.getSelectionModel().selectedIndexProperty().addListener((observableValue, s, t1) ->
                    rebuildAssetSelectBox(assetSelectBox, labels, searchField.getText(), t1.intValue(), version));
            filter.getSelectionModel().select(2);

            Button illDoItMyself = new Button("...");
            illDoItMyself.setOnAction(actionEvent -> {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(FileManager.getGameDir(version)));
                fileChooser.getExtensionFilters().add(getCustomExtensionFilter(version));
                File file = fileChooser.showOpenDialog(new Stage());
                if (file == null) return;

                // TODO: support directories for levels

                try {
                    AssetLoader.openAsset(this, file, getNameFromFile(file, version), version);
                } catch (IOException e) {
                    ErrorAlarm.show(e);
                }

                stage.close();

            });


            List<String> items;
            try {
                items = getItems(version);
            } catch (IOException e) {
                ErrorAlarm.show(e);
                return;
            }
            for (String item : items) {
                Label label = new Label(item);

                label.setOnMouseClicked(event -> {
                    if (label == selectedLabel) {
                        File file = getDefaultFileForName(item, version);
                        try {
                            AssetLoader.openAsset(this, file, label.getText(), version);
                        } catch (IOException e) {
                            ErrorAlarm.show(e);
                        }
                        stage.close();
                    } else {
                        if (selectedLabel != null) selectedLabel.setStyle(selectedLabel.getId());
                        selectedLabel = label;
                        label.setStyle("-fx-background-color: #C0E0FFFF");
                    }
                });

                labels.add(label);
            }

            searchField.textProperty().addListener((observableValue, string, t1) -> rebuildAssetSelectBox(assetSelectBox, labels, t1, filter.getSelectionModel().getSelectedIndex(), version));
            rebuildAssetSelectBox(assetSelectBox, labels, "", filter.getSelectionModel().getSelectedIndex(), version);

            ScrollPane scrollPane = new ScrollPane(assetSelectBox);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            HBox hBox = new HBox(searchField, filter, illDoItMyself);

            allEncompassingBox.getChildren().addAll(hBox, scrollPane);

            searchField.prefWidthProperty().bind(hBox.widthProperty().subtract(filter.widthProperty()).subtract(illDoItMyself.widthProperty()));

            stage.setTitle("Select " + getTitle() + "...");

        }

        stage.setScene(new Scene(allEncompassingBox, 400, 375));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

    }

    @Override
    public void start(Stage stage) throws Exception {

    }


    public void start(Stage stage, GameVersion version, boolean forNew) {
        buildStage(stage, version, forNew);
    }


    public abstract List<String> getItems(GameVersion version) throws IOException;

    public abstract boolean isOriginal(String item, GameVersion version);

    protected abstract T secretNewInstance(String name, GameVersion version);

    protected abstract T secretOpenInstance(File file, String name, GameVersion version) throws IOException;


    protected abstract FileChooser.ExtensionFilter getCustomExtensionFilter(GameVersion version);

    protected abstract File getDefaultFileForName(String name, GameVersion version);

    protected abstract String getNameFromFile(File file, GameVersion version);


    public final T newInstance(String name, GameVersion version) {
        T asset = secretNewInstance(name, version);
        if (asset == null) return null;

        asset.setName(name);
        asset.load();
        // Get the default file for this asset using its asset selector
        AssetSelector<?> assetSelector;
        try {
            assetSelector = (AssetSelector<?>) asset.getClass().getField("assetSelector").get(asset);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        asset.setFile(assetSelector.getDefaultFileForName(name, version));
        // Save the asset so it appears in the file structure
        asset.save(asset.getFile());
        importedAssets.put(new Pair<>(name, version), asset);
        return asset;
    }


    public final T openInstance(File file, String name, GameVersion version) throws IOException {
        T importedAsset = importedAssets.get(new Pair<>(name, version));
        if (importedAsset != null) return importedAsset;

        T asset = secretOpenInstance(file, name, version);
        if (asset == null) return null;

        asset.setName(name);
        asset.setFile(file);
        importedAssets.put(new Pair<>(name, version), asset);
        return asset;
    }


    public final T openInstance(String name, GameVersion version) throws IOException {
        return openInstance(getDefaultFileForName(name, version), name, version);
    }


    public final void removeImportedAsset(Asset asset) {
        importedAssets.remove(new Pair<>(asset.getName(), asset.getVersion()));
    }

}
