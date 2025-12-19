package com.local.train.entity;

public enum TransactionType {
    CREDIT("Credit", "Amount added to wallet"),
    DEBIT("Debit", "Amount deducted from wallet"),
    REFUND("Refund", "Amount refunded to wallet"),
    BOOKING_PAYMENT("Booking Payment", "Payment for ticket booking"),
    WALLET_TOPUP("Wallet Topup", "Wallet recharge"),
    CANCELLATION_REFUND("Cancellation Refund", "Refund for cancelled booking"),
    PROMOTIONAL_CREDIT("Promotional Credit", "Bonus credits added"),
    ADMIN_ADJUSTMENT("Admin Adjustment", "Manual adjustment by admin");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCreditType() {
        return this == CREDIT || this == REFUND || 
               this == CANCELLATION_REFUND || this == PROMOTIONAL_CREDIT;
    }

    public boolean isDebitType() {
        return this == DEBIT || this == BOOKING_PAYMENT;
    }

    public boolean isBookingRelated() {
        return this == BOOKING_PAYMENT || this == CANCELLATION_REFUND;
    }

    public boolean isWalletOperation() {
        return this == WALLET_TOPUP || this == ADMIN_ADJUSTMENT || 
               this == PROMOTIONAL_CREDIT;
    }

    // Helper method to get TransactionType from string
    public static TransactionType fromString(String text) {
        for (TransactionType type : TransactionType.values()) {
            if (type.name().equalsIgnoreCase(text) || 
                type.getDisplayName().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    // Get all booking-related transaction types
    public static TransactionType[] getBookingRelatedTypes() {
        return new TransactionType[]{BOOKING_PAYMENT, CANCELLATION_REFUND};
    }

    // Get all credit transaction types
    public static TransactionType[] getCreditTypes() {
        return new TransactionType[]{CREDIT, REFUND, CANCELLATION_REFUND, 
                                   PROMOTIONAL_CREDIT, ADMIN_ADJUSTMENT};
    }

    // Get all debit transaction types
    public static TransactionType[] getDebitTypes() {
        return new TransactionType[]{DEBIT, BOOKING_PAYMENT, WALLET_TOPUP};
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}