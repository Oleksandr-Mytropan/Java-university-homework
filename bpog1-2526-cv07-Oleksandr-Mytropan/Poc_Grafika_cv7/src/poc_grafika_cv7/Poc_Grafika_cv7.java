package poc_grafika_cv7;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.*;

public class Poc_Grafika_cv7 extends Application {

    private TreeView<String> treeView;
    private TreeItem<String> rootItem;

    private Image rootIcon;
    private Image europeIcon;
    private Image asiaIcon;
    private Image americaIcon;
    private Image africaIcon;
    private Image australiaIcon;
    private Image countryIcon;

    @Override
    public void start(Stage stage) {

        rootIcon = loadImage("World_16.png");
        europeIcon = loadImage("Europe_16.png");
        asiaIcon = loadImage("Asia_16.png");
        americaIcon = loadImage("America_16.png");
        africaIcon = loadImage("Africa_16.png");
        australiaIcon = loadImage("Australia_16.png");
        countryIcon = loadImage("Unknown_16.png");

        rootItem = new TreeItem<>("Seznam států", createIcon(rootIcon));
        rootItem.setExpanded(true);

        TreeItem<String> evropa = new TreeItem<>("Evropa", createIcon(europeIcon));
        TreeItem<String> asie = new TreeItem<>("Asie", createIcon(asiaIcon));
        TreeItem<String> amerika = new TreeItem<>("Amerika", createIcon(americaIcon));
        TreeItem<String> afrika = new TreeItem<>("Afrika", createIcon(africaIcon));
        TreeItem<String> australia = new TreeItem<>("Austrálie a Oceánie", createIcon(australiaIcon));

        rootItem.getChildren().addAll(evropa, asie, amerika, afrika, australia);

        List<State> states = List.of(
                new State("Česko", "Evropa"),
                new State("Německo", "Evropa"),
                new State("Francie", "Evropa"),
                new State("Japonsko", "Asie"),
                new State("Čína", "Asie"),
                new State("USA", "Amerika"),
                new State("Austrálie", "Austrálie a Oceánie")
        );
        
        Map<String, TreeItem<String>> map = new HashMap<>();
        map.put("Evropa", evropa);
        map.put("Asie", asie);
        map.put("Amerika", amerika);
        map.put("Afrika", afrika);
        map.put("Austrálie a Oceánie", australia);

        for (State s : states) {
            TreeItem<String> parent = map.get(s.getContinent());
            if (parent != null) {
                parent.getChildren().add(
                        new TreeItem<>(s.getName(), createIcon(countryIcon))
                );
            }
        }

        treeView = new TreeView<>(rootItem);

        treeView.setEditable(true);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView());

        MenuItem addItem = new MenuItem("Add new");
        MenuItem removeItem = new MenuItem("Remove");

        ContextMenu menu = new ContextMenu(addItem, removeItem);
        treeView.setContextMenu(menu);

        addItem.setOnAction(e -> addItem());
        removeItem.setOnAction(e -> removeItem());

        StackPane root = new StackPane(treeView);
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("TreeView");
        stage.show();
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("Image not found: " + path);
            return null;
        }
    }

    private ImageView createIcon(Image img) {
        if (img == null) {
            return null;
        } else {
            return new ImageView(img);
        }
    }

    private void addItem() {
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();

        if (selected != null) {

            TreeItem<String> newItem;

            if (selected == rootItem) {
                newItem = new TreeItem<>("New Continent", createIcon(rootIcon));
            } else {
                newItem = new TreeItem<>("New State", createIcon(countryIcon));
            }

            selected.getChildren().add(newItem);
            selected.setExpanded(true);
        }
    }

    private void removeItem() {
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();

        if (selected != null && selected != rootItem) {
            selected.getParent().getChildren().remove(selected);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
