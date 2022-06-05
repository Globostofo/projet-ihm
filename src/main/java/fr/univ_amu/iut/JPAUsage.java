package fr.univ_amu.iut;

import fr.univ_amu.iut.app_main.LaunchApp;
import fr.univ_amu.iut.model.Academie;
import fr.univ_amu.iut.model.Usage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class JPAUsage extends Tab {
    private static final EntityManagerFactory emf = LaunchApp.emf;
    private static final EntityManager em = LaunchApp.em;
    private TableView<Usage> table;
    private TableColumn<Usage, String> nom;
    private TableColumn<Usage, Integer> id;
    private TableColumn<Usage, String> description;
    private TableColumn<Usage, String> niveau;
    private TableColumn<Usage, Academie> academie;
    private ObservableList<Usage> data;
    private HBox boutons;
    private Button ajouter;
    private Button supprimer;
    private VBox racine;

    public JPAUsage(){
        setText("Administration Usage");
        setClosable(false);
        initialiserTable();
        initialiserBoutons();
        initialiserRacine();
        setContent(racine);
    }
    private void initialiserRacine() {
        racine = new VBox();
        racine.setPadding(new Insets(10));
        racine.getChildren().addAll(table, boutons);
    }

    private void initialiserBoutons() {
        boutons = new HBox(10);
        boutons.setPadding(new Insets(10));
        boutons.setAlignment(Pos.CENTER);
        initialiserBoutonAjouter();
        initialiserBoutonSupprimer();
        boutons.getChildren().addAll(ajouter, supprimer);
    }

    private void initialiserBoutonSupprimer() {
        supprimer = new Button("Supprimer");
        supprimer.setOnAction(this::supprimerUsage);
    }

    private void initialiserBoutonAjouter() {
        ajouter = new Button("Ajouter");
        ajouter.setOnAction(this::ajouterUsage);
    }

    private void initialiserTable() {
        table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().select(0);

        initialiserColonnes();
        insererColonnes();
        remplirDonnees();

        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private static TableColumn<Usage, Integer> initialiserColonneId() {
        TableColumn<Usage, Integer> code = new TableColumn<>("Identifiant");
        code.setCellValueFactory(new PropertyValueFactory<>("id"));
        return code;
    }

    private static TableColumn<Usage, String> initialiserColonneNom() {
        TableColumn<Usage, String> nom = new TableColumn<>("Nom Usage");
        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nom.setCellFactory(TextFieldTableCell.forTableColumn());
        nom.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            Usage Usage = event.getTableView().getItems().get(index);
            em.getTransaction().begin();
            Usage.setNom(event.getNewValue());
            em.getTransaction().commit();
        });
        return nom;
    }

    private static TableColumn<Usage, String> initialiserColonneDescprition() {
        TableColumn<Usage, String> description = new TableColumn<>("Description");
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        description.setCellFactory(TextFieldTableCell.forTableColumn());

        description.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            Usage Usage = event.getTableView().getItems().get(index);
            em.getTransaction().begin();
            Usage.setDescription(event.getNewValue());
            em.getTransaction().commit();
        });

        return description;
    }

    private static TableColumn<Usage, String> initialiserColonneNiveau() {
        TableColumn<Usage, String> niveau = new TableColumn<>("Niveau");
        niveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        niveau.setCellFactory(TextFieldTableCell.forTableColumn());
        niveau.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            Usage Usage = event.getTableView().getItems().get(index);
            em.getTransaction().begin();
            Usage.setNiveau(event.getNewValue());
            em.getTransaction().commit();
        });
        return niveau;
    }

    private TableColumn<Usage, Academie> initialiserColonneCodeAca() {
        TableColumn<Usage, Academie> academie = new TableColumn<>("Code Academique");
        academie.setCellValueFactory(new PropertyValueFactory<>("academie"));
        //academie.setCellFactory(TextFieldTableCell.forTableColumn());
        academie.setOnEditCommit(event -> {
            int index = event.getTablePosition().getRow();
            Usage Usage = event.getTableView().getItems().get(index);
            em.getTransaction().begin();
            Usage.setAcademie(event.getNewValue());
            em.getTransaction().commit();
        });
        return academie;
    }

    private void initialiserColonnes() {
        id = initialiserColonneId();
        nom = initialiserColonneNom();
        description = initialiserColonneDescprition();
        niveau = initialiserColonneNiveau();
        academie = initialiserColonneCodeAca();
    }

    private void insererColonnes() {
        table.getColumns().addAll(List.of(id, nom, description, niveau, academie));
    }

    private void remplirDonnees() {
        data = listerUsages();
        table.setItems(data);
    }

    private ObservableList<Usage> listerUsages() {
        TypedQuery<Usage> query = em.createNamedQuery("Usage.findAll", Usage.class);
        return FXCollections.observableList(query.getResultList());
    }

    private void ajouterUsage(ActionEvent event) {
        Usage Usage = new Usage();
        em.getTransaction().begin();
        em.persist(Usage);
        em.getTransaction().commit();
        data.add(Usage);

        int rowIndex = data.size() - 1;
        table.requestFocus();
        table.scrollTo(rowIndex);
        table.getSelectionModel().select(rowIndex);
        table.getFocusModel().focus(rowIndex);
    }

    private void supprimerUsage(ActionEvent event) {
        if (table.getItems().size() == 0) return;

        em.getTransaction().begin();
        em.remove(table.getSelectionModel().getSelectedItem());
        em.getTransaction().commit();

        int selectedRowIndex = table.getSelectionModel().getSelectedIndex();

        data.remove(selectedRowIndex);

        if (selectedRowIndex != 0) {
            selectedRowIndex = selectedRowIndex - 1;
        }

        table.requestFocus();
        table.scrollTo(selectedRowIndex);
        table.getSelectionModel().select(selectedRowIndex);
        table.getFocusModel().focus(selectedRowIndex);
    }

}

