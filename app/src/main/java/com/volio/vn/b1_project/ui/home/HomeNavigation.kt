package com.volio.vn.b1_project.ui.home

import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.base.BaseNavigation

class HomeNavigation(val fragment: HomeFragment) : BaseNavigation() {

    fun gotoSetting() {
        val directions = HomeFragmentDirections.actionHomeFragmentToSettingFragment()
        navigateTo(R.id.homeFragment, directions)
    }

    fun gotoPolicy() {
        val directions = HomeFragmentDirections.actionHomeFragmentToPolicyFragment()
        navigateTo(R.id.homeFragment, directions)
    }

    override fun fragment(): BaseFragment<*, *> {
        return fragment
    }
}