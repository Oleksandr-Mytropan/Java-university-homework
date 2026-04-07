package poc_grafika_cv6;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.converter.IntegerStringConverter;

public class FXMLController {

    @FXML
    private TableView<Student> table;

    @FXML
    private TableColumn<Student, Integer> colId;

    @FXML
    private TableColumn<Student, String> colJmeno;

    @FXML
    private TableColumn<Student, String> colPrijmeni;
    @FXML
    private TableColumn<Student, Integer> colRocnik;

    @FXML
    private PieChart pieChart;

    private ObservableList<Student> seznamStudentu = FXCollections.observableArrayList();

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colJmeno.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
        colPrijmeni.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
        colRocnik.setCellValueFactory(new PropertyValueFactory<>("rocnik"));

        table.setItems(seznamStudentu);

        table.setEditable(true);

        colId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colJmeno.setCellFactory(TextFieldTableCell.forTableColumn());
        colPrijmeni.setCellFactory(TextFieldTableCell.forTableColumn());
        colRocnik.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colId.setOnEditCommit(el -> {
            el.getRowValue().setId(el.getNewValue());
            pieChanged();
        });
        colJmeno.setOnEditCommit(el -> {
            el.getRowValue().setJmeno(el.getNewValue());
            pieChanged();
        });
        colPrijmeni.setOnEditCommit(el -> {
            el.getRowValue().setPrijmeni(el.getNewValue());
            pieChanged();
        });
        colRocnik.setOnEditCommit(el -> {
            el.getRowValue().setRocnik(el.getNewValue());
            pieChanged();
        });

        seznamStudentu.addAll(new Student(1, "Petr", "Novotný", 1),
                new Student(2, "Roman", "Kučera", 2),
                new Student(3, "Dana", "Křížová", 2));
        pieChanged();
    }

    @FXML
    private void addStudent() {
        seznamStudentu.add(new Student(0, "jmeno", "prijmeni", 0));

        pieChanged();
    }

    @FXML
    private void removeStudent() {
        Student s = table.getSelectionModel().getSelectedItem();

        if (s != null) {
            seznamStudentu.remove(s);
        }
        pieChanged();
    }

    private int countOfRocnik(int r) {
        int count = 0;
        for (Student student : seznamStudentu) {
            if (student.getRocnik() == r) {
                count++;
            }
        }
        return count;
    }

    @FXML
    private void pieChanged() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        int pocet;
        for (int i = 1; i < 6; i++) {
            pocet = countOfRocnik(i);
            pieChartData.add(new Data(
                    String.format("%d. ročník [%d]", i, pocet),
                    pocet)
            );
        }

        pieChart.setData(pieChartData);
    }
}
