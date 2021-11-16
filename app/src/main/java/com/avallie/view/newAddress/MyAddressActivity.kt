package com.avallie.view.newAddress

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.core.CustomActivity
import com.avallie.databinding.ActivityMyAddressBinding
import com.avallie.helpers.PaperHelper
import com.avallie.model.ScreenState
import com.avallie.view.newAddress.model.Address


class MyAddressActivity : CustomActivity() {

    lateinit var binding: ActivityMyAddressBinding

    lateinit var viewModel: MyAddressViewModel

    private var defaultAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAddressBinding.inflate(layoutInflater)

        binding.addAddressButton.setOnClickListener {
            startActivityForResult(Intent(this, FindAddressActivity::class.java), 0)
        }

        viewModel = ViewModelProviders.of(this).get(MyAddressViewModel::class.java)

        viewModel.getCustomer(this)

        viewModel.mState.observe(this, Observer {
            if (it == ScreenState.Success) {
                addDefaultAddressToList(false)
            }
        })

        addDefaultAddressToList(false)

        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setContentView(binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            addDefaultAddressToList(true)
        }
    }

    private fun addDefaultAddressToList(fromRegister: Boolean) {
        defaultAddress = PaperHelper.getDefaultAddress()

        if (viewModel.customer?.addresses.isNullOrEmpty()) {
            viewModel.customer?.addresses = mutableListOf()
        }

        if (fromRegister) {
            viewModel.customer?.addresses?.add(defaultAddress!!)
        }

        var defaultAddressIndex: Int? = null

        viewModel.customer?.addresses!!.forEachIndexed { index, address ->
            if (address.id == defaultAddress?.id) {
                defaultAddressIndex = index
            }
        }

        defaultAddressIndex?.run {
            viewModel.customer?.addresses?.removeAt(this)
            viewModel.customer?.addresses?.add(0, defaultAddress!!)
        }.runCatching {
            PaperHelper.setDefaultAddress(viewModel.customer?.addresses!![0])
        }

        binding.addressRecycler.layoutManager = LinearLayoutManager(this)
        binding.addressRecycler.adapter = AddressAdapter()
    }

    inner class AddressAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return AddressViewHolder(
                LayoutInflater.from(this@MyAddressActivity)
                    .inflate(R.layout.address_item, parent, false)
            )
        }

        override fun getItemCount(): Int = viewModel.customer?.addresses?.count()!!

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as AddressViewHolder

            val address = viewModel.customer?.addresses?.get(position)

            holder.view.background =
                getDrawable(if (position == 0) R.drawable.address_green_bg else R.drawable.address_bg)

            holder.firstLine.setTextColor(
                if (position != 0)
                    ContextCompat.getColor(
                        this@MyAddressActivity,
                        R.color.grayPrimaryDarker
                    ) else Color.WHITE

            )

            holder.secondLine.setTextColor(
                if (position != 0)
                    ContextCompat.getColor(
                        this@MyAddressActivity,
                        R.color.grayPrimary
                    ) else Color.WHITE

            )

            holder.mapIcon.setColorFilter(
                if (position != 0)
                    ContextCompat.getColor(
                        this@MyAddressActivity,
                        R.color.grayPrimary
                    ) else Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN
            );

            address?.run {
                holder.firstLine.text = "$street, $streetNumber - $postalCode"
                holder.secondLine.text = "$district - $city, $state - $country"
            }
        }

        inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var firstLine: TextView = itemView.findViewById(R.id.first_text)
            var secondLine: TextView = itemView.findViewById(R.id.second_text)
            var mapIcon: ImageView = itemView.findViewById(R.id.map_icon)
            var view = itemView

            init {
                itemView.setOnClickListener {
                    PaperHelper.setDefaultAddress(viewModel.customer?.addresses!![adapterPosition])

                    finish()
                }
            }
        }

    }

}