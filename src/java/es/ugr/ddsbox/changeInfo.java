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

/**
 * Created with IntelliJ IDEA.
 * User: Victor
 * Date: 12/10/13
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
public class changeInfo{
    public String filename;
    public FileSystemController.changes code;
    public boolean isDir;

    public changeInfo(String filename, FileSystemController.changes code, boolean dir){
        this.filename = filename;
        this.code = code;
        this.isDir = dir;
    }
}
