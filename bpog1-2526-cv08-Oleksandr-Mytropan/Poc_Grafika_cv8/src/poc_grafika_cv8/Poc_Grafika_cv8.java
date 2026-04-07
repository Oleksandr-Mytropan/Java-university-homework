package poc_grafika_cv8;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.MouseEvent;

public class Poc_Grafika_cv8 extends Application {

    private double startX, startY;
    private double moveStartX, moveStartY;

    private Shape tempShape = null;
    private boolean drawing = false;

    @Override
    public void start(Stage stage) {

        VBox root = new VBox(10);

        ColorPicker colorPicker = new ColorPicker(Color.RED);

        ChoiceBox<String> shapeChoice = new ChoiceBox<>();
        shapeChoice.getItems().addAll("Rectangle", "Circle", "Polygon");
        shapeChoice.setValue("Rectangle");

        Slider sizeSlider = new Slider(10, 100, 30);
        sizeSlider.setShowTickLabels(true);

        Label coords = new Label("X: 0 Y: 0");

        Pane pane = new Pane();
        pane.setPrefSize(800, 600);

        List<Shape> shapes = new ArrayList<>();

        root.getChildren().addAll(
                new Label("Color:"), colorPicker,
                new Label("Shape:"), shapeChoice,
                new Label("Size:"), sizeSlider,
                coords,
                pane
        );

        pane.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            coords.setText("X: " + e.getX() + " Y: " + e.getY());
        });

        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {

            if (e.getButton() == MouseButton.PRIMARY) {

                startX = e.getX();
                startY = e.getY();
                drawing = true;

                String shapeType = shapeChoice.getValue();
                Color color = colorPicker.getValue();

                if (shapeType.equals("Rectangle")) {
                    Rectangle r = new Rectangle(startX, startY, 1, 1);
                    r.setFill(color);
                    tempShape = r;
                    pane.getChildren().add(r);
                    shapes.add(r);

                } else if (shapeType.equals("Polygon")) {
                    Polygon p = new Polygon();
                    p.getPoints().addAll(
                            startX, startY,
                            startX, startY,
                            startX, startY
                    );
                    p.setFill(color);
                    tempShape = p;
                    pane.getChildren().add(p);
                    shapes.add(p);

                } else {
                    Circle c = new Circle(startX, startY, 1);
                    c.setFill(color);
                    tempShape = c;
                    pane.getChildren().add(c);
                    shapes.add(c);
                }
            }

            if (e.getButton() == MouseButton.MIDDLE && e.getTarget() instanceof Shape s) {
                tempShape = s;
                moveStartX = e.getX();
                moveStartY = e.getY();
            }

            if (e.getButton() == MouseButton.SECONDARY) {
                Shape nearest = null;
                double minDist = Double.MAX_VALUE;

                for (Shape s : shapes) {
                    double cx = 0, cy = 0;

                    if (s instanceof Rectangle r) {
                        cx = r.getX() + r.getWidth() / 2;
                        cy = r.getY() + r.getHeight() / 2;
                    } else if (s instanceof Circle c) {
                        cx = c.getCenterX();
                        cy = c.getCenterY();
                    } else if (s instanceof Polygon p) {
                        for (int i = 0; i < p.getPoints().size(); i += 2) {
                            cx = p.getPoints().get(i);
                            cy = p.getPoints().get(i + 1);
                        }
                    }

                    double dist = Math.hypot(cx - e.getX(), cy - e.getY());

                    if (dist < minDist) {
                        minDist = dist;
                        nearest = s;
                    }
                }

                if (nearest != null) {
                    pane.getChildren().remove(nearest);
                    shapes.remove(nearest);
                }
            }
        });

        pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {

            if (drawing && tempShape != null) {

                double width = Math.abs(e.getX() - startX);
                double height = Math.abs(e.getY() - startY);

                if (tempShape instanceof Rectangle r) {
                    r.setX(Math.min(startX, e.getX()));
                    r.setY(Math.min(startY, e.getY()));
                    r.setWidth(width);
                    r.setHeight(height);

                } else if (tempShape instanceof Circle c) {
                    c.setRadius(Math.max(width, height));

                } else if (tempShape instanceof Polygon p) {
                    double radius = Math.hypot(e.getX() - startX, e.getY() - startY);
                    p.getPoints().clear();
                    for (int i = 0; i < 6; i++) {
                        double angle = Math.toRadians(60 * i - 30);
                        p.getPoints().addAll(
                                startX + radius * Math.cos(angle),
                                startY + radius * Math.sin(angle)
                        );
                    }
                }
            }

            if (!drawing && tempShape != null && e.getButton() == MouseButton.MIDDLE) {
                double dx = e.getX() - moveStartX;
                double dy = e.getY() - moveStartY;

                if (tempShape instanceof Rectangle r) {
                    r.setX(r.getX() + dx);
                    r.setY(r.getY() + dy);

                } else if (tempShape instanceof Circle c) {
                    c.setCenterX(c.getCenterX() + dx);
                    c.setCenterY(c.getCenterY() + dy);

                } else if (tempShape instanceof Polygon p) {
                    for (int i = 0; i < p.getPoints().size(); i += 2) {
                        p.getPoints().set(i, p.getPoints().get(i) + dx);
                        p.getPoints().set(i + 1, p.getPoints().get(i + 1) + dy);
                    }
                }

                moveStartX = e.getX();
                moveStartY = e.getY();
            }
        });

        pane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            drawing = false;
            tempShape = null;
        });

        Scene scene = new Scene(root, 800, 700);
        stage.setTitle("Drawing App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
