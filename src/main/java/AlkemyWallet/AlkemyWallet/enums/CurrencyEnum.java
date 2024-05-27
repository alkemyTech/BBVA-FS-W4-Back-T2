package AlkemyWallet.AlkemyWallet.enums;

import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;

public enum CurrencyEnum {
    ARS,
    USD;

    private double transactionLimit;

    public double getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(double transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public static void initializeLimits(CurrencyConfig config) {
        ARS.setTransactionLimit(config.getArsLimit());
        USD.setTransactionLimit(config.getUsdLimit());
    }
}

