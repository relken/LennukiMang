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

public class Main extends Application {
    final int LAIUS = 1300;
    final int KORGUS = 800;
    int JUHUSLIK(int max, int min){ return ((int) (Math.random()*(max - min))) + min; }
    Animation lennuk1Animatsioon, lennuk2Animatsioon, rakettAnimatsioon, pommAnimatsioon, pommAnimatsioon2;
    Timeline lennuk1Timeline, lennuk2Timeline, lennuk1PommTimeline, lennuk2PommTimeline;
    ProgressBar kahuriEluIndikaator = new ProgressBar();
    IntegerProperty lennukiLoendur = new SimpleIntegerProperty();
    IntegerProperty lasuLoendur = new SimpleIntegerProperty();
    IntegerProperty pommiLoendur = new SimpleIntegerProperty();
    boolean manguSeisund, raketiKontroll;
    final Image TAUST_PILT = new Image (Main.class.getResource("taust.jpg").toString());
    final Image RAKETT = new Image (Main.class.getResource("rakett.png").toString());
    final Image LENNUK1 = new Image (Main.class.getResource("lennuk1.png").toString());
    final Image LENNUK2 = new Image (Main.class.getResource("lennuk2.png").toString());
    final ImageView taust = new ImageView(TAUST_PILT);
    final ImageView rakett = new ImageView(RAKETT);
    double elu;

    @Override
    public void start(Stage lava) throws Exception {
            //luuakse lennukid - mängu põhiobjektid, mida peab tulistama. Allpool meetod nende lendama panemiseks
        Lennuk lennuk1 = new Lennuk(LENNUK1);
        Lennuk lennuk2 = new Lennuk(LENNUK2);
            //mängijapoolne kahur koosneb kahest liikumatust kujundist - kahurikehast, mida peab lennukipommide eest kaitsma
        Shape kahur = new Arc(LAIUS / 2, KORGUS - 50, 50, 50, 0, 180);
        kahur.setFill(Color.DARKGRAY);
            //kahuri teine osa
        Rectangle kahuriKeha = new Rectangle((LAIUS / 2)-110, KORGUS-50, 220, 80);
        kahuriKeha.setArcHeight(25);
        kahuriKeha.setArcWidth(25);
        kahuriKeha.setFill(Color.DARKGRAY);
            //kahuritorust käib lennukite laskmine, allpool meetod toru keeramiseks ja tulistamiseks
        Line kahuriToru = new Line(LAIUS / 2, KORGUS + 50, LAIUS / 2, KORGUS - 150);
        kahuriToru.setFill(Color.BLACK);
        kahuriToru.setStrokeWidth(15);
            //kahuri elu, kuvatakse progressbaril
        elu = 1;
        kahuriEluIndikaator.setProgress(elu);
            //kuigi rakett tekib ekraanile tulistamise hetkel, laetakse vaikimisi siiski ekraanile, vaja eest ära panna
        rakett.setTranslateX(-2000);
        rakett.setTranslateY(-2000);
            //luuakse infoväljad, mis lähevad loodavasse infopaneel
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
        Text label7 = new Text();
        label7.textProperty().bind(Bindings.concat(lennukiLoendur));
            //infopaneel, ekraani vasakule alla
        GridPane infoPaneel = new GridPane();
        infoPaneel.setHgap(10);
        infoPaneel.setVgap(5);
        infoPaneel.setPadding(new Insets(3));
        infoPaneel.add(label1, 0, 0);
        infoPaneel.add(label2, 1, 0);
        infoPaneel.add(label3, 2, 0);
        infoPaneel.add(label4, 3, 0);
        infoPaneel.add(label5, 1, 1);
        infoPaneel.add(label6, 2, 1);
        infoPaneel.add(label7, 0,1);
        infoPaneel.add(kahuriEluIndikaator, 3, 1);
        infoPaneel.setHalignment(label4, HPos.CENTER);
        infoPaneel.setHalignment(label5, HPos.CENTER);
        infoPaneel.setHalignment(label6, HPos.CENTER);
        infoPaneel.setHalignment(label7, HPos.CENTER);
        infoPaneel.setStyle("-fx-background-color: #C0C0C0;");
        infoPaneel.relocate(LAIUS-1250, KORGUS-70);
            //nupp, millega saab mängu pausile panna ja siis edasi mängida
        Button manguSeisNupp = new Button();
        manguSeisNupp.setText("PAUS");
        manguSeisNupp.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 15));
        manguSeisNupp.setPrefSize(170, 50);
        manguSeisNupp.setFocusTraversable(false);
        manguSeisNupp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if(manguSeisund) {
                    mangiMang(manguSeisNupp);
                    }
                else {pausMang(manguSeisNupp);
                    }
                }
            }
        });
            //nupp mängu kinni panemiseks
        Button valjuMangust = new Button();
        valjuMangust.setText("LÕPETA MÄNG");
        valjuMangust.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 15));
        valjuMangust.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });
        valjuMangust.setPrefSize(170, 50);
        valjuMangust.setFocusTraversable(false);
            //nupupaneel alla paremasse nurka
        HBox nupuPaneel = new HBox(5);
        nupuPaneel.getChildren().addAll(manguSeisNupp, valjuMangust);
        nupuPaneel.relocate(LAIUS-360, KORGUS-60);
            //loodud objektid ekraanile
        Pane manguRaam = new Pane(taust, lennuk1, lennuk2, kahuriToru, kahuriKeha, kahur, infoPaneel, nupuPaneel);

        Scene stseen = new Scene(manguRaam, LAIUS, KORGUS);
            //kahuritoru vasakule-paremale keeramiseks
        RotateTransition p88raVasakule = new RotateTransition(Duration.millis(2000), kahuriToru);
        p88raVasakule.setToAngle(-80);
        RotateTransition p88raParemale = new RotateTransition(Duration.millis(2000), kahuriToru);
        p88raParemale.setToAngle(80);
            //seadistatakse klaviatuurinupud kahuritoru keeramiseks ja tulistamiseks
        stseen.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.UP) {
                    if (raketiKontroll == false) {
                        tulistaRakett(manguRaam, kahuriToru);
                        p88raVasakule.stop();
                        p88raParemale.stop();
                    }
                }
                if (ke.getCode() == KeyCode.LEFT) {
                    if (p88raVasakule.getStatus() == Animation.Status.STOPPED) {
                        p88raParemale.stop();
                        p88raVasakule.play();
                    } else {
                        p88raVasakule.stop();
                    }
                }
                if (ke.getCode() == KeyCode.RIGHT) {
                    if (p88raParemale.getStatus() == Animation.Status.STOPPED) {
                        p88raVasakule.stop();
                        p88raParemale.play();
                    } else {
                        p88raVasakule.stop();
                        p88raParemale.stop();
                    }
                }
            }
        });

        lava.setTitle("LennukiMäng");
        lava.setScene(stseen);
        taust.setFitWidth(LAIUS);
        taust.setFitHeight(KORGUS);
        lava.show();
            //meetodid lennukite lennutamiseks, kõige lihtsam oli teha kaks eraldi meetodit
            //kumbagi lennuki jaoks, venitab samas koodi pikemaks.
        lennuk1AnimMeetod(manguRaam, lennuk1, kahur, infoPaneel, kahuriKeha, lava, manguSeisNupp, nupuPaneel);
        lennuk2AnimMeetod(manguRaam, lennuk2, kahur, infoPaneel, kahuriKeha, lava, manguSeisNupp, nupuPaneel);
    }
            //SIIT ALGAVAD MEETODID ISE
    private void tulistaRakett(Pane manguRaam, Line kahuriToru) {
        if (rakettAnimatsioon != null) {
            rakettAnimatsioon.stop();
        }
        raketiKontroll = true;
        rakett.setRotate(kahuriToru.getRotate());
        rakett.setVisible(true);
        manguRaam.getChildren().add(rakett);
        lasuLoendur.set(lasuLoendur.get()+1);
        rakettAnimatsioon = TranslateTransitionBuilder.create()
                .node(rakett)
                .duration(Duration.seconds(1))
                .fromX(koordX(kahuriToru) + (0.42 * kahuriToru.getRotate() - 16)) //lihtsustatud lähenemine, kuidas määrata raketi algkoordinaate
                .fromY(koordY(kahuriToru) - (-0.2 * kahuriToru.getRotate()) - 60) //kahuritoru järgi
                .toX(Math.tan(Math.toRadians(kahuriToru.getRotate())) * ((koordY(kahuriToru))) + LAIUS / 2)
                .toY(0 - rakett.getImage().getWidth() * 2)
                .build();
        rakettAnimatsioon.play();
        rakettAnimatsioon.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rakettAnimatsioon.stop();
                manguRaam.getChildren().remove(rakett);
                raketiKontroll = false;
            }
        });
    }

    public double koordY(Line kahuritoru) {
        Point2D koordinaadid = kahuritoru.localToScene(kahuritoru.getEndX(), kahuritoru.getEndY());
        return koordinaadid.getY();
    }

    public double koordX(Line kahuritoru) {
        Point2D koordinaadid = kahuritoru.localToScene(kahuritoru.getEndX(), kahuritoru.getEndY());
        return koordinaadid.getX();
    }

    private void lennuk1AnimMeetod(Pane manguRaam, ImageView lennuk1, Shape kahur, GridPane infoPaneel, Rectangle kahuriKeha, Stage lava, Button manguSeis, HBox nupuPaneel) {
        if (lennuk1Animatsioon != null) {
            lennuk1Animatsioon.stop();
        }
        if (lennuk1Timeline != null) {
            lennuk1Timeline.stop();
        }

        int pommitusX = JUHUSLIK(1050, 250);
        int y = JUHUSLIK(70, 20);
        int suund = JUHUSLIK(2, 0);
        if (suund ==1) {
            Lennuk.fromX = -100;
            Lennuk.toX = LAIUS+200;
            lennuk1.setScaleX(1);
        }
        else {
            Lennuk.fromX = LAIUS+200;
            Lennuk.toX = -300;
            lennuk1.setScaleX(-1);
        }
            //timelinega kontrollitakse objektide kokkusaamist ning see on ka pommitamissündmuse kaasaaitamiseks
        lennuk1Timeline = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (lennuk1.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            lennuk1Timeline.stop();
                            lennukKuku(manguRaam, lennuk1, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
                        }
                        if (lennuk1.getBoundsInParent().getMaxX() > pommitusX && lennuk1.getBoundsInParent().getMaxX()<pommitusX+8) {
                            lennuk1Timeline.stop();
                            pommita(manguRaam, lennuk1, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
                            lennuk1Timeline.play();
                        }
                    }
                })).build();
        lennuk1Timeline.play();

        lennuk1Animatsioon = TranslateTransitionBuilder.create()
                .node(lennuk1).fromX(Lennuk.fromX)
                .node(lennuk1).toX(Lennuk.toX)
                .fromY(y)
                .toY(y)
                .duration(Duration.seconds(4))
                .node(lennuk1).delay(Duration.seconds(JUHUSLIK(7, 4)))
                .build();
        lennuk1Animatsioon.play();

        lennuk1Animatsioon.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lennuk1AnimMeetod(manguRaam, lennuk1, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
            }
        });
    }

    private void lennuk2AnimMeetod(Pane manguRaam, ImageView lennuk2, Shape kahur, GridPane infoPaneel, Rectangle kahuriKeha, Stage lava, Button manguSeis, HBox nupuPaneel) {
        if (lennuk2Animatsioon != null) {
            lennuk2Animatsioon.stop();
        }
        if (lennuk2Timeline != null) {
            lennuk2Timeline.stop();
        }

        int pommitusX = JUHUSLIK(1050, 250);
        int y = JUHUSLIK(100, 30);
        int suund = JUHUSLIK(2, 0);
        if (suund ==1) {
            Lennuk.fromX = -100;
            Lennuk.toX = LAIUS+200;
            lennuk2.setScaleX(1);
        }
        else {
            Lennuk.fromX = LAIUS+200;
            Lennuk.toX = -300;
            lennuk2.setScaleX(-1);
        }

        lennuk2Timeline = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (lennuk2.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            lennuk2Timeline.stop();
                            lennukKuku2(manguRaam, lennuk2, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
                        }
                        if (lennuk2.getBoundsInParent().getMaxX() > pommitusX && lennuk2.getBoundsInParent().getMaxX()<pommitusX+10) {
                            lennuk2Timeline.stop();
                            pommita2(manguRaam, lennuk2, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
                            lennuk2Timeline.play();
                        }
                    }
                })).build();
        lennuk2Timeline.play();

        lennuk2Animatsioon = TranslateTransitionBuilder.create()
                .node(lennuk2)
                .fromX(Lennuk.fromX)
                .toX(Lennuk.toX)
                .fromY(y)
                .toY(y)
                .duration(Duration.seconds(6))
                .delay(Duration.seconds(JUHUSLIK(15, 10)))
                .onFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        lennuk1Timeline.stop();
                        lennuk2AnimMeetod(manguRaam, lennuk2, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
                    }
                }).build();
        lennuk2Animatsioon.play();
    }

    private void lennukKuku(Pane manguRaam, ImageView lennuk1, Shape kahur, GridPane infoPaneel, Rectangle kahuriKeha, Stage lava, Button manguSeis, HBox nupuPaneel) {
        rakett.setVisible(false);
        lennukiLoendur.set(lennukiLoendur.get()+1);
        Animation lennuk1Kukub = TranslateTransitionBuilder.create()
                .node(lennuk1)
                .byY(1000)
                .duration(Duration.seconds(1))
                .build();
        lennuk1Kukub.play();
        lennuk1Kukub.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lennuk1Kukub.stop();
                lennuk1AnimMeetod(manguRaam, lennuk1, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
            }
        });
    }

    private void lennukKuku2(Pane manguRaam, ImageView lennuk2, Shape kahur, GridPane infoPaneel, Rectangle kahuriKeha, Stage lava, Button manguSeis, HBox nupuPaneel) {
        rakett.setVisible(false);
        lennukiLoendur.set(lennukiLoendur.get()+1);
        Animation lennuk2Kukub = TranslateTransitionBuilder.create()
                .node(lennuk2)
                .byY(1000)
                .duration(Duration.seconds(1.5))
                .build();
        lennuk2Kukub.play();
        lennuk2Kukub.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lennuk2Kukub.stop();
                lennuk2AnimMeetod(manguRaam, lennuk2, kahur, infoPaneel, kahuriKeha, lava, manguSeis, nupuPaneel);
            }
        });
    }

    private void pommita(Pane manguRaam, ImageView lennuk1, Shape kahur, GridPane infoPaneel, Rectangle kahuriKeha, Stage lava, Button manguSeis, HBox nupuPaneel){
        final int pommitusKohtX = JUHUSLIK(900, 300);
        Shape pomm = new Circle(10, Color.BLACK);
        manguRaam.getChildren().add(pomm);
        infoPaneel.toFront();
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
                manguRaam.getChildren().remove(pomm);
            }
        });
        pommAnimatsioon.play();

        lennuk1PommTimeline = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (pomm.getBoundsInParent().intersects(kahur.getBoundsInParent()) ||
                            pomm.getBoundsInParent().intersects(kahuriKeha.getBoundsInParent())) {
                            lennuk1PommTimeline.stop();
                            pomm.setVisible(false);
                            elu = elu - 0.3;
                            kahuriEluIndikaator.setProgress(elu);
                            kasOnMangLabi(manguRaam, lava, manguSeis);
                        }
                        if (pomm.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            lennuk1PommTimeline.stop();
                            pommiLoendur.set(pommiLoendur.get()+1);
                            pomm.setVisible(false);
                            manguRaam.getChildren().remove(pomm);
                            manguRaam.getChildren().remove(rakett);
                        }
                    }
                })).build();
        lennuk1PommTimeline.play();
    }

    private void pommita2(Pane manguRaam, ImageView lennuk2, Shape kahur, GridPane infoPaneel, Rectangle kahuriKeha, Stage lava, Button manguSeis, HBox nupuPaneel){
        final int pommitusKohtX = JUHUSLIK(700, 400);
        Shape pomm2 = new Circle(15, Color.BLACK);
        manguRaam.getChildren().add(pomm2);
        infoPaneel.toFront();
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
                manguRaam.getChildren().remove(pomm2);
            }
        });
        pommAnimatsioon2.play();

        lennuk2PommTimeline = TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (pomm2.getBoundsInParent().intersects(kahur.getBoundsInParent()) ||
                            pomm2.getBoundsInParent().intersects(kahuriKeha.getBoundsInParent())) {
                            lennuk2PommTimeline.stop();
                            pomm2.setVisible(false);
                            elu = elu - 0.4;
                            kahuriEluIndikaator.setProgress(elu);
                            kasOnMangLabi(manguRaam, lava, manguSeis);
                        }
                        if (pomm2.getBoundsInParent().intersects(rakett.getBoundsInParent())) {
                            lennuk2PommTimeline.stop();
                            pommiLoendur.set(pommiLoendur.get()+1);
                            pomm2.setVisible(false);
                            manguRaam.getChildren().remove(pomm2);
                            manguRaam.getChildren().remove(rakett);
                        }
                    }
                })).build();
        lennuk2PommTimeline.play();
    }

    private void kasOnMangLabi(Pane manguRaam, Stage lava, Button manguSeisNupp) {
        if (elu <= 0) {
            pausMang(manguSeisNupp);
            manguSeisNupp.setDisable(true);

            Label mangLabi = new Label("MÄNG LÄBI");
            mangLabi.setFont(Font.font("Serif", FontWeight.BOLD, 150));
            DropShadow vari = new DropShadow(20, Color.DARKGREEN);
            mangLabi.setEffect(vari);

            Button restartNupp = new Button("ALUSTA UUT MÄNGU");
            restartNupp.setPrefSize(300, 50);
            restartNupp.setFont(Font.font(20));
            manguRaam.getChildren().addAll(mangLabi, restartNupp);

            mangLabi.relocate(200, 200);
            restartNupp.relocate(500, 400);

            restartNupp.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    try {
                        start(lava);
                        lennukiLoendur.setValue(0);
                        lasuLoendur.setValue(0);
                        pommiLoendur.setValue(0);
                        elu = 1;
                        }
                    catch (Exception e) {
                        e.printStackTrace();
                        }
                }
            });
        }
    }

    private void pausMang(Button manguSeisNupp) {
        manguSeisNupp.setText("MÄNGI");
        lennuk1Animatsioon.pause();
        lennuk2Animatsioon.pause();

        if (pommAnimatsioon != null && pommAnimatsioon.getStatus() != Animation.Status.PAUSED)
        {pommAnimatsioon.pause();}
        if (pommAnimatsioon2 != null && pommAnimatsioon2.getStatus() != Animation.Status.PAUSED)
        {pommAnimatsioon2.pause();}
        if (rakettAnimatsioon != null && rakettAnimatsioon.getStatus() != Animation.Status.PAUSED)
        {rakettAnimatsioon.pause();}
        if (lennuk1Timeline != null && lennuk1Timeline.getStatus() != Animation.Status.PAUSED)
        {lennuk1Timeline.pause();}
        if (lennuk2Timeline != null && lennuk2Timeline.getStatus() != Animation.Status.PAUSED)
        {lennuk2Timeline.pause();}
        if (lennuk1PommTimeline != null && lennuk1PommTimeline.getStatus() != Animation.Status.PAUSED)
        {lennuk1PommTimeline.pause();}
        if (lennuk2PommTimeline != null && lennuk2PommTimeline.getStatus() != Animation.Status.PAUSED)
        {lennuk2PommTimeline.pause();}

        manguSeisund = true;
    }

    private void mangiMang(Button manguSeisNupp) {
        manguSeisNupp.setText("PAUS");
        lennuk1Animatsioon.play();
        lennuk2Animatsioon.play();

        if (pommAnimatsioon != null && pommAnimatsioon.getStatus() == Animation.Status.PAUSED)
        {pommAnimatsioon.play();}
        if (pommAnimatsioon2 != null && pommAnimatsioon2.getStatus() == Animation.Status.PAUSED)
        {pommAnimatsioon2.play();}
        if (rakettAnimatsioon != null && rakettAnimatsioon.getStatus() == Animation.Status.PAUSED)
        {rakettAnimatsioon.play();}
        if (lennuk1Timeline != null && lennuk1Timeline.getStatus() == Animation.Status.PAUSED)
        {lennuk1Timeline.play();}
        if (lennuk2Timeline != null && lennuk2Timeline.getStatus() == Animation.Status.PAUSED)
        {lennuk2Timeline.pause();}
        if (lennuk1PommTimeline != null && lennuk1PommTimeline.getStatus() == Animation.Status.PAUSED)
        {lennuk1PommTimeline.play();}
        if (lennuk2PommTimeline != null && lennuk2PommTimeline.getStatus() == Animation.Status.PAUSED)
        {lennuk2PommTimeline.play();}

        manguSeisund =false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}



