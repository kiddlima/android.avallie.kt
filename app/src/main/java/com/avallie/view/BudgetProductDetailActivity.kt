package com.avallie.view

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avallie.databinding.ActivityBudgetProductDetailBinding
import com.avallie.helpers.PaperHelper
import com.avallie.helpers.SpecHelper
import com.avallie.model.RequestedProduct
import com.avallie.model.ScreenState
import com.avallie.view.adapter.BudgetsAdapter
import com.avallie.view.register.BudgetProductDetailViewModel
import com.avallie.widgets.NoDataContainer
import com.avallie.widgets.SupplierInfoDialog
import io.paperdb.Paper
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class BudgetProductDetailActivity : AppCompatActivity() {

    private val SELECTED_PRODUCT = "selected-product"

    private lateinit var recyclerView: RecyclerView

    lateinit var viewModel: BudgetProductDetailViewModel

    var lastPhoneCalled: String = ""

    val adapter: BudgetsAdapter by lazy {
        BudgetsAdapter(
            this,
            viewModel.requestedProduct.value!!.budgets,
            onBudgetClicked = { budget ->
                SupplierInfoDialog(
                    this,
                    budget.supplier.name,
                    budget.supplier.phone,
                    budget.supplier.email,
                    onPhoneClicked = View.OnClickListener {
                        callSupplier(budget.supplier.phone)
                    },
                    onMailClicked = View.OnClickListener {
                        emailToSupplier(
                            budget.supplier.email,
                            viewModel.requestedProduct.value?.product?.name!!
                        )
                    }
                ).show()
            })
    }

    private fun callSupplier(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)

        lastPhoneCalled = phoneNumber

        intent.data = Uri.parse("tel:$phoneNumber")

        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }

    private fun emailToSupplier(email: String, productName: String) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.run {
            type = "message/rfc822"
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(email)
            )

            putExtra(Intent.EXTRA_SUBJECT, "Recebi seu orçamento na Avallie!")

            putExtra(
                Intent.EXTRA_TEXT,
                "Olá! \n Me chamo ${PaperHelper.getCustomer()?.name} e recebi o seu orçamento na Avallie para o produto $productName."
            )
        }

        try {
            startActivity(
                Intent.createChooser(
                    intent,
                    "Send mail..."
                ), null
            )
        } catch (ex: ActivityNotFoundException) {
        }
    }


    private lateinit var binding: ActivityBudgetProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetProductDetailBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(BudgetProductDetailViewModel::class.java)

        viewModel.requestedProduct.value =
            intent.getSerializableExtra(SELECTED_PRODUCT) as RequestedProduct

        viewModel.getSelectedProductResponses(this)

        viewModel.screenState.observe(this, Observer {
            if (it == ScreenState.Success) {
                setAdapter()
            }
        })

        binding.model = this
        binding.viewModel = viewModel

        viewModel.requestedProduct.value?.specifications =
            viewModel.requestedProduct.value?.specifications?.replace(":", ": ")!!

        binding.productDetailSubtitle.text =
            "${viewModel.requestedProduct.value?.amount} ${viewModel.requestedProduct.value?.product?.unit}"

        viewModel.requestedProduct.value?.run {
            binding.productDetailDescription.text = formatProductDescription(this)
        }

        binding.noDataContainer = NoDataContainer(
            "Nenhum orçamento disponível ainda!",
            "Enviaremos uma notificação assim que algum fornecedor responder"
        )

        setContentView(binding.root)

        binding.productDetailHeader.text =
            viewModel.requestedProduct.value!!.product.name.toLowerCase().capitalize()

        recyclerView = binding.budgetsRecycler
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        callSupplier(lastPhoneCalled)
    }

    private fun formatProductDescription(requestedProduct: RequestedProduct): String {
        val specs = SpecHelper.createSpecs(viewModel.requestedProduct.value?.specifications!!)

        val specsString = SpecHelper.formattedSpecsString(specs)

        val formattedString = "${if (requestedProduct.brand != null) "Marca de preferência: ${requestedProduct.brand}\n" else ""}${specsString}"

        if (requestedProduct.extraInformation != null) {
            return "$formattedString\n Informações extras: ${requestedProduct.extraInformation}"
        }

        return formattedString
    }


    private fun setAdapter() {
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun onBackPressed(view: View) {
        super.onBackPressed()
    }
}
