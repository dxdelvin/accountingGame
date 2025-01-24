package com.accounting.main;

import java.util.ArrayList;
import java.util.List;

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
    private ObservableList<Account> allAccounts;
    private ObservableList<Account> assetsData;
    private ObservableList<Account> equityLiabilitiesData;
    private Label assetsTotalLabel;
    private Label equityLiabilitiesTotalLabel;
    private Button validateButton;
    private Label successLabel;
    private ObservableList<Account> draggableAccounts;


    public AccountingPanel(ObservableList<Account> defaultAccounts, ObservableList<Account> draggableAccounts) {
        DialogueSystem.pauseDialogue();
        this.draggableAccounts = draggableAccounts;
        this.allAccounts = FXCollections.observableArrayList();
        this.allAccounts.addAll(defaultAccounts.stream()
            .map(account -> new Account(account.getAccountName(), account.getType(), account.getValue()))
            .toList());


        // Consolidate duplicates
        ObservableList<Account> uniqueDraggableAccounts = FXCollections.observableArrayList();
        for (Account draggableAccount : draggableAccounts) {
            Account matchingDefaultAccount = findMatchingAccount(draggableAccount, defaultAccounts);
            if (matchingDefaultAccount != null) {
                matchingDefaultAccount.setValue(matchingDefaultAccount.getValue() + draggableAccount.getValue());
            } else {
                uniqueDraggableAccounts.add(draggableAccount);
            }
        }

        this.allAccounts.addAll(uniqueDraggableAccounts);

        this.assetsData = FXCollections.observableArrayList();
        this.equityLiabilitiesData = FXCollections.observableArrayList();

        this.assetsTable = createTableView("Assets", assetsData);
        this.equityLiabilitiesTable = createTableView("Equity & Liabilities", equityLiabilitiesData);
        FlowPane draggableItemsPane = createDraggablePane(uniqueDraggableAccounts);

        assetsTotalLabel = new Label("Total A: 0");
        equityLiabilitiesTotalLabel = new Label("Total E&L: 0");
        assetsTotalLabel.setFont(new Font(16));
        equityLiabilitiesTotalLabel.setFont(new Font(16));

        this.validateButton = new Button("Validate");

        setSpacing(10);
        setPadding(new Insets(10));

        HBox tablesBox = new HBox(10, new VBox(new Label("Assets"), assetsTable, assetsTotalLabel),
                new VBox(new Label("Equity & Liabilities"), equityLiabilitiesTable, equityLiabilitiesTotalLabel));
        tablesBox.setPadding(new Insets(10));

        getChildren().addAll(tablesBox, draggableItemsPane, validateButton);

        validateButton.setOnAction(e -> validateBalanceSheet(uniqueDraggableAccounts));

        assetsData.addListener((ListChangeListener<Account>) change -> updateTotals());
        equityLiabilitiesData.addListener((ListChangeListener<Account>) change -> updateTotals());

        populateDefaultAccounts(defaultAccounts);

        this.successLabel = new Label();
        successLabel.setFont(new Font(16));
        successLabel.setStyle("-fx-text-fill: green;");
        successLabel.setVisible(false); // Initially hidden

        // Add success label after validateButton, not duplicating any children list
        getChildren().add(successLabel);

        validateButton.setOnAction(e -> validateBalanceSheet(uniqueDraggableAccounts));

        getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
    }


    
    private FlowPane createDraggablePane(ObservableList<Account> accounts) {
        FlowPane pane = new FlowPane();
        pane.setHgap(10);
        pane.setVgap(10);

        // Create draggable labels for each account
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
    
    


    private boolean isAccountInTable(String accountName, String accountType) {
        // Check if account already exists in the tables
        for (Account account : assetsData) {
            if (account.getAccountName().equals(accountName) && account.getType().equals(accountType)) {
                return true; // Account already in Assets table
            }
        }
        for (Account account : equityLiabilitiesData) {
            if (account.getAccountName().equals(accountName) && account.getType().equals(accountType)) {
                return true; // Account already in Equity & Liabilities table
            }
        }
        return false; // Account not found in either table
    }


    private TableView<Account> createTableView(String tableTitle, ObservableList<Account> tableData) {
        TableView<Account> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(tableData);

        TableColumn<Account, String> accountNameCol = new TableColumn<>("Account Name");
        accountNameCol.setCellValueFactory(data -> data.getValue().accountNameProperty());

        TableColumn<Account, Double> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(data -> data.getValue().valueProperty().asObject());
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        valueCol.setOnEditCommit(event -> {
            Account account = event.getRowValue();
            account.setValue(event.getNewValue());
            updateTotals();
        });

        table.getColumns().addAll(accountNameCol, valueCol);
        table.setPrefHeight(300);
        table.setEditable(true);

        table.setRowFactory(tv -> {
            TableRow<Account> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Delete");

                    Account account = row.getItem();
                    deleteItem.setDisable(allAccounts.contains(account));
                    deleteItem.setOnAction(e -> {
                        tableData.remove(account);
                        addDraggableLabel((FlowPane) getChildren().get(1), account); // Add back draggable label
                    });

                    contextMenu.getItems().add(deleteItem);
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

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

            if (!isAccountInTable(name, type)) {
                Account account = new Account(name, type, 0.0);
                Account draggableAccount = findMatchingAccount(account, draggableAccounts);
                if (draggableAccount != null) {
                    account.setOriginalValue(draggableAccount.getValue()); // Set the original value from draggable accounts
                }
                if (table == assetsTable) {
                    assetsData.add(account);
                } else {
                    equityLiabilitiesData.add(account);
                }

                // Remove label from draggable pane
                FlowPane draggablePane = (FlowPane) getChildren().get(1);
                draggablePane.getChildren().removeIf(node -> ((Label) node).getText().equals(name));

                event.setDropCompleted(true);
                event.consume();
            }
        });

        return table;
    }


	private void populateDefaultAccounts(ObservableList<Account> defaultAccounts) {
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

    private void validateBalanceSheet(ObservableList<Account> draggableAccounts) {
        boolean isCorrect = true;
        StringBuilder errorMessages = new StringBuilder();

        // Check totals
        double totalAssets = assetsData.stream().mapToDouble(Account::getValue).sum();
        double totalEquityAndLiabilities = equityLiabilitiesData.stream().mapToDouble(Account::getValue).sum();

        if (Double.compare(totalAssets, totalEquityAndLiabilities) != 0) {
            isCorrect = false;
            errorMessages.append("Total Assets and Total Liabilities are not equal.\n");
        }

        // Check individual accounts
        for (Account account : assetsData) {
            Account matchingAccount = findMatchingAccount(account, allAccounts);
            if (matchingAccount == null) {
                // If the account does not exist in default accounts, treat it as a new draggable account
                if (Double.compare(account.getValue(), account.getOriginalValue()) != 0) {
                    isCorrect = false;
                    errorMessages.append(account.getAccountName())
                                 .append(" in Assets has incorrect values.\n");
                }
            } else {
                // If the account exists in default accounts, compare original value
                if (!account.getType().equals("Asset") ||
                    Double.compare(account.getOriginalValue(), matchingAccount.getOriginalValue()) != 0) {
                    isCorrect = false;
                    errorMessages.append(account.getAccountName())
                                 .append(" in Assets has incorrect original values or has been modified.\n");
                }
            }
        }

        for (Account account : equityLiabilitiesData) {
            Account matchingAccount = findMatchingAccount(account, allAccounts);
            if (matchingAccount == null) {
                // If the account does not exist in default accounts, treat it as a new draggable account
                if (Double.compare(account.getValue(), account.getOriginalValue()) != 0) {
                    isCorrect = false;
                    errorMessages.append(account.getAccountName())
                                 .append(" in Liabilities has incorrect values.\n");
                }
            } else {
                // If the account exists in default accounts, compare original value
                if (!account.getType().equals("Liability") ||
                    Double.compare(account.getOriginalValue(), matchingAccount.getOriginalValue()) != 0) {
                    isCorrect = false;
                    errorMessages.append(account.getAccountName())
                                 .append(" in Liabilities has incorrect original values or has been modified.\n");
                }
            }
        }

        // Show results
        if (isCorrect) {
            DialogueSystem.resumeDialogue();
            successLabel.setText("Good Job! Your balance sheet is correct.");
            successLabel.setVisible(true); // Show success message
            validateButton.setVisible(false);
        } else {
            successLabel.setVisible(false); // Hide success message on failure
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Balance sheet is not correct. Errors:\n" + errorMessages.toString(), ButtonType.OK);
            alert.showAndWait();
        }
    }


    private Account findMatchingAccount(Account account, ObservableList<Account> accountList) {
        return accountList.stream()
                .filter(a -> a.getAccountName().equalsIgnoreCase(account.getAccountName()) // Case-insensitive comparison
                        && a.getType().equals(account.getType()))
                .findFirst()
                .orElse(null);
    }

}
