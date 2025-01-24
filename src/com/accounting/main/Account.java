package com.accounting.main;

import java.util.Objects;
import javafx.beans.property.*;

public class Account {
    private final StringProperty accountName;
    private final StringProperty type;
    private final DoubleProperty value;
    private double originalValue; // Added missing declaration

    public Account(String accountName, String type, double value) {
        this.accountName = new SimpleStringProperty(accountName);
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleDoubleProperty(value);
        this.originalValue = value;
    }

    public String getAccountName() {
        return accountName.get();
    }

    public void setAccountName(String accountName) {
        this.accountName.set(accountName);
    }

    public StringProperty accountNameProperty() {
        return accountName;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public double getValue() {
        return value.get();
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public double getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(double originalValue) {
        this.originalValue = originalValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return Double.compare(account.originalValue, originalValue) == 0 &&
               Objects.equals(accountName.get(), account.accountName.get()) &&
               Objects.equals(type.get(), account.type.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName.get(), type.get(), originalValue);
    }
}
