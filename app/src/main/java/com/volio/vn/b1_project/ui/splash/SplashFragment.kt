package com.volio.vn.b1_project.ui.splash

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import androidx.fragment.app.viewModels
import com.tencent.mmkv.MMKV
import com.volio.draw.model.DataDrawList
import com.volio.draw.model.TypeDraw
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentSplashBinding
import com.volio.vn.b1_project.ui.loadImage
import com.volio.vn.b1_project.utils.MMKVKey
import com.volio.vn.common.utils.setPreventDoubleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashNavigation>() {

    val viewModel: SplashViewModel by viewModels()
    override val navigation = SplashNavigation(this)

    override fun getLayoutId() = R.layout.fragment_splash

    override fun observersData() {

    }

    val imageFloodFill by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.test)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewReady() {
        viewModel.test()

        binding.tvSticker.setPreventDoubleClick {
            binding.drawView.setTypeDraw(TypeDraw.STICKER)
        }
        binding.tvBrush.setPreventDoubleClick {
            binding.drawView.setTypeDraw(TypeDraw.BRUSH)
        }
        binding.tvEraser.setPreventDoubleClick {
            binding.drawView.setTypeDraw(TypeDraw.ERASE)
        }

        binding.tvGetData.setPreventDoubleClick {
            MMKV.defaultMMKV()
                .encode(MMKVKey.DATA_DRAW, DataDrawList(binding.drawView.getDataDraw()))
        }

        binding.tvSetData.setPreventDoubleClick {
            MMKV.defaultMMKV().decodeParcelable(MMKVKey.DATA_DRAW, DataDrawList::class.java)?.let {
                binding.drawView.setData(it.data)
            }
        }

        binding.tvColor.setPreventDoubleClick {
            binding.drawView.setBrushColor(randomColor())
        }

        binding.tvSize.setPreventDoubleClick {
            binding.drawView.setBrushSize(Random.nextInt(10, 50).toFloat())
        }

        binding.tvUndo.setPreventDoubleClick {
            binding.drawView.undo()
        }

        binding.tvRedo.setPreventDoubleClick {
            binding.drawView.redo()
        }


//        binding.imgTest.loadImage(bitmap = imageFloodFill)
//
//
//        binding.imgTest.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_UP -> {
//                    val queueLinearFloodFiller = QueueLinearFloodFiller(
//                        imageFloodFill,
//                        imageFloodFill.getPixel(
//                            event.x.toInt() * imageFloodFill.width / binding.imgTest.width,
//                            event.y.toInt() * imageFloodFill.width / binding.imgTest.width
//                        ),
//                        Color.RED
//                    )
//
//                    queueLinearFloodFiller.floodFill(
//                        event.x.toInt() * imageFloodFill.width / binding.imgTest.width,
//                        event.y.toInt() * imageFloodFill.width / binding.imgTest.width
//                    )
//
//                    binding.imgTest.setImageBitmap(queueLinearFloodFiller.image)
//
//                }
//
//                else -> {
//
//                }
//            }
//            return@setOnTouchListener true
//        }


        //   binding.imgTest.loadImage(bitmap = queueLinearFloodFiller.image)

    }

    fun randomColor(): Int {
        // Generate a random color with full alpha (opacity)
        return Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }

    fun minPointX(image: Bitmap, node: Point, targetColor: Int, replacementColor: Int) {
        val listPoint = mutableListOf<Point>()
        var pointOrigin = Point(0, 0)
        var pointCurrent = Point(0, 0)
        var x = node.x
        var y = node.y

        var isDown = true
        var isLeft = true

        while (x > 0 && image.getPixel(x - 1, y) == targetColor) {
            x--
        }
        pointOrigin = Point(x, y)
        listPoint.add(pointOrigin)

        while (pointCurrent != pointOrigin) {
            if (isDown) {
                if (image.getPixel(x, y + 1) == targetColor) {
                    if (isLeft) {
                        while (x > 0 && image.getPixel(x - 1, y) == targetColor) {
                            x--
                        }
                        pointCurrent = Point(x, y)
                        listPoint.add(pointCurrent)
                    } else {

                    }
                } else {
                    isDown = false
                }
            }


        }


    }
}