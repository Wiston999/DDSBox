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

public class Segment implements Serializable {
    private String fileUuid;
    private int idSegment;
    private byte[] content;

    public Segment(){

    }

    public Segment(String fileUuid, int idSegment, byte[] content){
        this.fileUuid = fileUuid;
        this.idSegment = idSegment;
        this.content = content;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public int getIdSegment() {
        return idSegment;
    }

    public void setIdSegment(int idSegment) {
        this.idSegment = idSegment;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.writeObject(fileUuid);
        o.writeObject(idSegment);
        o.writeObject(content);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        fileUuid = (String) o.readObject();
        idSegment = (Integer) o.readObject();
        content = (byte[]) o.readObject();
    }
}
