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

import es.ugr.ddsbox.utils.FileUtils;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;

public class File implements Serializable {
    private String fileUuid = "";
    private String name = "";
    private String owner = "";
    private long size = 0;
    private int status;
    private ArrayList<Short> segments = new ArrayList<Short>();

    private long numSegments = 0;
    private long tamLastSegment = 0;
    private long segmentSize;
    private int version = 0;
    private Timestamp timeLastVersion;
    private boolean completed = false;
    private String hash = "";
    private String folderId;
    private boolean isDir = false;
    private int change;

    private static final int EXISTS = 0;
    private static final int NOEXISTS = 1;

    public File(){

    }

    public File(String n, String o){
        status = EXISTS;
        name = n;
        owner = o;

        if(FileUtils.isDirectory(name)){
            isDir = true;
        }
        else if(!FileUtils.exists(name)){
            status = NOEXISTS;
        }
        else{
            FileUtils.readInfo(this);
            completed = true;
            hash = FileUtils.getHashFromFile(name);
        }
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    void calcSegSize(){
        if(size<20*1024*1024)
            segmentSize = 64*1024;
        else if(size<100*1024*1024)
            segmentSize = 128*1024;
        else if(size<350*1024*1024)
            segmentSize = 256*1024;
        else if(size<512*1024*1024)
            segmentSize = 512*1024;
        else if(size<1024*1024*1024)
            segmentSize = 1024*1024;
        segmentSize = 1024*1024;
    }

    public String getStatus(){
        String ret = "";

        if(status == EXISTS)
            ret = "Exists";
        else if(status==NOEXISTS)
            ret = "No Exists";

        return ret;
    }

    public boolean isCompleted(){
        return completed;
    }

    public int isCompletedInt(){
        if(completed)
            return 1;
        else
            return 0;
    }

    public boolean checkCompleted(){
        int sum = 0;
        for(int i : segments){
            sum+= i;
        }
        completed = (sum == numSegments);
        return completed;
    }

    public float percentCompleted(){
        float sum = 0;
        for(int i : segments){
            sum += i;
        }

        return sum/numSegments;
    }

    public void setSize(long s){
        size = s;
        calcSegSize();

        numSegments = (size/segmentSize);
        if((size%segmentSize)>0){
            numSegments++;
            tamLastSegment = size%segmentSize;
        }
        else
            tamLastSegment = segmentSize;
    }

    public void setCompleted(boolean compt){
        completed = compt;

        //segments = new ArrayList<Short>((int)numSegments);
        for(int i=0; i<segments.size(); i++)
            segments.set(i, completed?(short)1:(short)0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDir() {
        return isDir;
    }

    public int isDirInt() {
        if(isDir)
            return 1;
        else
            return 0;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Timestamp getTimeLastVersion() {
        return timeLastVersion;
    }

    public void setTimeLastVersion(Timestamp timeLastVersion) {
        this.timeLastVersion = timeLastVersion;
    }

    public long getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(long segmentSize) {
        this.segmentSize = segmentSize;
    }

    public long getTamLastSegment() {
        return tamLastSegment;
    }

    public void setTamLastSegment(long tamLastSegment) {
        this.tamLastSegment = tamLastSegment;
    }

    public long getNumSegments() {
        return numSegments;
    }

    public void setNumSegments(long numSegments) {
        this.numSegments = numSegments;
    }

    public ArrayList<Short> getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = new ArrayList<Short>();

        for(int i=0; i<segments.length(); i++){
            if(segments.charAt(i) == '0')
                this.segments.add((short)0);
            else if(segments.charAt(i) == '1')
                this.segments.add((short)1);
        }
    }

    public long getSize(){
        return size;
    }

    public void setSegmentsArray(ArrayList<Short> segments){
        this.segments = segments;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.writeObject(name);
        o.writeObject(owner);
        o.writeObject(timeLastVersion);
        o.writeObject(size);
        o.writeObject(numSegments);
        o.writeObject(segmentSize);
        o.writeObject(tamLastSegment);
        o.writeObject(hash);
        o.writeObject(isDir);
        o.writeObject(folderId);
        o.writeObject(change);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        name = (String) o.readObject();
        owner = (String) o.readObject();
        timeLastVersion = (Timestamp) o.readObject();
        size = (Long) o.readObject();
        numSegments = (Long) o.readObject();
        segmentSize = (Long) o.readObject();
        tamLastSegment = (Long) o.readObject();
        hash = (String) o.readObject();
        isDir = (Boolean) o.readObject();
        folderId = (String) o.readObject();
        change = (Integer) o.readObject();
    }
}
