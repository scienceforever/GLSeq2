package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.cell.CheckBoxTreeCell;

public class AdvancedOptionsTreeCell extends CheckBoxTreeCell<String> {
	public AdvancedOptionsTreeCell() {
		// instantiate the root context menu
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
			setText(item);

			/*
			 * If this item happens to not be a checkbox, let's remove the check
			 * box graphic here.
			 */
			if (!(getTreeItem() instanceof CheckBoxTreeItem)) {
				setGraphic(null);
			}

		} else {
			if (getTreeItem() instanceof SpecialInputTreeItem) {
				SpecialInputTreeItem<String> treeItem = (SpecialInputTreeItem<String>) getTreeItem();
				setGraphic(null);
				String input_type = treeItem.getInputType().toLowerCase();

				/*
				 * Here we handle all the special cases for what types of input
				 * we may want.
				 */
				// Integers
				if (input_type.equals("int")) {
					if (this.getGraphic() == null) {
						Spinner<Integer> intSpinner = new Spinner<Integer>();
						int default_value = (int) Integer.valueOf(treeItem.getDefaultValue());
						/*
						 * Default values imported from SpecialInputTreeItem
						 */

						// Create the factory
						intSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000000));

						intSpinner.valueProperty().addListener(
								(obs, oldValue, newValue) -> treeItem.setDefaultValue(String.valueOf(newValue)));

						intSpinner.focusedProperty().addListener((s, ov, nv) -> intSpinner.getValueFactory()
								.setValue(Integer.valueOf(intSpinner.getEditor().getText())));

						intSpinner.focusedProperty()
								.addListener((s, ov, nv) -> treeItem.setDefaultValue(intSpinner.getEditor().getText()));

						// THEN you can set the value
						intSpinner.getValueFactory().setValue(default_value);
						// We also allow direct editing.
						intSpinner.setEditable(true);
						// Graphics take nodes, spinners are nodes.
						setGraphic(intSpinner);
					}
				}

				// Float
				if (input_type.equals("float")) {
					if (this.getGraphic() == null) {
						Spinner<Double> doubleSpinner = new Spinner<Double>();
						double default_value = (double) Double.valueOf(treeItem.getDefaultValue());
						/*
						 * Default values imported from SpecialInputTreeItem
						 */

						// Create the factory
						DoubleSpinnerValueFactory dblSpinFact = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0,
								1.0);

						dblSpinFact.valueProperty().addListener(
								(obs, oldValue, newValue) -> treeItem.setDefaultValue(String.valueOf(newValue)));
						
						doubleSpinner.focusedProperty().addListener((s, ov, nv) -> doubleSpinner.getValueFactory()
								.setValue(Double.valueOf(doubleSpinner.getEditor().getText())

						));
						
						doubleSpinner.focusedProperty().addListener(
								(s, ov, nv) -> treeItem.setDefaultValue(doubleSpinner.getEditor().getText()));

						dblSpinFact.amountToStepByProperty().set(0.01);
						doubleSpinner.setValueFactory(dblSpinFact);

						// Then you can set the value
						doubleSpinner.getValueFactory().setValue(default_value);
						// We also allow direct editing.
						doubleSpinner.setEditable(true);
						// Graphics take nodes, spinners are nodes.
						setGraphic(doubleSpinner);
					}
				}
				// Boolean
				if (input_type.equals("boolean")) {
					if (this.getGraphic() == null) {
						String default_value = treeItem.getDefaultValue();

						ObservableList<String> options = FXCollections.observableArrayList("True", "False");
						ComboBox<String> combo = new ComboBox<String>(options);
						combo.getSelectionModel().select(default_value);
						combo.valueProperty().addListener(
								(obs, oldValue, newValue) -> treeItem.setDefaultValue(String.valueOf(newValue)));
						combo.focusedProperty().addListener(
								(obs) -> treeItem.setDefaultValue(combo.getSelectionModel().getSelectedItem()));
						;
						setGraphic(combo);
					}
				}

			} else {
				setText(null);
				setGraphic(null);
			}
		}
	}
}
