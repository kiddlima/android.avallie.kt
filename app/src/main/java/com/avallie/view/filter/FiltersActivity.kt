package com.avallie.view.filter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.databinding.ActivityFiltersBinding
import com.avallie.helpers.PaperHelper
import com.avallie.helpers.PaperHelper.Companion.hasPhases
import com.avallie.helpers.PaperHelper.Companion.savePhases
import com.avallie.model.Category
import com.avallie.model.ConstructionPhase
import com.avallie.model.ScreenState
import com.avallie.view.MainActivity
import com.avallie.view.adapter.CategoriesAdapter
import com.avallie.view.adapter.FiltersPhasesAdapter
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class FiltersActivity : AppCompatActivity() {

    private val phases: ArrayList<ConstructionPhase> = ArrayList()
    private var selectedPhase: String? = null
    private var selectedCategories: HashSet<String> = HashSet()
    private var categories: ArrayList<Category> = ArrayList()

    lateinit var viewModel: FilterViewModel

    private val phasesAdapter: FiltersPhasesAdapter by lazy {
        FiltersPhasesAdapter(this, phases)
    }

    private val categoriesAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(this, categories, selectedCategories)
    }

    lateinit var binding: ActivityFiltersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltersBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.model = this

        viewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        binding.viewModel = viewModel

        selectedCategories.clear()
        selectedCategories.addAll(intent.getStringArrayListExtra("categories").orEmpty())

        viewModel.screenState.observe(this, Observer { screenState ->
            when (screenState) {
                ScreenState.Fail -> {
                    binding.errorContainer.visibility = View.VISIBLE
                    binding.errorContainer.findViewById<TextView>(R.id.response_subtitle).text = viewModel.errorMessage.value
                }
                ScreenState.Success -> binding.errorContainer.visibility = View.GONE
                ScreenState.Loading -> binding.errorContainer.visibility = View.GONE
            }
        })

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.errorContainer.findViewById<Button>(R.id.btn_response_action).setOnClickListener {
            viewModel.screenState.value = ScreenState.Loading
            getPhases()
        }

        if (!hasPhases()) {
            viewModel.screenState.value = ScreenState.Loading
            getPhases()
        } else {
            phases.addAll(PaperHelper.getPhases())
            viewModel.screenState.value = ScreenState.Success

            initAdapters()
        }

        binding.vButtonFilter.setOnClickListener {
            Intent(this, MainActivity::class.java).run {
                putExtra("categories", ArrayList(selectedCategories))
                setResult(1, this)
                finish()
            }
        }

        setContentView(binding.root)
    }

    private fun initAdapters() {
        setPhaseAdapter()

        selectPhase(phases[0])
    }

    private fun getPhases() {
        HttpService(this).getAllPhases(object : ConnectionListener<List<ConstructionPhase>> {
            override fun onSuccess(response: List<ConstructionPhase>) {
                phases.clear()
                phases.addAll(response)

                savePhases(phases)

                viewModel.screenState.value = ScreenState.Success

                initAdapters()
            }

            override fun onFail(error: String?) {
                viewModel.screenState.value = ScreenState.Fail

                viewModel.errorMessage.value = error
            }

            override fun noInternet() {
                viewModel.errorMessage.value = resources.getString(R.string.ops_layout_description)
            }

        })
    }

    private fun selectPhase(phase: ConstructionPhase) {
        categories.clear()
        categories.addAll(phase.categoriesObject)

        selectedPhase = phase.name

        updateSelectedPhaseInList(phase)
        updateCategoriesAdapter()

        phasesAdapter.notifyDataSetChanged()
    }

    private fun updateSelectedPhaseInList(phase: ConstructionPhase) {
        for (constructionPhase in phases) {
            constructionPhase.selected = phase.name == constructionPhase.name
        }
    }

    private fun updateCategoriesAdapter() {
        setCategoriesAdapter()
        categoriesAdapter.selectedPhase = selectedPhase
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun setCategoriesAdapter() {
        binding.vCategoriesRecycler.adapter = categoriesAdapter
        categoriesAdapter.onCategoryClicked = { isChecked, category ->
            if (isChecked) {
                selectedCategories.add(category.name)
            } else {
                selectedCategories.remove(category.name)
            }
        }

        binding.vCategoriesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setPhaseAdapter() {
        binding.vPhasesRecycler.adapter = phasesAdapter
        phasesAdapter.itemClickListener = {
            selectPhase(it)
        }
        binding.vPhasesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

}
