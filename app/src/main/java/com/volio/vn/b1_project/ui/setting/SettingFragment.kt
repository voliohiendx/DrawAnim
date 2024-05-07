package com.volio.vn.b1_project.ui.setting

import androidx.fragment.app.viewModels
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingNavigation>() {

    val viewModel: SettingViewModel by viewModels()
    override val navigation= SettingNavigation(this)

    override fun getLayoutId() = R.layout.fragment_setting


    override fun observersData() {

    }

    override fun onViewReady() {

    }

}