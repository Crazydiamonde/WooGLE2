package com.woogleFX.engine.fx.menu;

import com.woogleFX.file.FileManager;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.util.Stack;

public class FXMenu {

    public static abstract class EditorMenuItem extends MenuItem {

        public abstract void updateDisabled();

        public void setIcon(String pathString) {
            setGraphic(new ImageView(FileManager.getIcon(pathString)));
        }

    }

    public static abstract class EditorMenu extends Menu {

        public abstract void updateDisabled();

        public void setIcon(String pathString) {
            setGraphic(new ImageView(FileManager.getIcon(pathString)));
        }

        public EditorMenu(String s) {
            super(s);
        }

    }


    private static final MenuBar menuBar = new MenuBar();
    public static MenuBar getMenuBar() {
        return menuBar;
    }


    public static void init() {
        FXMenu_File.init();
        FXMenu_Asset.init();
        FXMenu_Edit.init();
        FXMenu_Resources.init();

        menuBar.getMenus().addAll(
                FXMenu_File.getFileMenu(),
                FXMenu_Asset.getLevelMenu(),
                FXMenu_Edit.getEditMenu(),
                FXMenu_Resources.getResourcesMenu()
        );

    }


    public static void updateAllButtons() {

        Stack<MenuItem> menuItems = new Stack<>();
        for (Menu menu : menuBar.getMenus()) menuItems.addAll(menu.getItems());

        while (!menuItems.isEmpty()) {
            MenuItem menuItem = menuItems.pop();
            if (menuItem instanceof EditorMenuItem editorMenuItem)
                editorMenuItem.updateDisabled();
            else if (menuItem instanceof Menu menu)
                menuItems.addAll(menu.getItems());
        }

    }

}
