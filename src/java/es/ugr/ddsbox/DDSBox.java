/*
 * Copyright (c) 2014. Olmo Jiménez Alaminos, Víctor Cabezas Lucena.
 *
 * This file is part of DDSBox.
 *
 * DDSBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DDSBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DDSBox.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.ddsbox;

import es.ugr.ddsbox.ui.ConsoleUI;
import es.ugr.ddsbox.ui.GraphicUI;

public class DDSBox {
    public static void main(String[] args) throws Exception {

        if(args.length == 2 && args[0].equals("-t")){
            ConsoleUI cui = new ConsoleUI();
            cui.run();
        }
        else{
            GraphicUI.run();
        }

    }
}
