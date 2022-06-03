package fr.univ_amu.iut.contenu.thematique;

import fr.univ_amu.iut.app_main.LaunchApp;
import fr.univ_amu.iut.model.Academie;
import fr.univ_amu.iut.model.Discipline;
import fr.univ_amu.iut.model.Thematique;
import fr.univ_amu.iut.model.Usage;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class ThematiqueContenuControl extends TableView<Usage> {

    private Tab parentTab;

    public ThematiqueContenuControl(Tab parentTab, Thematique thematique){
        System.out.println(thematique);
        this.parentTab = parentTab;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ThematiqueContenuView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        remplirTable(thematique);
    }

    private void remplirTable(Thematique thematique) {
        TableColumn<Usage, String> nom = new TableColumn<>("Intitulé");
        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Usage, Discipline> discipline = new TableColumn<>("Discipline");
        discipline.setCellValueFactory(new PropertyValueFactory<>("discipline"));

        TableColumn<Usage, Academie> academie = new TableColumn<>("Académie");
        academie.setCellValueFactory(new PropertyValueFactory<>("academie"));

        TableColumn<Usage, String> niveau = new TableColumn<>("Niveau");
        niveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        // TODO colonne bouton et peut être favoris

        getColumns().addAll(List.of(nom, discipline, academie, niveau));

        TypedQuery<Usage> query = LaunchApp.em.createNamedQuery("Usage.findByThematique", Usage.class);
        query.setParameter("thematique", thematique);
        setItems(FXCollections.observableList(query.getResultList()));

    }

}