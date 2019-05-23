package com.avallie.model

import java.util.*
import kotlin.collections.ArrayList

class BudgetRequested(val budgetName: String, val budgetDate: Date, val budgetsAvaiable: Int, var products: ArrayList<SelectedProduct>?)