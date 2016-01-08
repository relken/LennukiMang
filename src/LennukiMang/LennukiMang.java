package LennukiMang;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

import javafx.util.Duration;

public class LennukiMang extends Application {
    private final int LAIUS = 1300;
    private final int KORGUS = 800;
    private static int fromX;
    private static int toX;
    public static int JUHUSLIK(int max, int min){ return ((int) (Math.random()*(max - min))) + min; }
    private Animation current;
    private Animation current2;
    private Animation rakettAnimatsioon;
    private Timeline timeline;
    private Timeline timeline2;
    private Timeline timeline3;
    private Timeline timeline4;
    private Animation pommAnimatsioon;
    private Animation pommAnimatsioon2;
    private double elu;
    boolean manguseisund = false;
    private ProgressBar kahurielu = new ProgressBar();
    IntegerProperty hitcounter = new SimpleIntegerProperty();
    IntegerProperty lasuLoendur = new SimpleIntegerProperty();
    IntegerProperty pommiLoendur = new SimpleIntegerProperty();

    @Override
    public void start(Stage primaryStage) throws Exception {

        final Image TAUST_PILT = new Image (LennukiMang.class.getResource("background.jpg").toString());
        final Image RAKETT = new Image (LennukiMang.class.getResource("rakett.png").toString());
        final Image LENNUK1 = new Image (LennukiMang.class.getResource("plane6.png").toString());
        final Image LENNUK2 = new Image (LennukiMang.class.getResource("plane4.png").toString());

        final ImageView taust = new ImageView(TAUST_PILT);
        ImageView lennuk1 = new ImageView(LENNUK1);
        ImageView lennuk2 = new ImageView(LENNUK2);
        lennuk1.setFitWidth(150);
        lennuk1.setPreserveRatio(true);
        lennuk2.setFitWidth(150);
        lennuk2.setPreserveRatio(true);
        ImageView rakett = new ImageView(RAKETT);
        elu = 1;
        kahurielu.setProgress(elu);
        lennuk1.setTranslateX(-500);
        lennuk1.setTranslateY(-500);
        lennuk2.setTranslateX(-200);
        lennuk2.setTranslateY(-200);
        rakett.setTranslateX(-1000);
        rakett.setTranslateY(-1000);
        Group lennukid;
        lennukid = new Group(lennuk1, lennuk2);
        Shape kahur = new Arc(LAIUS / 2, KORGUS - 50, 50, 50, 0, 180);
        Rectangle kahurikeha = new Rectangle((LAIUS / 2)-110, KORGUS-50, 220, 80);
        kahurikeha.setArcHeight(25);
        kahurikeha.setArcWidth(25);

        Line kahuritoru = new Line(LAIUS / 2, 850, LAIUS / 2, 650);
        Group kahurkokku = new Group(kahuritoru, kahurikeha, kahur);
        kahuritoru.setFill(Color.BLACK);
        kahuritoru.setStrokeType(StrokeType.CENTERED);
        kahuritoru.setStrokeWidth(15);
        kahur.setFill(Color.DARKGRAY);
        kahurikeha.setFill(Color.DARKGRAY);
        kahurielu.setLayoutX(LAIUS-300);
        kahurielu.setLayoutY(KORGUS-30);
        Text hitLabel = new Text();
        hitLabel.textProperty().bind(Bindings.concat(hitcounter));
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(3));
        TextBuilder label = TextBuilder.create()
                .font(Font.font("Verdana", FontWeight.BOLD, 12))
                .textAlignment(TextAlignment.CENTER);
        Text label1 = label.text("Allatulistatud \nlennukid").build();
        Text label2 = label.text("Sooritatud \nlasud").build();
        Text label3 = label.text("Kahjutuks tehtud \npommid").build();
        Text label4 = label.text("Kahuri \nelu").build();
        Text label5 = new Text();
        label5.textProperty().bind(Bindings.concat(lasuLoendur));
        Text label6 = new Text();
        label6.textProperty().bind(Bindings.concat(pommiLoendur));
        grid.add(label1, 0, 0);
        grid.add(label2, 1, 0);
        grid.add(label3, 2, 0);
        grid.add(label4, 3, 0);
        grid.add(hitLabel, 0,1);
        grid.add(kahurielu, 3, 1);
        grid.add(label5, 1, 1);
        grid.add(label6, 2, 1);
        grid.relocate(LAIUS-1250, KORGUS-70);
        grid.setHalignment(hitLabel, HPos.CENTER);
        grid.setHalignment(label5, HPos.CENTER);
        grid.setHalignment(label4, HPos.CENTER);
        grid.setHalignment(label6, HPos.CENTER);
        grid.setStyle("-fx-background-color: #C0C0C0;");
        //grid.setGridLinesVisible(true);
        HBox nupuPaneel = new HBox(5);

        Button manguSeis = new Button();
        manguSeis.setText("PAUS");
        manguSeis.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                if (e.getButton() == MouseButton.PRIMARY)
                { if(manguseisund==false) {
                    pausMang(manguSeis);
                }
                else if (manguseisund=true){
                    mangiMang(manguSeis);
                }
                }
            }
        });


        Button valjuMangust = new Button();
        valjuMangust.setText("LÕPETA MÄNG");
        valjuMangust.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 15));
        manguSeis.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 15));
        valjuMangust.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });
        manguSeis.setPrefSize(170, 50);
        valjuMangust.setPrefSize(170, 50);

        nupuPaneel.getChildren().addAll(manguSeis, valjuMangust);
        nupuPaneel.relocate(LAIUS-360, KORGUS-60);

        manguSeis.setFocusTraversable(false);
        valjuMangust.setFocusTraversable(false);

        Pane raam = new Pane(taust, lennukid, kahurkokku, grid, nupuPaneel);

        Scene stseen = new Scene(raam, LAIUS, KORGUS);

        RotateTransition rotate = new RotateTransition(Duration.millis(2000), kahuritoru);
        rotate.setToAngle(-80);
        RotateTransition rotate2 = new RotateTransition(Duration.millis(2000), kahuritoru);
        rotate2.setToAngle(80);
        stseen.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke1) {
                if (ke1.getCode() == KeyCode.UP) {
                    tulistaRakett(raam, kahuritoru, rakett);
                    rotate.stop();
                    rotate2.stop();
                }
                if (ke1.getCode() == KeyCode.LEFT) {
                    if (rotate.getStatus() == Animation.Status.STOPPED) {
                        rotate2.stop();
                        rotate.play();
                    } else {
                        rotate.stop();
                    }
                }
                if (ke1.getCode() == KeyCode.RIGHT) {
                    if (rotate2.getStatus() == Animation.Status.STOPPED) {
                        rotate.stop();
                        rotate2.play();
                    } else {
                        rotate.stop();
                        rotate2.stop();
                    }
                }
            }
        });
        raam.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(kahur);
            }
        });



        primaryStage.setTitle("LennukiMäng");
        primaryStage.setScene(stseen);
        taust.setFitWidth(LAIUS);
        taust.setFitHeight(KORGUS);
        primaryStage.show();
        startAnimation(raam, lennuk1, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
        startAnimation2(raam, lennuk2, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);


    }

    private void tulistaRakett(final Pane raam, final Line kahuritoru, ImageView rakett) {
        if (rakettAnimatsioon != null) {
            rakettAnimatsioon.stop();
        }
        rakett.setRotate(kahuritoru.getRotate());
        rakett.setVisible(true);
        raam.getChildren().add(rakett);
        lasuLoendur.set(lasuLoendur.get()+1);
        rakett.setTranslateX(-5000);
        rakett.setTranslateY(-5000);
        rakettAnimatsioon = TranslateTransitionBuilder.create()
                .node(rakett)
                .duration(Duration.seconds(1))
                .fromX(koordX(kahuritoru) + (0.42 * kahuritoru.getRotate() - 16))
                .fromY(koordY(kahuritoru) - (-0.2 * kahuritoru.getRotate()) - 60)
                .toX(Math.tan(Math.toRadians(kahuritoru.getRotate())) * ((koordY(kahuritoru))) + LAIUS / 2)
                .toY(0 - rakett.getImage().getWidth() * 2)
                .build();
        rakettAnimatsioon.play();
        rakettAnimatsioon.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rakettAnimatsioon.stop();
                raam.getChildren().remove(rakett);
            }
        });

    }
    public double koordY(Line kahuritoru) {
        Point2D aa = kahuritoru.localToScene(kahuritoru.getEndX(), kahuritoru.getEndY());
        double y = aa.getY();
        return y;
    }
    public double koordX(Line kahuritoru) {
        Point2D aa = kahuritoru.localToScene(kahuritoru.getEndX(), kahuritoru.getEndY());
        double x = aa.getX();
        return x;
    }

    private void startAnimation(Pane raam, ImageView lennuk1, ImageView rakett, Shape kahur, GridPane grid, Rectangle kahurikeha,  Stage primaryStage, Button manguSeis, HBox nupuPaneel) {
        if (current != null) {
            current.stop();
        }
        if (timeline != null) {
            timeline.stop();
        }

        final int pommitusX = JUHUSLIK(1050, 250);
        final int y = JUHUSLIK(70, 20);
        int suund = JUHUSLIK(2, 0);
        if (suund ==1) {
            fromX = -100;
            toX = LAIUS+200;
            lennuk1.setScaleX(1);
        }
        else {
            fromX = LAIUS+200;
            toX = -300;
            lennuk1.setScaleX(-1);
        }
        timeline = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (lennuk1.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            timeline.stop();
                            lennukKuku(raam, lennuk1, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
                        }
                        if (lennuk1.getBoundsInParent().getMaxX() > pommitusX && lennuk1.getBoundsInParent().getMaxX()<pommitusX+8) {
                            timeline.stop();
                            pommita(raam, lennuk1, kahur, rakett, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
                            timeline.play();
                        }
                    }})).build();
        timeline.play();

        current = TranslateTransitionBuilder.create()
                .node(lennuk1).fromX(fromX)
                .node(lennuk1).toX(toX)
                .fromY(y)
                .toY(y)
                .duration(Duration.seconds(4))
                .node(lennuk1).delay(Duration.seconds(JUHUSLIK(7, 4)))
                .build();
        current.play();

        current.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startAnimation(raam, lennuk1, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
            }
        });
    }
    private void startAnimation2(Pane raam, ImageView lennuk2, ImageView rakett, Shape kahur, GridPane grid, Rectangle kahurikeha,  Stage primaryStage, Button manguSeis, HBox nupuPaneel) {
        if (current2 != null) {
            current2.stop();
        }
        if (timeline2 != null) {
            timeline2.stop();
        }

        final int pommitusX = JUHUSLIK(1050, 250);
        final int y = JUHUSLIK(100, 30);
        int suund = JUHUSLIK(2, 0);
        if (suund ==1) {
            fromX = -100;
            toX = LAIUS+200;
            lennuk2.setScaleX(1);
        }
        else {
            fromX = LAIUS+200;
            toX = -300;
            lennuk2.setScaleX(-1);
        }

        timeline2 = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (lennuk2.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            timeline2.stop();
                            lennukKuku2(raam, lennuk2, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
                        }
                        if (lennuk2.getBoundsInParent().getMaxX() > pommitusX && lennuk2.getBoundsInParent().getMaxX()<pommitusX+10) {
                            timeline2.stop();
                            pommita2(raam, lennuk2, kahur, rakett, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
                            timeline2.play();
                        }}})).build();
        timeline2.play();

        current2 = TranslateTransitionBuilder.create()
                .node(lennuk2)
                .fromX(fromX)
                .toX(toX)
                .fromY(y)
                .toY(y)
                .duration(Duration.seconds(6))
                .delay(Duration.seconds(JUHUSLIK(15, 10)))
                .onFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        timeline.stop();
                        startAnimation2(raam, lennuk2, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
                    }
                }).build();
        current2.play();
    }

    private void lennukKuku(Pane raam, ImageView lennuk1, ImageView rakett, Shape kahur, GridPane grid, Rectangle kahurikeha,  Stage primaryStage, Button manguSeis, HBox nupuPaneel) {
        rakett.setVisible(false);
        hitcounter.set(hitcounter.get()+1);
        Animation test2 = TranslateTransitionBuilder.create()
                .node(lennuk1)
                .byY(1000)
                .duration(Duration.seconds(1))
                .build();
        test2.play();
        test2.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                test2.stop();
                startAnimation(raam, lennuk1, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
            }
        });
    }
    private void lennukKuku2(Pane raam, ImageView lennuk2, ImageView rakett, Shape kahur, GridPane grid, Rectangle kahurikeha, Stage primaryStage, Button manguSeis, HBox nupuPaneel) {
        rakett.setVisible(false);
        hitcounter.set(hitcounter.get()+1);
        Animation test3 = TranslateTransitionBuilder.create()
                .node(lennuk2)
                .byY(1000)
                .duration(Duration.seconds(1.5))
                .build();
        test3.play();

        test3.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                test3.stop();
                startAnimation2(raam, lennuk2, rakett, kahur, grid, kahurikeha, primaryStage, manguSeis, nupuPaneel);
            }
        });
    }
    private void pommita(Pane raam, ImageView lennuk1, Shape kahur, ImageView rakett, GridPane grid, Rectangle kahurikeha, Stage primaryStage, Button manguSeis, HBox nupuPaneel){
        final int pommitusKohtX = JUHUSLIK(900, 300);
        System.out.println(pommitusKohtX);
        Shape pomm = new Circle(10, Color.BLACK);
        raam.getChildren().add(pomm);
        grid.toFront();
        nupuPaneel.toFront();
        pommAnimatsioon = TranslateTransitionBuilder.create()
                .node(pomm)
                .duration(Duration.seconds(5))
                .fromX((lennuk1.getBoundsInParent().getMaxX()+lennuk1.getBoundsInParent().getMinX())/2)
                .fromY(lennuk1.getBoundsInParent().getMinY()+100)
                .toX(pommitusKohtX)
                .toY(KORGUS-30)
                .build();
        pommAnimatsioon.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pommAnimatsioon.stop();
                raam.getChildren().remove(pomm);
            }
        });
        pommAnimatsioon.play();

        timeline3 = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (pomm.getBoundsInParent().intersects(kahur.getBoundsInParent()) ||
                            pomm.getBoundsInParent().intersects(kahurikeha.getBoundsInParent())) {
                            timeline3.stop();
                            pomm.setVisible(false);
                            elu = elu - 0.3;
                            kahurielu.setProgress(elu);
                            System.out.println(elu);
                            kasOnMangLabi(raam, primaryStage, manguSeis);
                        }
                        if (pomm.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            timeline3.stop();
                            pommiLoendur.set(pommiLoendur.get()+1);
                            pomm.setVisible(false);
                            raam.getChildren().remove(pomm);
                            raam.getChildren().remove(rakett);
                        }
                    }})).build();
        timeline3.play();

    }
    private void pommita2(Pane raam, ImageView lennuk2, Shape kahur, ImageView rakett, GridPane grid, Rectangle kahurikeha, Stage primaryStage, Button manguSeis, HBox nupuPaneel){
        final int pommitusKohtX = JUHUSLIK(700, 400);
        System.out.println(pommitusKohtX);
        Shape pomm2 = new Circle(15, Color.BLACK);
        raam.getChildren().add(pomm2);
        grid.toFront();
        nupuPaneel.toFront();
        pommAnimatsioon2 = TranslateTransitionBuilder.create()
                .node(pomm2)
                .duration(Duration.seconds(8))
                .fromX((lennuk2.getBoundsInParent().getMaxX()+lennuk2.getBoundsInParent().getMinX())/2)
                .fromY(lennuk2.getBoundsInParent().getMinY()+100)
                .toX(pommitusKohtX)
                .toY(KORGUS-30)
                .build();
        pommAnimatsioon2.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pommAnimatsioon2.stop();
                raam.getChildren().remove(pomm2);
            }
        });
        pommAnimatsioon2.play();
        timeline4 = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (pomm2.getBoundsInParent().intersects(kahur.getBoundsInParent()) ||
                            pomm2.getBoundsInParent().intersects(kahurikeha.getBoundsInParent())) {
                            timeline4.stop();
                            pomm2.setVisible(false);
                            elu = elu - 0.4;
                            kahurielu.setProgress(elu);
                            System.out.println(kahurielu.progressProperty());
                            System.out.println(elu);
                            kasOnMangLabi(raam, primaryStage, manguSeis);
                        }
                        if (pomm2.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            timeline4.stop();
                            pommiLoendur.set(pommiLoendur.get()+1);
                            pomm2.setVisible(false);
                            raam.getChildren().remove(pomm2);
                            raam.getChildren().remove(rakett);
                        }
                    }})).build();
        timeline4.play();
    }
    private void kasOnMangLabi(Pane raam, Stage primaryStage, Button manguSeis) {
        if (elu <= 0) {
            pausMang(manguSeis);
            Label mangLabi = new Label("MÄNG LÄBI");
            mangLabi.setFont(Font.font("Serif", FontWeight.BOLD, 150));
            DropShadow vari = new DropShadow(20, Color.DARKGREEN);
            mangLabi.setEffect(vari);
            Button restartNupp = new Button("ALUSTA UUT MÄNGU");
            restartNupp.setPrefSize(300, 50);
            restartNupp.setFont(Font.font(20));
            raam.getChildren().addAll(mangLabi, restartNupp);
            mangLabi.relocate(200, 200);
            restartNupp.relocate(500, 400);
            manguSeis.setDisable(true);
            restartNupp.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    try {
                        start(primaryStage);
                        hitcounter.setValue(0);
                        lasuLoendur.setValue(0);
                        pommiLoendur.setValue(0);
                        elu = 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private void pausMang(Button manguSeis) {
        current.pause();
        current2.pause();
        manguSeis.setText("MÄNGI");
        if (pommAnimatsioon != null && pommAnimatsioon.getStatus() != Animation.Status.PAUSED)
        {pommAnimatsioon.pause();}
        if (pommAnimatsioon2 != null && pommAnimatsioon2.getStatus() != Animation.Status.PAUSED)
        {pommAnimatsioon2.pause();}
        if (rakettAnimatsioon != null && rakettAnimatsioon.getStatus() != Animation.Status.PAUSED)
        {rakettAnimatsioon.pause();}
        if (timeline != null && timeline.getStatus() != Animation.Status.PAUSED)
        {timeline.pause();}
        if (timeline2 != null && timeline2.getStatus() != Animation.Status.PAUSED)
        {timeline2.pause();}
        if (timeline3 != null && timeline3.getStatus() != Animation.Status.PAUSED)
        {timeline3.pause();}
        if (timeline4 != null && timeline4.getStatus() != Animation.Status.PAUSED)
        {timeline4.pause();}
        manguseisund = true;
    }
    private void mangiMang(Button manguSeis) {
        manguSeis.setText("PAUS");
        current.play();
        current2.play();
        if (pommAnimatsioon != null && pommAnimatsioon.getStatus() == Animation.Status.PAUSED)
        {pommAnimatsioon.play();}
        if (pommAnimatsioon2 != null && pommAnimatsioon2.getStatus() == Animation.Status.PAUSED)
        {pommAnimatsioon2.play();}
        if (rakettAnimatsioon != null && rakettAnimatsioon.getStatus() == Animation.Status.PAUSED)
        {rakettAnimatsioon.play();}
        if (timeline != null && timeline.getStatus() == Animation.Status.PAUSED)
        {timeline.play();}
        if (timeline2 != null && timeline2.getStatus() == Animation.Status.PAUSED)
        {timeline2.pause();}
        if (timeline3 != null && timeline3.getStatus() == Animation.Status.PAUSED)
        {timeline3.play();}
        if (timeline4 != null && timeline4.getStatus() == Animation.Status.PAUSED)
        {timeline4.play();}
        manguseisund=false;
    }




    public static void main(String[] args) {
        launch(args);
    }


}



