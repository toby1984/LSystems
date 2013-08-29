/**
 * Copyright 2013 Tobias Gierke <tobias.gierke@code-sourcery.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codesourcery.lsystems.rendering;

import de.codesourcery.lsystems.lsystem.LSystem;

/**
 * Implementations of this interface know how to create a visual representation of
 * an L-System.
 *
 * @author tobias.gierke@code-sourcery.de
 */
public interface LSystemRenderer 
{
	/**
	 * Renders an L-System using a specific rendering context.
	 * 
	 * @param system
	 * @param context
	 */
	public void render(LSystem system,RenderingContext2D context);
	
	public void setTokenTranslator(TokenTranslator translator);
}
