/*
 * *
 *    Copyright 2011-2016 Frédéric Bapst & Valentin Gazzola
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
package com.github.cojac.models;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for indicating a specific Class, other than "java/lang/Math" in a behaviour.
 * annotation should be like this: @FromClass("java/lang/Double")
 * @author Gazzola Valentin
 *
 */
@Retention(RUNTIME)//so the annotation can be used reflexively
public @interface FromClass {
    String value();
}
