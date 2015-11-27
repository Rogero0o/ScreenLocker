/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roger.screenlocker.welcome;

public interface LogoPaths {
    public static final String[] GLYPHS = {

            "M160.334,280.068l0,-175l-40,0l-40,120l-40,-120l-40,0l0,175l30,0l0,-135l45,135l10,0l45,-135l0,135z",//M

            "M290.334,280.068l-60,-75l60,0l0,-100l-120,0l0,175l30,0l0,-145l60,0l0,50l-60,0l0,20l60,75z",//R

            "M320.334,280.068l0,-20l-20,0l0,20z",//.

            "M420.334,280.068l0,-40l-40,0l0,-135l-40,0l0,175z",//L

            "M530.334,280.068l0,-175l-100,0l0,175l100,0l-35,-30l-30,0l0,-115l30,0l0,115l35,30z",//O

            "M630.334,280.068l0,-40l-50,0l0,-95l50,0l0,-40l-80,0l0,175z",//C

            "M770.334,280.068l-60,-95l60,-80l-30,0l-60,80l0,-80l-30,0l0,175l30,0l0,-65l15,0l45,65z",//K

            "M870.334,280.068l0,-35l-60,0l0,-35l60,0l0,-35l-60,0l0,-35l60,0l0,-35l-90,0l0,175z",//E

            "M1000.334,280.068l-60,-75l60,0l0,-100l-120,0l0,175l30,0l0,-145l60,0l0,50l-60,0l0,20l60,75z",//R
    };

    /**
     * 坐标X最大值160，Y最大值175，使用相对坐标，只需要调整M后第一个起点的位置即可
     */
}
