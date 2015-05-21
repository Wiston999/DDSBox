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

package es.ugr.ddsbox.models;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Command implements Serializable {
    private int idCommand;
    private String parameters;

    public Command(){

    }

    public Command(int idCommand, String parameters){
        this.idCommand = idCommand;
        this.parameters = parameters;
    }

    public int getIdCommand() {
        return idCommand;
    }

    public void setIdCommand(int idCommand) {
        this.idCommand = idCommand;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.writeObject(idCommand);
        o.writeObject(parameters);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        idCommand = (Integer) o.readObject();
        parameters = (String) o.readObject();
    }
}
