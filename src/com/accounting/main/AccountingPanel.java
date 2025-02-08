package com.accounting.main;

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
    // Store the full set of accounts (for validation) and the default ones separately.
    private ObservableList<Account> allAccounts;
    private ObservableList<Account> defaultAccounts;
    private ObservableList<Account> assetsData;
    private ObservableList<Account> equityLiabilitiesData;
    private Label assetsTotalLabel;
    private Label equityLiabilitiesTotalLabel;
    private Button validateButton;
    private Label successLabel;
    private ObservableList<Account> draggableAccounts;


    public AccountingPanel(ObservableList<Account> defaultAccounts, ObservableList<Account> draggableAccounts) {
        DialogueSystem.pauseDialogue();
        
        this.defaultAccounts = FXCollections.observableArrayList();
        this.defaultAccounts.addAll(defaultAccounts);
        
        this.draggableAccounts = draggableAccounts;
        
        this.allAccounts = FXCollections.observableArrayList();

        this.allAccounts.addAll(defaultAccounts.stream()
                .map(acc -> new Account(acc.getAccountName(), acc.getType(), acc.getValue()))
                .toList());


        ObservableList<Account> uniqueDraggableAccounts = FXCollections.observableArrayList();
        for (Account draggableAccount : draggableAccounts) {
            Account matchingDefault = findMatchingAccount(draggableAccount, defaultAccounts);
            if (matchingDefault != null) {
                // IMPORTANT: Do NOT change the displayed value.
                // Update the expected correct answer (original value) by adding the draggable amount.
                matchingDefault.setOriginalValue(matchingDefault.getOriginalValue() + draggableAccount.getValue());
                // Also update the copy in allAccounts so validation will use the consolidated value.
                Account matchingAll = findMatchingAccount(draggableAccount, allAccounts);
                if (matchingAll != null) {
                    matchingAll.setOriginalValue(matchingAll.getOriginalValue() + draggableAccount.getValue());
                }
            } else {
                uniqueDraggableAccounts.add(draggableAccount);
            }
        }
        // Add any unique (non-duplicate) draggable accounts to the complete set.
        this.allAccounts.addAll(uniqueDraggableAccounts);

        // Prepare the table data lists.
        this.assetsData = FXCollections.observableArrayList();
        this.equityLiabilitiesData = FXCollections.observableArrayList();

        // Create the two tables.
        this.assetsTable = createTableView("Assets", assetsData);
        this.equityLiabilitiesTable = createTableView("Equity & Liabilities", equityLiabilitiesData);
        // Create the pane holding draggable labels.
        FlowPane draggableItemsPane = createDraggablePane(uniqueDraggableAccounts);

        assetsTotalLabel = new Label("Total Assets: 0");
        equityLiabilitiesTotalLabel = new Label("Total E&L: 0");
        assetsTotalLabel.setFont(new Font(16));
        equityLiabilitiesTotalLabel.setFont(new Font(16));

        this.validateButton = new Button("Validate");

        setSpacing(10);
        setPadding(new Insets(10));

        HBox tablesBox = new HBox(10, 
                new VBox(new Label("Assets"), assetsTable, assetsTotalLabel),
                new VBox(new Label("Equity & Liabilities"), equityLiabilitiesTable, equityLiabilitiesTotalLabel));
        tablesBox.setPadding(new Insets(10));

        getChildren().addAll(tablesBox, draggableItemsPane, validateButton);

        // Use the uniqueDraggableAccounts in the validation (so that expected answers for duplicates are known)
        validateButton.setOnAction(e -> validateBalanceSheet());

        // Update totals when table data changes.
        assetsData.addListener((ListChangeListener<Account>) change -> updateTotals());
        equityLiabilitiesData.addListener((ListChangeListener<Account>) change -> updateTotals());

        // Populate the tables with the default accounts.
        populateDefaultAccounts(defaultAccounts);

        this.successLabel = new Label();
        successLabel.setFont(new Font(16));
        successLabel.setStyle("-fx-text-fill: green;");
        successLabel.setVisible(false); // Initially hidden

        // Add success label after validateButton.
        getChildren().add(successLabel);

        getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
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
            // Pass both the account name and type
            content.putString(account.getAccountName() + ":" + account.getType());
            db.setContent(content);
            event.consume();
        });
        pane.getChildren().add(accountLabel);
    }


    private boolean isAccountInTable(String accountName, String accountType) {
        for (Account account : assetsData) {
            if (account.getAccountName().equalsIgnoreCase(accountName) &&
                account.getType().equals(accountType)) {
                return true;
            }
        }
        for (Account account : equityLiabilitiesData) {
            if (account.getAccountName().equalsIgnoreCase(accountName) &&
                account.getType().equals(accountType)) {
                return true;
            }
        }
        return false;
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
        // When user edits a cell, update the account’s value.
        valueCol.setOnEditCommit(event -> {
            Account account = event.getRowValue();
            account.setValue(event.getNewValue());
            updateTotals();
        });

        table.getColumns().addAll(accountNameCol, valueCol);
        table.setPrefHeight(300);
        table.setEditable(true);

        // Set up right-click deletion. Deletion is allowed only for accounts that are NOT default.
        table.setRowFactory(tv -> {
            TableRow<Account> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Delete");

                    Account account = row.getItem();
                    // Allow deletion only if the account is not one of the default accounts.
                    boolean isDefault = (findMatchingAccount(account, defaultAccounts) != null);
                    deleteItem.setDisable(isDefault);
                    deleteItem.setOnAction(e -> {
                        tableData.remove(account);
                        // Add back the draggable label if this account is a draggable (non-default) one.
                        FlowPane draggablePane = (FlowPane) getChildren().get(1);
                        // Check if a label with the same account name already exists.
                        if (!draggablePane.getChildren().stream()
                                .anyMatch(node -> ((Label) node).getText().equalsIgnoreCase(account.getAccountName()))) {
                            addDraggableLabel(draggablePane, account);
                        }
                    });

                    contextMenu.getItems().add(deleteItem);
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

        // Handle drag-and-drop into the table.
        table.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        table.setOnDragDropped(event -> {
            String data = event.getDragboard().getString();
            String[] parts = data.split(":");
            if (parts.length < 2) {
                event.setDropCompleted(false);
                return;
            }
            String name = parts[0];
            String type = parts[1];

            // Only add if the account is not already present.
            if (!isAccountInTable(name, type)) {
                // Create a new account with a displayed value of 0.
                Account newAccount = new Account(name, type, 0.0);
                // If this drop comes from a draggable account, set its expected answer (original value)
                Account draggableSource = findMatchingAccount(new Account(name, type, 0.0), draggableAccounts);
                if (draggableSource != null) {
                    newAccount.setOriginalValue(draggableSource.getValue());
                }
                if (table == assetsTable) {
                    assetsData.add(newAccount);
                } else {
                    equityLiabilitiesData.add(newAccount);
                }
                // Remove its label from the draggable pane.
                FlowPane draggablePane = (FlowPane) getChildren().get(1);
                draggablePane.getChildren().removeIf(node -> ((Label) node).getText().equalsIgnoreCase(name));
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
        double totalEandL = equityLiabilitiesData.stream().mapToDouble(Account::getValue).sum();

        assetsTotalLabel.setText("Total Assets: " + totalAssets);
        equityLiabilitiesTotalLabel.setText("Total E&L: " + totalEandL);
    }


    private void validateBalanceSheet() {
        boolean isCorrect = true;
        StringBuilder errorMessages = new StringBuilder();

        // Check overall totals.
        double totalAssets = assetsData.stream().mapToDouble(Account::getValue).sum();
        double totalEandL = equityLiabilitiesData.stream().mapToDouble(Account::getValue).sum();

        if (Double.compare(totalAssets, totalEandL) != 0) {
            isCorrect = false;
            errorMessages.append("Total Assets and Total Liabilities are not equal.\n");
        }

        // New condition: Balance sheet should not be zero
        if (totalAssets == 0 && totalEandL == 0) {
            isCorrect = false;
            errorMessages.append("Balance sheet cannot be empty. Please add values to the accounts.\n");
        }

        // Validate each account in Assets.
        for (Account account : assetsData) {
            double expectedValue = account.getOriginalValue();
            Account expectedAccount = findMatchingAccount(account, allAccounts);
            if (expectedAccount != null) {
                expectedValue = expectedAccount.getOriginalValue();
            }

            boolean typeError = !account.getType().equalsIgnoreCase("Asset");
            boolean valueError = Double.compare(account.getValue(), expectedValue) != 0;
            if (typeError || valueError) {
                isCorrect = false;
                errorMessages.append(account.getAccountName()).append(" in Assets ");
                if (typeError && valueError) {
                    errorMessages.append("is on the wrong side of the balance sheet and has incorrect values.\n");
                } else if (typeError) {
                    errorMessages.append("is on the wrong side of the balance sheet.\n");
                } else {
                    errorMessages.append("has incorrect values.\n");
                }
            }
        }

        // Validate each account in Equity & Liabilities.
        for (Account account : equityLiabilitiesData) {
            double expectedValue = account.getOriginalValue();
            Account expectedAccount = findMatchingAccount(account, allAccounts);
            if (expectedAccount != null) {
                expectedValue = expectedAccount.getOriginalValue();
            }

            boolean typeError = !account.getType().equalsIgnoreCase("Liability");
            boolean valueError = Double.compare(account.getValue(), expectedValue) != 0;
            if (typeError || valueError) {
                isCorrect = false;
                errorMessages.append(account.getAccountName()).append(" in Liabilities ");
                if (typeError && valueError) {
                    errorMessages.append("is on the wrong side of the balance sheet and has incorrect values.\n");
                } else if (typeError) {
                    errorMessages.append("is on the wrong side of the balance sheet.\n");
                } else {
                    errorMessages.append("has incorrect values.\n");
                }
            }
        }

        // Show results.
        if (isCorrect) {
            DialogueSystem.resumeDialogue();
            successLabel.setText("Good Job! Your balance sheet is correct.");
            successLabel.setVisible(true);
            validateButton.setVisible(false);
        } else {
            successLabel.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Balance sheet is not correct. Errors:\n" + errorMessages.toString(), ButtonType.OK);
            alert.showAndWait();
        }
    }


    private Account findMatchingAccount(Account account, ObservableList<Account> accountList) {
        return accountList.stream()
                .filter(a -> a.getAccountName().equalsIgnoreCase(account.getAccountName())
                          && a.getType().equals(account.getType()))
                .findFirst()
                .orElse(null);
    }
}
