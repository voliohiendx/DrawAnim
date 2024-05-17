package com.volio.vn.b1_project.ui.splash

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import androidx.fragment.app.viewModels
import com.tencent.mmkv.MMKV
import com.volio.draw.draw.DrawLayout
import com.volio.draw.model.FrameModel
import com.volio.draw.model.ProjectModel
import com.volio.draw.model.TypeCubes
import com.volio.draw.model.TypeDraw
import com.volio.draw.saveBitmapToInternalStorage
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentSplashBinding
import com.volio.vn.b1_project.ui.loadImage
import com.volio.vn.b1_project.utils.MMKVKey
import com.volio.vn.common.utils.delay
import com.volio.vn.common.utils.getScreenWidth
import com.volio.vn.common.utils.setPreventDoubleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                .encode(MMKVKey.DATA_DRAW, FrameModel(binding.drawView.getDataDraw()))
        }

        binding.tvSetData.setPreventDoubleClick {
            MMKV.defaultMMKV().decodeParcelable(MMKVKey.DATA_DRAW, FrameModel::class.java)?.let {
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

        binding.tvZoomIn.setPreventDoubleClick {
            binding.drawView.zoomIn()
        }

        binding.tvZoomOut.setPreventDoubleClick {
            binding.drawView.zoomOut()
        }

        binding.tvFill.setPreventDoubleClick {
            binding.drawView.fillOn()
        }

        binding.tvCircle.setPreventDoubleClick {
            binding.drawView.cubes(TypeCubes.CIRCLE)
        }

        binding.tvSquare.setPreventDoubleClick {
            binding.drawView.cubes(TypeCubes.SQUARE)
        }

        binding.tvLine.setPreventDoubleClick {
            binding.drawView.cubes(TypeCubes.LINE)
        }

        binding.tvAddFrame.setPreventDoubleClick {
            CoroutineScope(Dispatchers.IO).launch {
                val data = mutableListOf<FrameModel>()
                MMKV.defaultMMKV().decodeParcelable(MMKVKey.DATA_PROJECT, ProjectModel::class.java)
                    ?.let {
                        data.addAll(it.frames)
                    }

                data.add(FrameModel(binding.drawView.getDataDraw()))

                MMKV.defaultMMKV().encode(MMKVKey.DATA_PROJECT, ProjectModel(data))
            }
        }

        binding.tvShowRandom.setPreventDoubleClick {
            MMKV.defaultMMKV().decodeParcelable(MMKVKey.DATA_PROJECT, ProjectModel::class.java)
                ?.let {
                    val data = it.frames.random()
                    binding.drawView.setData(data.data)
                }
        }

        binding.tvPlay.setPreventDoubleClick {
            MMKV.defaultMMKV().decodeParcelable(MMKVKey.DATA_PROJECT, ProjectModel::class.java)
                ?.let {
                    var check = 0
                    CoroutineScope(Dispatchers.IO).launch {
                        while (check < it.frames.size) {
                            withContext(Dispatchers.Main) {
                                binding.drawView.setData(it.frames[check].data)
                            }
                            check++
                            kotlinx.coroutines.delay(125L)

                            if (check == it.frames.size) check = 0
                        }
                    }
                }
        }

        binding.tvExport.setPreventDoubleClick {
            MMKV.defaultMMKV().decodeParcelable(MMKVKey.DATA_PROJECT, ProjectModel::class.java)
                ?.let {
                    it.frames.forEachIndexed { index, data ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val viewWidth = (getScreenWidth() * 0.8).toInt()
                            val viewHeight = viewWidth
                            val drawLayout = DrawLayout(requireContext()) {}

                            drawLayout.setViewSize(viewWidth, viewHeight)
                            drawLayout.setData(data.data)
                            val bitmapCache =
                                Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)

                            Canvas(bitmapCache).apply {
                                drawLayout.drawViewDraw(this, 0, data.data.size - 1)
                            }
                            bitmapCache.saveBitmapToInternalStorage(
                                requireContext(),
                                "${index}.png"
                            )
                        }
                    }

                }
        }

        binding.tvBackground.setPreventDoubleClick {
            binding.drawView.setBackgroundBitmap("https://c4.wallpaperflare.com/wallpaper/383/217/191/abstract-pattern-mosaic-design-wallpaper-preview.jpg")
        }


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