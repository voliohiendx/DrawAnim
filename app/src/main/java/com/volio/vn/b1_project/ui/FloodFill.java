package com.volio.vn.b1_project.ui;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class FloodFill {
    public void floodFill(Bitmap image, Point node, int targetColor, int replacementColor, int replacementColor2) {
        Log.d("CheckX", "Start");
        int width = image.getWidth();
        int height = image.getHeight();

        int target = targetColor;
        int replacement = replacementColor;
        if (target != replacement) {
            Queue<Point> queue = new LinkedList<Point>();
            do {
                Log.d("CheckX", "Start");

                int x = node.x;
                int y = node.y;
                while (x > 0 && image.getPixel(x - 1, y) == target) {
                    x--;
                }
                Log.d("CheckX", ""+x);
                boolean spanUp = false;
                boolean spanDown = false;
                while (x < width && image.getPixel(x, y) == target) {
                    image.setPixel(x, y, replacementColor2);
                    if (!spanUp && y > 0 && image.getPixel(x, y - 1) == target) {
                        queue.add(new Point(x, y - 1));
                      //  image.setPixel(x, y - 1, replacementColor);
                        spanUp = true;
                    } else if (spanUp && image.getPixel(x, y - 1) != target) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1 && image.getPixel(x, y + 1) == target) {
                        queue.add(new Point(x, y + 1));
                       // image.setPixel(x, y + 1, replacementColor);
                        spanDown = true;
                    } else if (spanDown && y < height - 1 && image.getPixel(x, y + 1) != target) {
                        spanDown = false;
                    }
                    x++;
                }
            } while ((node = queue.poll()) != null);

        }
    }
}