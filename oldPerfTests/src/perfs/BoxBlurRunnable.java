/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.cojac.perfs;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import com.github.cojac.perfs.image.BoxBlurFilter;

public class BoxBlurRunnable implements Runnable {
    private BufferedImage image;

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void run() {
        BufferedImageOp filter = new BoxBlurFilter();

        BufferedImage dest = filter.createCompatibleDestImage(image, image.getColorModel());

        filter.filter(image, dest);
    }
}