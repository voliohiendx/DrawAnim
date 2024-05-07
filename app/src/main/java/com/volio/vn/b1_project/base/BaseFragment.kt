package com.volio.vn.b1_project.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * BaseFragment define một số function cơ bản
 *  - V: ViewDataBinding => Xử lý dataBinding cho fragment
 *  - E: BaseEvent => Tách xử lý các event của View sang 1 file khác, giảm thiểu code ở fragment
 *  - N: BaseNavigation => Mong muốn gộp các phần chuyển màn hình vào file này để dễ theo dõi cho team maintain
 *
 *  - getDataBundle(): Xử lý nhận dữ liệu của args ở đây, đảm bảo clean code
 *  - colorsStatusBar: Define màu của statusBar theo tên file Màn hình (eg: Homefragment -> HomeFragment.kt)
 *  - onBackPressed(): Xử lý backPress cho fragment, nếu return false sẽ tự popBackStack, return true thì tự xử lý
 */
abstract class BaseFragment<V : ViewDataBinding, N : BaseNavigation> :
    Fragment() {

    lateinit var binding: V
        private set


    abstract val navigation: N

    open var isUseBackPress = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDataBundle(arguments)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (isUseBackPress) {
            requireActivity()
                .onBackPressedDispatcher
                .addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (!onBackPressed()) {
                            navigation.popBackStack()
                        }
                    }
                })
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observersData()
        onViewReady()
        initListener()
    }


    open fun getDataBundle(arguments: Bundle?) {

    }


    @LayoutRes
    abstract fun getLayoutId(): Int


    abstract fun observersData()


    open fun onViewReady() {

    }

    open fun initListener() {

    }


    /**
     * Xử lý backPress cho fragment, nếu return false sẽ tự popBackStack, return true thì tự xử lý
     */
    open fun onBackPressed(): Boolean {
        return false
    }

}