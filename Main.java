import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

// Person Class
class Person {
    String name;
    Person(String name) {
        this.name = name;
    }
    void showRole(TextArea output) {
        output.appendText("Person Name: " + name + "\n");
    }
}

// Doctor Class
class Doctor extends Person {
    String department;
    Doctor(String name, String department) {
        super(name);
        this.department = department;
    }
    @Override
    void showRole(TextArea output) {
        output.appendText("\n--- Doctor Details ---\n");
        output.appendText("Doctor Name: " + name + "\n");
        output.appendText("Department: " + department + "\n");
    }
}

// Patient Class
class Patient extends Person {
    String disease;
    Patient(String name, String disease) {
        super(name);
        this.disease = disease;
    }
    @Override
    void showRole(TextArea output) {
        output.appendText("\n--- Patient Details ---\n");
        output.appendText("Patient Name: " + name + "\n");
        output.appendText("Disease: " + disease + "\n");
    }
}

// Room Class
class Room {
    int roomNumber;
    Patient patient;
    Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    boolean isEmpty() {
        return patient == null;
    }
}

// Hospital Management Class
class HospitalManagement {
    ArrayList<Room> rooms = new ArrayList<>();
    HospitalManagement(int size) {
        for (int i = 1; i <= size; i++) {
            rooms.add(new Room(i));
        }
    }
    String admitPatient(Patient patient) {
        for (Room room : rooms) {
            if (room.isEmpty()) {
                room.patient = patient;
                return patient.name + " admitted in Room " + room.roomNumber;
            }
        }
        return "No Rooms Available";
    }
    String dischargePatient(String name) {
        for (Room room : rooms) {
            if (!room.isEmpty() && room.patient.name.equals(name)) {
                room.patient = null;
                return name + " discharged successfully";
            }
        }
        return "Patient Not Found";
    }
    String displayRooms() {
        String result = "\n--- Room Details ---\n";
        for (Room room : rooms) {
            if (room.isEmpty()) {
                result += "Room " + room.roomNumber + " Empty\n";
            } else {
                result += "Room " + room.roomNumber + " Occupied by " + room.patient.name + "\n";
            }
        }
        return result;
    }
}

// Main JavaFX Class
public class Main extends Application {

    @Override
    public void start(Stage stage) {

        HospitalManagement h = new HospitalManagement(3);

        Label title = new Label("Hospital Management System");
        TextField patientName = new TextField();
        patientName.setPromptText("Enter Patient Name");
        TextField disease = new TextField();
        disease.setPromptText("Enter Disease");
        Button admitBtn = new Button("Admit Patient");
        Button displayBtn = new Button("Display Rooms");
        Button dischargeBtn = new Button("Discharge Patient");
        TextArea output = new TextArea();

        // Admit Button — Memory + MySQL
        admitBtn.setOnAction(e -> {
            String name = patientName.getText();
            String dis = disease.getText();
            Patient p = new Patient(name, dis);
            String result = h.admitPatient(p);
            output.appendText(result + "\n");

            try {
                Connection con = DatabaseConnection.getConnection();
                String query = "INSERT INTO patients (name, disease) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, dis);
                ps.executeUpdate();
                output.appendText("Saved to Database!\n");
                con.close();
            } catch (Exception ex) {
                output.appendText("DB Error: " + ex.getMessage() + "\n");
            }
        });

        // Display Button
        displayBtn.setOnAction(e -> {
            output.appendText(h.displayRooms());
        });

        // Discharge Button — Memory + MySQL
        dischargeBtn.setOnAction(e -> {
            String name = patientName.getText();
            String result = h.dischargePatient(name);
            output.appendText(result + "\n");

            try {
                Connection con = DatabaseConnection.getConnection();
                String query = "DELETE FROM patients WHERE name = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, name);
                ps.executeUpdate();
                output.appendText("Removed from Database!\n");
                con.close();
            } catch (Exception ex) {
                output.appendText("DB Error: " + ex.getMessage() + "\n");
            }
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(title, patientName, disease, admitBtn, dischargeBtn, displayBtn, output);

        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("Hospital Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}