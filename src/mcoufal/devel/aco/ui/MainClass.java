package mcoufal.devel.aco.ui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mcoufal.devel.aco.core.AntColonyAlgorithm;
import mcoufal.devel.aco.core.AntCycleAlgorithm;
import mcoufal.devel.aco.core.CrossroadPoint;

public class MainClass extends Application {
	// logger
	private static final Logger LOG = Logger.getLogger(MainClass.class.getName());

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
	private static int a = 1;
	// impact parameter of pheromone
	private static int alpha = 1;
	// impact parameter of visibility
	private static int beta = 1;
	// pheromone quantity given by each ant per cycle
	private static int quantityOfPheromone = 100;
	// pheromone evaporation parameter
	private static double ro = 0.1;

	// GUI variables

	// crossroad point size
	private int crossroadPointSize = 10;
	// algorithm initialised
	private Boolean algorithmInitialised = false;

	@Override
	public void start(Stage primaryStage) {
		try {
			// set up basic GUI
			VBox root = new VBox();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Ant Colony Optimalization demo");
			primaryStage.show();

			// setup canvas area
			Canvas canvasLayer1 = new Canvas(370,350);
			Canvas canvasLayer2 = new Canvas(370,350);
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
			canvasLayer2.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							algorithmInitialised = false;
							gcLayer1.fillOval(e.getX() - crossroadPointSize / 2, e.getY()  - crossroadPointSize / 2, crossroadPointSize, crossroadPointSize);
							allCrossroads.add(new CrossroadPoint(crossroadPointID, new Point2D.Double(e.getX(), e.getY())));
							crossroadPointID++;
						}
					});

			// init button
			initButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					algorithmInitialised = true;
					gcLayer2.clearRect(0, 0, canvasLayer2.getWidth(), canvasLayer2.getHeight());
					alg = new AntCycleAlgorithm(maximumIterations, numberOfAnts, allCrossroads.size(),
							allCrossroads, a, alpha, beta, quantityOfPheromone, ro);
				}
			});

			// step button
			stepButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					// no crossroads chosen
					if (allCrossroads.isEmpty()) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to choose points of interest!");
						alert.setContentText("1) click to add points of interest\n2) click on 'Init'\n3) click on 'Step'");
						alert.showAndWait();
						return;
					}
					// not initialised
					if (!algorithmInitialised) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to initialise algorithm first!");
						alert.setContentText("1) click to add points of interest\n2) click on 'Init'\n3) click on 'Step'");
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
						gcLayer2.strokeLine(prev.getCoordinates().getX(), prev.getCoordinates().getY(), p.getCoordinates().getX(), p.getCoordinates().getY());
						prev = p;
					}
				}
			});

			// run button
			runButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					// no crossroads chosen
					if (allCrossroads.isEmpty()) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to choose points of interest!");
						alert.setContentText("1) click to add points of interest\n2) click on 'Init'\n3) click on 'Run'");
						alert.showAndWait();
						return;
					}
					// not initialised
					if (!algorithmInitialised ) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("You have to initialise algorithm first!");
						alert.setContentText("1) click to add points of interest\n2) click on 'Init'\n3) click on 'Run'");
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
							gcLayer2.strokeLine(prev.getCoordinates().getX(), prev.getCoordinates().getY(), p.getCoordinates().getX(), p.getCoordinates().getY());
							prev = p;
						}
					}
				}
			});

			// clear button
			clearButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new EventHandler<MouseEvent>() {
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

		// ###########################################################

		AntColonyAlgorithm alg = new AntCycleAlgorithm(maximumIterations, numberOfAnts, allCrossroads.size(),
				allCrossroads, a, alpha, beta, quantityOfPheromone, ro);

		for (int i = 1; i <= 50; i++) {
			alg.step();
			String path = "";
			for (CrossroadPoint p : alg.getBestPath()) {
				path += String.format("[%d]", p.getID());
			}
			System.out.println(
					String.format("[%d] Best solution: %s with length of: %f", i, path, alg.getBestPathLength()));
		}
		// ##########################################################
	}

}
