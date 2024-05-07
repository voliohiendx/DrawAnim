package com.volio.vn.b1_project.ui.setting

import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.base.BaseNavigation

class SettingNavigation(val fragment: SettingFragment) : BaseNavigation() {
    override fun fragment(): BaseFragment<*, *> = fragment
}