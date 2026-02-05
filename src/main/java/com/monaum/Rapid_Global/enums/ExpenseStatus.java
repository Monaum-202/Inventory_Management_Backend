package com.monaum.Rapid_Global.enums;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 30-Jan-26 7:14 PM
 */

public enum ExpenseStatus {
    DRAFT,              // Initial state
    SUBMITTED,          // Submitted for approval
    PENDING_LEVEL_1,    // Waiting for Manager approval
    PENDING_LEVEL_2,    // Waiting for Account Manager approval
    PENDING_LEVEL_3,    // Waiting for MD approval
    APPROVED,           // Fully approved
    REJECTED,           // Rejected at any level
    CANCELLED,          // Cancelled by creator
    PAID                // Payment completed
}