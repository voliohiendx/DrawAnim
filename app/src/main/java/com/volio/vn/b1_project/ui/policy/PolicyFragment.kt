package com.volio.vn.b1_project.ui.policy

import androidx.fragment.app.viewModels
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentPolicyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyFragment : BaseFragment<FragmentPolicyBinding, PolicyNavigation>() {

    val viewModel: PolicyViewModel by viewModels()
    override val navigation =  PolicyNavigation(this)

    override fun getLayoutId() = R.layout.fragment_policy



    override fun observersData() {

    }

    override fun onViewReady() {
    }

}