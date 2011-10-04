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

package ch.eiafr.cojac.models;

public enum ReactionType {
    PRINT(0),
    PRINT_SMALLER(1),
    LOG(2),
    LOG_SMALLER(3),
    EXCEPTION(4),
    CALLBACK(5);

    private final int value;

    ReactionType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ReactionType get(int value) {
        for (ReactionType reactionType : values()) {
            if (reactionType.value() == value) {
                return reactionType;
            }
        }

        return null;
    }
}