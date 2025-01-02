package com.accounting.main;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.converter.DoubleStringConverter;

public class AccountingPanel extends VBox {

	private TableView<Account> assetsTable;
	private TableView<Account> equityLiabilitiesTable;
	private ObservableList<Account> allAccounts; // Merged list of default and draggable accounts
	private ObservableList<Account> assetsData;
	private ObservableList<Account> equityLiabilitiesData;
	private Label assetsTotalLabel;
	private Label equityLiabilitiesTotalLabel;
	private Button validateButton;

	public AccountingPanel(ObservableList<Account> defaultAccounts, ObservableList<Account> draggableAccounts) {
		// Merge default accounts and draggable accounts into one list
		this.allAccounts = FXCollections.observableArrayList();
		this.allAccounts.addAll(defaultAccounts);
		this.allAccounts.addAll(draggableAccounts);

		// Separate accounts into Assets and Equity & Liabilities based on type
		this.assetsData = FXCollections.observableArrayList();
		this.equityLiabilitiesData = FXCollections.observableArrayList();

		// Initialize components
		this.assetsTable = createTableView("Assets", assetsData);
		this.equityLiabilitiesTable = createTableView("Equity & Liabilities", equityLiabilitiesData);
		FlowPane draggableItemsPane = createDraggablePane(draggableAccounts);


		assetsTotalLabel = new Label("Total A: 0");
		equityLiabilitiesTotalLabel = new Label("Total E&L: 0");
		assetsTotalLabel.setFont(new Font(16));
		equityLiabilitiesTotalLabel.setFont(new Font(16));

		this.validateButton = new Button("Validate");

		// Layout configuration
		setSpacing(10);
		setPadding(new Insets(10));

		HBox tablesBox = new HBox(10, new VBox(new Label("Assets"), assetsTable, assetsTotalLabel),
				new VBox(new Label("Equity & Liabilities"), equityLiabilitiesTable, equityLiabilitiesTotalLabel));
		tablesBox.setPadding(new Insets(10));

		getChildren().addAll(tablesBox, draggableItemsPane, validateButton);

		// Button action for validation
		validateButton.setOnAction(e -> validateBalanceSheet());

		assetsData.addListener((ListChangeListener<Account>) change -> updateTotals());
		equityLiabilitiesData.addListener((ListChangeListener<Account>) change -> updateTotals());

		// Populate tables with default accounts initially
		populateDefaultAccounts(defaultAccounts);
		
		getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());

	}

	private TableView<Account> createTableView(String tableTitle, ObservableList<Account> tableData) {
		
		TableView<Account> table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		table.setItems(tableData);

		TableColumn<Account, String> accountNameCol = new TableColumn<>("Account Name");
		accountNameCol.setCellValueFactory(data -> data.getValue().accountNameProperty());

		TableColumn<Account, Double> valueCol = new TableColumn<>("Value");
		valueCol.setCellValueFactory(data -> data.getValue().valueProperty().asObject());
		valueCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter())); // Editable cell
		valueCol.setOnEditCommit(event -> {
			Account account = event.getRowValue();
			account.setValue(event.getNewValue());
			updateTotals(); // Update totals after editing a value
		});

		table.getColumns().addAll(accountNameCol, valueCol);
		table.setPrefHeight(300);
		table.setPrefWidth(300);
		table.setEditable(true); // Make the table editable

		// Add delete functionality for non-default rows
		table.setRowFactory(tv -> {
			TableRow<Account> row = new TableRow<>();
			row.setOnContextMenuRequested(event -> {
				if (!row.isEmpty()) {
					ContextMenu contextMenu = new ContextMenu();
					MenuItem deleteItem = new MenuItem("Delete");
					deleteItem.setOnAction(e -> {
						Account account = row.getItem();
						tableData.remove(account); // Remove account from the table
						addDraggableLabel((FlowPane) getChildren().get(1), account); // Restore draggable account
					});
					contextMenu.getItems().add(deleteItem);
					contextMenu.show(row, event.getScreenX(), event.getScreenY());
				}
			});
			return row;
		});

		// Enable drag-and-drop functionality
		table.setOnDragOver(event -> {
			if (event.getDragboard().hasString()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();
		});

		table.setOnDragDropped(event -> {
			String data = event.getDragboard().getString();
			String[] accountData = data.split(":");
			String name = accountData[0];
			String type = accountData[1];

			// Add dragged account to the appropriate table (Assets or Equity & Liabilities)
			Account account = new Account(name, type, 0.0); // Default value 0.0, user will enter it
			if (table == assetsTable) {
				assetsData.add(account);
			} else {
				equityLiabilitiesData.add(account);
			}

			// Remove the dragged label from the draggable pane
			FlowPane draggablePane = (FlowPane) getChildren().get(1); // Assuming draggable pane is the second child
			draggablePane.getChildren().removeIf(node -> ((Label) node).getText().equals(name));

			event.setDropCompleted(true);
			event.consume();
		});

		return table;
	}

	private FlowPane createDraggablePane(ObservableList<Account> accounts) {
		FlowPane pane = new FlowPane();
		pane.setHgap(10);
		pane.setVgap(10);

		for (Account account : accounts) {
			addDraggableLabel(pane, account);
		}

		return pane;
	}

	private void addDraggableLabel(FlowPane pane, Account account) {
		Label accountLabel = new Label(account.getAccountName());
		accountLabel.setStyle("-fx-border-color: black; -fx-padding: 5px;");
		accountLabel.setOnDragDetected(event -> {
			Dragboard db = accountLabel.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString(account.getAccountName() + ":" + account.getType());
			db.setContent(content);
			event.consume();
		});

		pane.getChildren().add(accountLabel);
	}

	private void populateDefaultAccounts(ObservableList<Account> defaultAccounts) {
		// Distribute default accounts into the respective tables based on account type
		for (Account account : defaultAccounts) {
			if (account.getType().equals("Asset")) {
				assetsData.add(account);
			} else {
				equityLiabilitiesData.add(account);
			}
		}
	}

	private void updateTotals() {
		double totalAssets = assetsData.stream().mapToDouble(Account::getValue).sum();
		double totalEquityAndLiabilities = equityLiabilitiesData.stream().mapToDouble(Account::getValue).sum();

		assetsTotalLabel.setText("Total Assets: " + totalAssets);
		equityLiabilitiesTotalLabel.setText("Total E&L: " + totalEquityAndLiabilities);
	}

	
	private void validateBalanceSheet() {
		boolean isOnlyDefaultAccounts = assetsData.stream().allMatch(allAccounts::contains)
				&& equityLiabilitiesData.stream().allMatch(allAccounts::contains);

		if (isOnlyDefaultAccounts) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "Please add or modify accounts before validating.",
					ButtonType.OK);
			alert.showAndWait();
			return;
		}

		boolean isCorrect = true;
		StringBuilder errorMessages = new StringBuilder();

		// Validate accounts in assetsData
		for (Account account : assetsData) {
			Account matchingAccount = findMatchingAccount(account);
			if (matchingAccount == null) {
				isCorrect = false;
				errorMessages.append(account.getAccountName()).append(" in Assets has wrong values.\n");
			} else if (!account.getType().equals(matchingAccount.getType())
					|| Double.compare(account.getValue(), matchingAccount.getValue()) != 0) {
				isCorrect = false;
				errorMessages.append(account.getAccountName()).append(" in Assets has wrong values.\n");
			}
		}

		// Validate accounts in equityLiabilitiesData
		for (Account account : equityLiabilitiesData) {
			Account matchingAccount = findMatchingAccount(account);
			if (matchingAccount == null) {
				isCorrect = false;
				errorMessages.append(account.getAccountName()).append(" in Liabilities has wrong values.\n");
			} else if (!account.getType().equals(matchingAccount.getType())
					|| Double.compare(account.getValue(), matchingAccount.getValue()) != 0) {
				isCorrect = false;
				errorMessages.append(account.getAccountName()).append(" in Liabilities has wrong values.\n");
			}
		}

		// Display result
		if (isCorrect) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Good Job! Your balance sheet is correct.",
					ButtonType.OK);
			alert.showAndWait();
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR,
					"Balance sheet is not correct. Errors:\n" + errorMessages.toString(), ButtonType.OK);
			alert.showAndWait();
		}
	}

	private Account findMatchingAccount(Account account) {
		// Search for matching account in allAccounts
		for (Account matchingAccount : allAccounts) {
			if (matchingAccount.getAccountName().equalsIgnoreCase(account.getAccountName())
					&& matchingAccount.getType().equals(account.getType())) {
				return matchingAccount;
			}
		}
		return null; // Not found
	}
}
