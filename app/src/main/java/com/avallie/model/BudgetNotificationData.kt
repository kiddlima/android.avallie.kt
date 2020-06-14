package com.avallie.model

import java.io.Serializable

class BudgetNotificationData(
    val budgetId: Long,
    val selectedProductId: Long
) : Serializable