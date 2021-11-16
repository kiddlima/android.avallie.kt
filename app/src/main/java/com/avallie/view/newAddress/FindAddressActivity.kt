package com.avallie.view.newAddress

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.core.CustomActivity
import com.avallie.databinding.ActivityFindAddressBinding
import com.avallie.helpers.AppHelper
import com.avallie.model.ScreenState
import com.avallie.view.newAddress.model.Address
import com.google.android.libraries.places.api.Places
import kotlinx.android.synthetic.main.activity_find_address.*

class FindAddressActivity :
    CustomActivity() {

    lateinit var viewModel: FindAddressViewModel

    lateinit var binding: ActivityFindAddressBinding

    lateinit var handler: Handler

    var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindAddressBinding.inflate(layoutInflater)

        viewModel = ViewModelProviders.of(this).get(FindAddressViewModel::class.java)

        binding.lifecycleOwner = this

        binding.activity = this
        binding.viewModel = viewModel

        Places.initialize(this, "AIzaSyBOIN8-_2OF48P3ItWRhvPs9mHtUQI-pjY")

        binding.addressSearchView.setOnEditorActionListener { v, actionId, event ->
            viewModel.searchPlaces(binding.addressSearchView.text.toString(), this)
            AppHelper.hideKeyboard(this, binding.root)

            if (runnable != null) {
                handler.removeCallbacks(runnable)
            }
            true
        }

        binding.addressSearchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                handler = Handler()

                runnable = Runnable {
                    viewModel.searchPlaces(
                        binding.addressSearchView.text.toString(),
                        this@FindAddressActivity
                    )
                }

                handler.postDelayed(runnable, 2000)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable)
                }

            }
        })

        binding.predictionsRecycler.setOnTouchListener { v, _ ->
            AppHelper.hideKeyboard(this, binding.root)

            false
        }

        viewModel.mState.observe(this, Observer {
            if (it == ScreenState.Success) {
                setAdapter()
            }
        })

        setContentView(binding.root)
    }

    private fun setAdapter() {
        val adapter = FindAddressAdapter(this, viewModel.predictions.value!!) {
            viewModel.findPlace(it.placeId, this, object : FindAddressViewModel.OnPlaceSearch {
                override fun onFind(address: Address) {
                    val confirmAddressBottomSheet = ConfirmAddressBottomSheet()
                    val bundle = Bundle()

                    bundle.putSerializable("address", address)
                    confirmAddressBottomSheet.arguments = bundle

                    confirmAddressBottomSheet.show(
                        supportFragmentManager,
                        "confirmAddressBottomSheet"
                    )
                }
            })
        }

        predictions_recycler.layoutManager = LinearLayoutManager(this)

        predictions_recycler.adapter = adapter
    }

}