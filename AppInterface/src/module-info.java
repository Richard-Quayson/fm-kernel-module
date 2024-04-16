module AppInterface {
	requires javafx.controls;
	exports controllers;

	opens application to javafx.graphics, javafx.fxml;
}
