package com.volio.vn.b1_project.base

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

abstract class BaseNavigation {

    abstract fun fragment(): BaseFragment<*, *>

    val navController: NavController?
        get() {
            return try {
                if (fragment().activity != null && fragment().isAdded) {
                    fragment().findNavController()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    private val currentNavDestination: NavDestination?
        get() {
            return navController?.currentDestination
        }


    private var defaultLifecycleObserver: DefaultLifecycleObserver? = null


    /**
     * Xử lý chuyển màn hình
     * @param currentNavId -> truyền vào id màn hình hiện tại để kiểm tra xem có đúng đang đứng ở màn hình hiện tại k
     * @param directions -> đích cần đến (đã bao gồm param rồi nhé)
     */
    fun navigateTo(
        currentNavId: Int,
        directions: NavDirections,
        navOptions: NavOptions? = null,
        navOnResumed: Boolean = true
    ) {
        fun executeNavigate(onDone: () -> Unit = {}) {
            if (navController != null && currentNavDestination?.id == currentNavId) {
                navController?.addOnDestinationChangedListener(object :
                    NavController.OnDestinationChangedListener {
                    override fun onDestinationChanged(
                        controller: NavController,
                        destination: NavDestination,
                        arguments: Bundle?
                    ) {
                        if (destination.id != currentNavId) {
                            navController?.removeOnDestinationChangedListener(this)
                            onDone()
                        }

                    }

                })
                navController?.navigate(directions, navOptions)
                return
            }
        }

        if (!navOnResumed) {
            executeNavigate()
        } else {
            val lifeCycleState = fragment().lifecycle.currentState
            if (lifeCycleState != Lifecycle.State.RESUMED) {
                if (defaultLifecycleObserver != null) {
                    fragment().lifecycle.removeObserver(defaultLifecycleObserver!!)
                }
                if (currentNavDestination?.id == currentNavId) {
                    defaultLifecycleObserver = object : DefaultLifecycleObserver {
                        override fun onResume(owner: LifecycleOwner) {
                            super.onResume(owner)
                            fragment().lifecycle.removeObserver(this)
                            executeNavigate()
                        }
                    }
                    fragment().lifecycle.addObserver(defaultLifecycleObserver as DefaultLifecycleObserver)
                    executeNavigate(onDone = {
                        if (defaultLifecycleObserver != null) {
                            fragment().lifecycle.removeObserver(defaultLifecycleObserver as DefaultLifecycleObserver)
                        }
                    })
                }
            }else{
                executeNavigate()
            }
        }
    }

    fun popBackStack() {
        navController?.popBackStack()
    }

    fun popBackStack(currentNavId: Int,inclusive:Boolean) {
        navController?.popBackStack(currentNavId, inclusive = inclusive)
    }


}