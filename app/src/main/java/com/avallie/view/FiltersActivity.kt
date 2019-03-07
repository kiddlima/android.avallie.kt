package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.helpers.PaperHelper
import com.avallie.helpers.PaperHelper.Companion.hasPhases
import com.avallie.helpers.PaperHelper.Companion.savePhases
import com.avallie.model.Category
import com.avallie.model.ConstructionPhase
import com.avallie.view.adapter.CategoriesAdapter
import com.avallie.view.adapter.FiltersPhasesAdapter
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService
import kotlinx.android.synthetic.main.activity_filters.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class FiltersActivity : AppCompatActivity() {

    private val phases: ArrayList<ConstructionPhase> = ArrayList()
    private var selectedPhase: String? = null
    private var selectedCategories: HashSet<String> = HashSet()
    private var categories: ArrayList<Category> = ArrayList()

    private val phasesAdapter: FiltersPhasesAdapter by lazy {
        FiltersPhasesAdapter(this, phases)
    }

    private val categoriesAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter(this, categories, selectedCategories)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        selectedCategories.clear()
        selectedCategories.addAll(intent.getStringArrayListExtra("categories").orEmpty())

        if(!hasPhases()){
            getPhases()
        } else {
            phases.addAll(PaperHelper.getPhases())

            initAdapters()
        }

        v_button_filter.setOnClickListener {
            Intent(this, MainActivity::class.java).run {
                putExtra("categories", ArrayList(selectedCategories))
                setResult(1, this)
                finish()
            }
        }
    }

    private fun initAdapters(){
        setPhaseAdapter()

        selectPhase(phases[0])
    }

    private fun getPhases() {
        HttpService(this).getAllPhases(object : ConnectionListener<List<ConstructionPhase>> {
            override fun onSuccess(response: List<ConstructionPhase>) {
                phases.clear()
                phases.addAll(response)

                savePhases(phases)

                initAdapters()
            }

            override fun onFail(error: String?) {

            }

            override fun noInternet() {

            }

        })
    }

    private fun selectPhase(phase: ConstructionPhase) {
        categories.clear()
        categories.addAll(phase.categoriesObject)

        selectedPhase = phase.name

        phasesAdapter.notifyDataSetChanged()

        updateSelectedPhaseInList(phase)
        updateCategoriesAdapter()
    }

    private fun updateSelectedPhaseInList(phase: ConstructionPhase) {
        for (constructionPhase in phases) {
            constructionPhase.selected = phase.id.equals(constructionPhase.id)
        }
    }

    private fun updateCategoriesAdapter() {
        setCategoriesAdapter()
        categoriesAdapter.selectedPhase = selectedPhase
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun setCategoriesAdapter() {
        v_categories_recycler.adapter = categoriesAdapter
        categoriesAdapter.onCategoryClicked = { isChecked, category ->
            if (isChecked) {
                selectedCategories.add(category.name.orEmpty())
            } else {
                selectedCategories.remove(category.name)
            }
        }

        v_categories_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setPhaseAdapter() {
        v_phases_recycler.adapter = phasesAdapter
        phasesAdapter.itemClickListener = {
            selectPhase(it)
        }
        v_phases_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

}
