package com.volio.vn.b1_project.ui.policy

import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.base.BaseNavigation

class PolicyNavigation(val fragment: PolicyFragment) : BaseNavigation() {

    override fun fragment(): BaseFragment<*, *> {
        return fragment
    }
}