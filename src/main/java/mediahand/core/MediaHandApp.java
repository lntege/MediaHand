package mediahand.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mediahand.MediaLoader;
import mediahand.controller.MediaHandAppController;
import mediahand.domain.DirectoryEntry;
import mediahand.repository.base.Database;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MediaHandApp extends Application {

    private BorderPane rootLayout;

    private static Stage stage;
    private static MediaLoader mediaLoader;
    private static MediaHandAppController mediaHandAppController;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        MediaHandApp.stage = stage;
        MediaHandApp.mediaLoader = new MediaLoader();

        initRootLayout();

        initDatabase();

        showMediaHand();
    }

    private void initRootLayout() throws IOException {
        this.rootLayout = FXMLLoader.load(getClass().getResource("/fxml/RootLayout.fxml"));
        scene = new Scene(this.rootLayout);
        MediaHandApp.stage.setScene(scene);
        MediaHandApp.stage.setTitle("Media Hand");
        MediaHandApp.stage.show();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Database.closeConnections();
            System.out.println("Program closed");
        }));
    }

    private void initDatabase() {
        Database.init("AnimeDatabase", "lueko", "1234", false);

        if (Database.getBasePathRepository().findAll().size() == 0) {
            chooseBasePath();
        }
    }

    private void showMediaHand() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MediaHandApp.class.getResource("/fxml/mediaHandApp.fxml"));
            this.rootLayout.setCenter(loader.load());
            MediaHandApp.mediaHandAppController = loader.getController();
            mediaHandAppController.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean chooseBasePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dialog = directoryChooser.showDialog(MediaHandApp.stage);

        if (dialog != null) {
            MediaHandApp.mediaLoader.addMedia(Database.getBasePathRepository().create(new DirectoryEntry(dialog.getAbsolutePath())));
            return true;
        }
        return false;
    }

    public static Optional<File> chooseMediaDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dialog = directoryChooser.showDialog(MediaHandApp.getStage());

        return Optional.ofNullable(dialog);
    }

    public static MediaHandAppController getMediaHandAppController() {
        return MediaHandApp.mediaHandAppController;
    }

    public static MediaLoader getMediaLoader() {
        return MediaHandApp.mediaLoader;
    }

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return scene;
    }
}
