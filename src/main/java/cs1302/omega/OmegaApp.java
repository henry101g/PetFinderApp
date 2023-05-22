package cs1302.omega;

import cs1302.game.DemoGame;
import cs1302.api.ApiBase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.layout.TilePane;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class OmegaApp extends Application {

    Label welcome;
    Label instructions;
    Button dogButton;
    Button catButton;
    Image dogImage;
    Image catImage;
    ImageView dogView;
    ImageView catView;
    HBox welcomeHBox;
    HBox instructionsHBox;
    VBox root;
    HBox buttonsHBox;
    HBox imageHBox;
    Label chosenPet;
    ComboBox<String> dropdown;
    boolean isDog;
    Button showImages;
    TilePane tile;
    Button backToHome;
    Label picturesShown;
    Button backScreen;
    /** Default height and width for Images. */
    protected static final int DEF_HEIGHT = 150;
    protected static final int DEF_WIDTH = 150;

    /**
     * Constructs an {@code OmegaApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public OmegaApp() {
        ApiBase.init();
        root = new VBox();
        tile = new TilePane();
        scene1();
    }

    /**
     * This function provides all details that are involved in the first scene.
     */

    public void scene1() {
        ApiBase.reset();
        root.getChildren().clear();
        welcome = new Label("Welcome to the pet discovery page!");
        welcome.setFont(new Font(50));
        welcome.setMaxWidth(Double.MAX_VALUE);
        welcome.setAlignment(Pos.CENTER);
        instructions = new Label("Select your pet");
        instructions.setFont(new Font(35));
        instructions.setMaxWidth(Double.MAX_VALUE);
        instructions.setAlignment(Pos.CENTER);

        dogButton = new Button("Dog");
        dogButton.setFont(new Font(20));
        //dogButton.setMaxSize(500, 400);
        catButton = new Button("Cat");
        catButton.setFont(new Font(20));
        //catButton.setMaxSize(100, 200);
        dogImage = new Image(ApiBase.DOGIMAGE, 300, 300, false, false);
        catImage = new Image(ApiBase.CATIMAGE, 300, 300, false, false);
        dogView = new ImageView(dogImage);
        catView = new ImageView(catImage);
        welcomeHBox = new HBox(welcome);
        welcomeHBox.setHgrow(welcome, Priority.ALWAYS);
        instructionsHBox = new HBox(instructions);
        instructionsHBox.setHgrow(instructions, Priority.ALWAYS);
        buttonsHBox = new HBox();
        buttonsHBox.setMargin(dogButton, new Insets(0, 370, 0, 0));
        buttonsHBox.setPadding(new Insets (20, 20, 20, 200));
        buttonsHBox.setHgrow(dogButton, Priority.ALWAYS);
        buttonsHBox.setPrefWidth(400);
        buttonsHBox.getChildren().addAll(dogButton, catButton);
        imageHBox = new HBox();
        imageHBox.setMargin(dogView, new Insets(0, 120, 0, 0));
        imageHBox.setPadding(new Insets(20, 20, 20, 80));
        imageHBox.getChildren().addAll(dogView, catView);
        root.getChildren().addAll(welcomeHBox, instructionsHBox, imageHBox, buttonsHBox);

        dogButton.setOnAction(e -> {
            isDog = true;
            chosenPet = new Label("You have chosen a dog! Now select your breed.");
            scene2();
        });

        catButton.setOnAction(e -> {
            isDog = false;
            chosenPet = new Label("You have chosen a cat! Now select your breed.");
            scene2();
        });
    }

    /**
     * This function sets up the dropdown menu.
     */

    public void dropdownFill() {
        dropdown = new ComboBox<String>();
        if (isDog) {
            ApiBase.getBreeds(ApiBase.DOGBREEDURL, ApiBase.DogAPIKey)
                .ifPresent(response -> ApiBase.fillBreeds(response));
        } else {
            ApiBase.getBreeds(ApiBase.CATBREEDURL, ApiBase.CatAPIKey)
                .ifPresent(response -> ApiBase.fillBreeds(response));
        }

        for (int i = 0; i < ApiBase.breedArray.size(); i++) {
            dropdown.getItems().add(ApiBase.breedArray.get(i).name);
        }
        dropdown.getSelectionModel().select(1);
    }

    /**
     * This function provides all details that are involved in the second scene.
     */

    public void scene2() {
        ApiBase.reset();
        dropdownFill();
        welcomeHBox.getChildren().clear();
        chosenPet.setFont(new Font(30));
        chosenPet.setMaxWidth(Double.MAX_VALUE);
        chosenPet.setAlignment(Pos.CENTER);
        welcomeHBox = new HBox(chosenPet);
        welcomeHBox.setHgrow(chosenPet, Priority.ALWAYS);
        imageHBox.getChildren().clear();
        showImages = new Button("Show Images");
        showImages.setFont(new Font(11));
        backScreen = new Button("Back");
        backScreen.setFont(new Font(11));
        imageHBox.setMargin(showImages, new Insets(0, 30, 0, 0));
        imageHBox.setMargin(dropdown, new Insets(0, 30, 0, 0));
        if (isDog) {
            imageHBox.setMargin(dogView, new Insets(0, 30, 0, 0));
            imageHBox.getChildren().addAll(dogView, dropdown, showImages, backScreen);
        } else {
            imageHBox.setMargin(catView, new Insets(0, 30, 0, 0));
            imageHBox.getChildren().addAll(catView, dropdown, showImages, backScreen);
        }

        root.getChildren().clear();
        root.getChildren().addAll(chosenPet, imageHBox);

        showImages.setOnAction(e -> {
            scene3();
        });

        backScreen.setOnAction(e -> {
            scene1();
        });
    }

    /**
     * This function provides all details that are involved in the third scene.
     */

    public void scene3() {
        if (isDog) {
            ApiBase.getImages(ApiBase.DOGIMAGEURL, ApiBase.DogAPIKey,
                ApiBase.getID(dropdown.getValue()), 60)
                .ifPresent(response -> ApiBase.fillImages(response));
        } else {
            ApiBase.getImages(ApiBase.CATIMAGEURL, ApiBase.CatAPIKey,
                ApiBase.getID(dropdown.getValue()), 60)
                .ifPresent(response -> ApiBase.fillImages(response));
        }
        tile.getChildren().clear();
        int numOfPictures = ApiBase.imageArray.size();
        int tileSize;
        if (numOfPictures < 9) {
            tileSize = numOfPictures;
            tile.setPrefColumns(3);
            tile.setPrefRows(numOfPictures % 3 + 1);
        } else {
            tileSize = 9;
            tile.setPrefColumns(3);
            tile.setPrefRows(3);
        }

        for (int i = 0; i < tileSize; i++) {
            Image img = new Image(ApiBase.imageArray.get(i).url,DEF_HEIGHT,
                DEF_WIDTH, false, false);
            ImageView iv = new ImageView(img);
            tile.getChildren().add(iv);
        }
        welcomeHBox.getChildren().clear();
        picturesShown = new Label("Enjoy pictures of " + dropdown.getValue() + "!!!");
        picturesShown.setFont(new Font(30));
        picturesShown.setMaxWidth(Double.MAX_VALUE);
        picturesShown.setAlignment(Pos.CENTER);
        backToHome = new Button("Back Home");
        backToHome.setFont(new Font(11));
        backToHome.setMaxWidth(Double.MAX_VALUE);
        backToHome.setAlignment(Pos.CENTER);
        welcomeHBox = new HBox(picturesShown);
        welcomeHBox.setHgrow(picturesShown, Priority.ALWAYS);
        backScreen = new Button("Back");
        backScreen.setFont(new Font(11));

        imageHBox.getChildren().clear();
        imageHBox.setMargin(tile, new Insets(0, 30, 0, 0));
        imageHBox.setMargin(backScreen, new Insets(0, 30, 0, 0));
        imageHBox.getChildren().addAll(tile, backScreen, backToHome);
        root.getChildren().clear();
        root.getChildren().addAll(welcomeHBox, imageHBox);

        backToHome.setOnAction(e -> {
            ApiBase.reset();
            scene1();
        });

        backScreen.setOnAction(e -> {
            scene2();
        });
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        Scene scene = new Scene(root);

        // setup stage
        stage.setTitle("OmegaApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

        // play the game
        //game.play();

    } // start

} // OmegaApp
