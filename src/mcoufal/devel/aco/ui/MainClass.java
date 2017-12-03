package mcoufal.devel.aco.ui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import mcoufal.devel.aco.core.AntColonyAlgorithm;
import mcoufal.devel.aco.core.AntCycleAlgorithm;
import mcoufal.devel.aco.core.CrossroadPoint;

public class MainClass extends Application {

	// algorithm variables

	// used algorithm
	private AntColonyAlgorithm alg = null;
	// list of all crossroad points
	private static ArrayList<CrossroadPoint> allCrossroads = new ArrayList<CrossroadPoint>();
	// id counter for crossroad points
	private int crossroadPointID = 0;
	// maximum number of algorithm iterations
	private static int maximumIterations = 100;
	// total number of ants
	private static int numberOfAnts = 10;
	// initial pheromone levels
	private static double a = 1;
	// impact parameter of pheromone
	private static double alpha = 1;
	// impact parameter of visibility
	private static double beta = 1;
	// pheromone quantity given by each ant per cycle
	private static double quantityOfPheromone = 100;
	// pheromone evaporation parameter
	private static double ro = 0.1;

	// GUI variables

	// crossroad point size
	private int crossroadPointSize = 10;
	// algorithm initialised
	private Boolean algorithmInitialised = false;
	// indicator if all the values are set
	private Boolean antsSet = true;
	private Boolean iterationsSet = true;
	private Boolean aSet = true;
	private Boolean alphaSet = true;
	private Boolean betaSet = true;
	private Boolean qSet = true;
	private Boolean roSet = true;

	@Override
	public void start(Stage primaryStage) {
		try {
			// set up basic GUI optimisation
			VBox root = new VBox();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Ant Colony Optimisation demo");
			primaryStage.show();

			// setup canvas area
			Canvas canvasLayer1 = new Canvas();
			canvasLayer1.widthProperty().bind(scene.widthProperty());
			canvasLayer1.heightProperty().bind(scene.heightProperty());
			Canvas canvasLayer2 = new Canvas();
			canvasLayer2.widthProperty().bind(scene.widthProperty());
			canvasLayer2.heightProperty().bind(scene.heightProperty());
			StackPane canvasContainer = new StackPane(canvasLayer1, canvasLayer2);
			canvasContainer.getStyleClass().add("canvas");
			GraphicsContext gcLayer1 = canvasLayer1.getGraphicsContext2D();
			GraphicsContext gcLayer2 = canvasLayer2.getGraphicsContext2D();
			gcLayer1.setFill(Color.BLUE);
			gcLayer1.setStroke(Color.BLACK);
			gcLayer2.setFill(Color.BLUE);
			gcLayer2.setStroke(Color.BLACK);

			// setup top area
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER);
			hbox.setSpacing(10);
			// create 'initialise' button
			Button initButton = new Button("Init");
			// create 'step' button
			Button stepButton = new Button("Step");
			// create 'run' button
			Button runButton = new Button("Run");
			// create 'clear' button
			Button clearButton = new Button("Clear");
			hbox.getChildren().addAll(initButton, stepButton, runButton, clearButton);
			root.getChildren().addAll(hbox, canvasContainer);

			// define actions

			// canvas - layer 2
			canvasLayer2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					algorithmInitialised = false;
					gcLayer1.fillOval(e.getX() - crossroadPointSize / 2, e.getY() - crossroadPointSize / 2,
							crossroadPointSize, crossroadPointSize);
					allCrossroads.add(new CrossroadPoint(crossroadPointID, new Point2D.Double(e.getX(), e.getY())));
					crossroadPointID++;
				}
			});

			// init button
			initButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					Dialog<Pair<String, String>> dialog = new Dialog<>();
					dialog.setTitle("Set algorithm variables");

					// Set up the buttons
					ButtonType okSetButton = new ButtonType("OK", ButtonData.OK_DONE);
					ButtonType cancelSetButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
					dialog.getDialogPane().getButtonTypes().addAll(okSetButton, cancelSetButton);

					// create labels and text fields for algorithm variables
					GridPane grid = new GridPane();
					grid.setHgap(10);
					grid.setVgap(10);
					grid.setPadding(new Insets(20, 150, 10, 10));

					// number of ants
					TextField numOfAntsText = new TextField();
					numOfAntsText.setText(Integer.toString(numberOfAnts));
					numOfAntsText.setPromptText("Number of ants");
					numOfAntsText.setTooltip(new Tooltip("Only integer values"));
					numOfAntsText.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!newValue.matches("\\d*")) {
								numOfAntsText.setText(newValue.replaceAll("[^\\d]", ""));
							}
						}
					});

					// maximum iterations
					TextField maximumIterationsText = new TextField();
					maximumIterationsText.setText(Integer.toString(maximumIterations));
					maximumIterationsText.setPromptText("Maximum iterations");
					maximumIterationsText.setTooltip(new Tooltip("Only integer values"));
					maximumIterationsText.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!newValue.matches("\\d*")) {
								maximumIterationsText.setText(newValue.replaceAll("[^\\d]", ""));
							}
						}
					});

					// initial pheromone level
					TextField initialPheromoneLevelText = new TextField();
					initialPheromoneLevelText.setText(Double.toString(a));
					initialPheromoneLevelText.setPromptText("Initial pheromone level(a)");
					initialPheromoneLevelText.setTooltip(new Tooltip("Only numeric values"));
					initialPheromoneLevelText.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!newValue.matches("\\d+(\\.\\d*)?")) {
								initialPheromoneLevelText.setText(newValue.replaceAll("[^\\d]", ""));
							}
						}
					});

					// impact parameter of pheromone
					TextField alphaText = new TextField();
					alphaText.setText(Double.toString(alpha));
					alphaText.setPromptText("Impact of pheromone");
					alphaText.setTooltip(new Tooltip("Only numeric values"));
					alphaText.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!newValue.matches("\\d+(\\.\\d*)?")) {
								alphaText.setText(newValue.replaceAll("[^\\d]", ""));
							}
						}
					});

					// impact parameter of visibility
					TextField betaText = new TextField();
					betaText.setText(Double.toString(beta));
					betaText.setPromptText("Impact of visibility");
					betaText.setTooltip(new Tooltip("Only numeric values"));
					betaText.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!newValue.matches("\\d+(\\.\\d*)?")) {
								betaText.setText(newValue.replaceAll("[^\\d]", ""));
							}
						}
					});

					// pheromone quantity
					TextField pheromoneQuantityText = new TextField();
					pheromoneQuantityText.setText(Double.toString(quantityOfPheromone));
					pheromoneQuantityText.setPromptText("Pheromone quantity(Q)");
					pheromoneQuantityText.setTooltip(new Tooltip("Only numeric values"));
					pheromoneQuantityText.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!newValue.matches("\\d+(\\.\\d*)?")) {
								pheromoneQuantityText.setText(newValue.replaceAll("[^\\d]", ""));
							}
						}
					});

					// pheromone evaporation parameter
					TextField pheromoneEvaporationText = new TextField();
					pheromoneEvaporationText.setText(Double.toString(ro));
					pheromoneEvaporationText.setPromptText("Pheromone evaporation(ro)");
					pheromoneEvaporationText.setTooltip(new Tooltip("Only numeric values"));
					pheromoneEvaporationText.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!newValue.matches("\\d+(\\.\\d*)?")) {
								pheromoneEvaporationText.setText(newValue.replaceAll("[^\\d]", ""));
							}
						}
					});

					// add to grid
					grid.add(new Label("Number of ants:"), 0, 0);
					grid.add(numOfAntsText, 1, 0);

					grid.add(new Label("Maximum iterations:"), 0, 1);
					grid.add(maximumIterationsText, 1, 1);

					grid.add(new Label("Initial pheromone level:"), 0, 2);
					grid.add(initialPheromoneLevelText, 1, 2);

					grid.add(new Label("Alpha:"), 0, 3);
					grid.add(alphaText, 1, 3);

					grid.add(new Label("Beta:"), 0, 4);
					grid.add(betaText, 1, 4);

					grid.add(new Label("Pheromon quantity:"), 0, 5);
					grid.add(pheromoneQuantityText, 1, 5);

					grid.add(new Label("Pheromon evaporation:"), 0, 6);
					grid.add(pheromoneEvaporationText, 1, 6);

					// disable ok button when some parameter is not set
					Node okButton = dialog.getDialogPane().lookupButton(okSetButton);
					numOfAntsText.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.trim().isEmpty()) {
							antsSet = true;
							if (allSet()) {
								okButton.setDisable(false);
							}
						} else if (newValue.trim().isEmpty()) {
							okButton.setDisable(true);
							antsSet = false;
						}
					});
					maximumIterationsText.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.trim().isEmpty()) {
							iterationsSet = true;
							if (allSet()) {
								okButton.setDisable(false);
							}
						} else if (newValue.trim().isEmpty()) {
							okButton.setDisable(true);
							iterationsSet = false;
						}
					});
					initialPheromoneLevelText.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.trim().isEmpty()) {
							aSet = true;
							if (allSet()) {
								okButton.setDisable(false);
							}
						} else if (newValue.trim().isEmpty()) {
							okButton.setDisable(true);
							aSet = false;
						}
					});
					alphaText.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.trim().isEmpty()) {
							alphaSet = true;
							if (allSet()) {
								okButton.setDisable(false);
							}
						} else if (newValue.trim().isEmpty()) {
							okButton.setDisable(true);
							alphaSet = false;
						}
					});
					betaText.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.trim().isEmpty()) {
							betaSet = true;
							if (allSet()) {
								okButton.setDisable(false);
							}
						} else if (newValue.trim().isEmpty()) {
							okButton.setDisable(true);
							betaSet = false;
						}
					});
					pheromoneQuantityText.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.trim().isEmpty()) {
							qSet = true;
							if (allSet()) {
								okButton.setDisable(false);
							}
						} else if (newValue.trim().isEmpty()) {
							okButton.setDisable(true);
							qSet = false;
						}
					});
					pheromoneEvaporationText.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.trim().isEmpty()) {
							roSet = true;
							if (allSet()) {
								okButton.setDisable(false);
							}
						} else if (newValue.trim().isEmpty()) {
							okButton.setDisable(true);
							roSet = false;
						}
					});

					dialog.getDialogPane().setContent(grid);

					// request focus on the first text field
					Platform.runLater(() -> numOfAntsText.requestFocus());

					// initialise algorithm values when 'OK' button is clicked
					dialog.setResultConverter(dialogButton -> {
						if (dialogButton == okSetButton) {
							algorithmInitialised = true;

							// set values
							maximumIterations = Integer.parseInt(maximumIterationsText.getText());
							numberOfAnts = Integer.parseInt(numOfAntsText.getText());;
							a = Double.parseDouble(initialPheromoneLevelText.getText());
							alpha = Double.parseDouble(alphaText.getText());
							beta = Double.parseDouble(betaText.getText());
							quantityOfPheromone = Double.parseDouble(pheromoneQuantityText.getText());
							ro = Double.parseDouble(pheromoneEvaporationText.getText());

							gcLayer2.clearRect(0, 0, canvasLayer2.getWidth(), canvasLayer2.getHeight());
							alg = new AntCycleAlgorithm(maximumIterations, numberOfAnts, allCrossroads.size(),
									allCrossroads, a, alpha, beta, quantityOfPheromone, ro);
							return null;
						}
						return null;
					});

					dialog.showAndWait();
				}

				/**
				 * @return true if all values have been set, false otherwise.
				 */
				private boolean allSet() {
					if (antsSet && iterationsSet && aSet && alphaSet && betaSet && qSet && roSet)
						return true;
					else
						return false;
				}
			});

			// step button
			stepButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					// no crossroads chosen
					if (allCrossroads.isEmpty()) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to choose points of interest!");
						alert.setContentText(
								"1) click to add points of interest\n2) click on 'Init'\n3) click on 'Step'");
						alert.showAndWait();
						return;
					}
					// not initialised
					if (!algorithmInitialised) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to initialise algorithm first!");
						alert.setContentText(
								"1) click to add points of interest\n2) click on 'Init'\n3) click on 'Step'");
						alert.showAndWait();
						return;
					}

					gcLayer2.clearRect(0, 0, canvasLayer2.getWidth(), canvasLayer2.getHeight());
					alg.step();
					CrossroadPoint prev = null;
					for (CrossroadPoint p : alg.getBestPath()) {
						if (prev == null) {
							prev = p;
							continue;
						}
						gcLayer2.strokeLine(prev.getCoordinates().getX(), prev.getCoordinates().getY(),
								p.getCoordinates().getX(), p.getCoordinates().getY());
						prev = p;
					}
				}
			});

			// run button
			runButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					// no crossroads chosen
					if (allCrossroads.isEmpty()) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to choose points of interest!");
						alert.setContentText(
								"1) click to add points of interest\n2) click on 'Init'\n3) click on 'Run'");
						alert.showAndWait();
						return;
					}
					// not initialised
					if (!algorithmInitialised) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to initialise algorithm first!");
						alert.setContentText(
								"1) click to add points of interest\n2) click on 'Init'\n3) click on 'Run'");
						alert.showAndWait();
						return;
					}

					for (int i = 1; i <= maximumIterations; i++) {
						gcLayer2.clearRect(0, 0, canvasLayer2.getWidth(), canvasLayer2.getHeight());
						alg.step();
						CrossroadPoint prev = null;
						for (CrossroadPoint p : alg.getBestPath()) {
							if (prev == null) {
								prev = p;
								continue;
							}
							gcLayer2.strokeLine(prev.getCoordinates().getX(), prev.getCoordinates().getY(),
									p.getCoordinates().getX(), p.getCoordinates().getY());
							prev = p;
						}
					}
				}
			});

			// clear button
			clearButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					algorithmInitialised = false;
					gcLayer1.clearRect(0, 0, canvasLayer1.getWidth(), canvasLayer1.getHeight());
					gcLayer2.clearRect(0, 0, canvasLayer2.getWidth(), canvasLayer2.getHeight());
					allCrossroads.clear();
					crossroadPointID = 0;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
