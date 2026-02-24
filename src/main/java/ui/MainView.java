package ui;

import controller.PlaybackController;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainView extends BorderPane {
    public MainView() {
        setPadding(new Insets(10));

        PlaybackController playbackController = new PlaybackController();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(createTab("Sorting", new SortingView(playbackController)));
        tabPane.getTabs().add(createTab("Maze", new MazeView(playbackController)));
        tabPane.getTabs().add(createTab("Trees", new TreeView(playbackController)));

        setCenter(tabPane);
    }

    private Tab createTab(String title, VBox content) {
        Tab tab = new Tab(title);
        tab.setClosable(false);
        tab.setContent(content);
        return tab;
    }
}
