package fr.univ_amu.iut.contenu.thematiques;

import fr.univ_amu.iut.app_main.LaunchApp;
import fr.univ_amu.iut.components.BoutonThematique;
import fr.univ_amu.iut.contenu.thematique.ThematiqueOnglet;
import fr.univ_amu.iut.model.Thematique;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ThematiquesContenuControl extends FlowPane {

    private Tab parentTab;

    EventHandler<ActionEvent> ecouteur = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            //Code de l'écouteur d'événement
            TabPane tabPane = parentTab.getTabPane();
            tabPane.getTabs().add(new ThematiqueOnglet(((BoutonThematique)event.getSource()).getThematique()));
        }
    };

    public ThematiquesContenuControl(Tab parentTab){
        this.parentTab = parentTab;
        initButtons();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ThematiquesContenuView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setBackground(new Background(new BackgroundFill(Color.web("#a3d7f7"), new CornerRadii(0), Insets.EMPTY)));
    }

    public void initButtons(){
        TypedQuery<Thematique> query = LaunchApp.em.createNamedQuery("Thematique.findAll", Thematique.class);
        for (Thematique thematique : FXCollections.observableList(query.getResultList())) {
            BoutonThematique bt = new BoutonThematique(thematique);
            bt.setPrefSize(200, 100);
            bt.setOnAction(ecouteur);
            bt.onMouseEnteredProperty().set((EventHandler<MouseEvent>) mouseEvent -> bt.setStyle("-fx-background-color: #62b4bf"));
            bt.onMouseExitedProperty().set((EventHandler<MouseEvent>) mouseEvent -> bt.setStyle("-fx-background-color: #c8f4fa"));
            getChildren().add(bt);
        }
    }
}