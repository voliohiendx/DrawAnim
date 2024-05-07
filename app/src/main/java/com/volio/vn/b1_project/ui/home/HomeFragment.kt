package com.volio.vn.b1_project.ui.home

import androidx.fragment.app.viewModels
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeNavigation>() {

    val viewModel: HomeViewModel by viewModels()
    override val navigation=  HomeNavigation(this)

    override fun getLayoutId() = R.layout.fragment_home


    override fun observersData() {

    }

    override fun onViewReady() {

    }



}