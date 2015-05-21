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
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

public class SharedFolder implements Serializable {
    private int id;
    private String path;
    private String name;
    private int permission;
    private int type;
    private String owner;
    private String uuid;
    private boolean suscribed;
    private int persistence;
    private String key = null;

    private HashMap<String, Integer> usersPermission = new HashMap<String, Integer>();

    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;

    public static final int READER = 0;
    public static final int CONTRIBUTOR = 1;
    public static final int EDITOR = 2;
    public static final int OWNER = 3;

    public static final int PERSISTENT = 0;
    public static final int TRANSIENT = 1;
    public static final int VOLATILE = 2;

    public SharedFolder(){

    }

    public SharedFolder(int id, String path, String name, int type, int permission, String owner, String uuid, boolean suscribed, int persistence, String key){
        this.id = id;
        this.path = path;
        this.name = name;
        this.type = type;
        this.permission = permission;
        this.owner = owner;
        this.uuid = uuid;
        this.suscribed = suscribed;
        this.persistence = persistence;
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public HashMap<String, Integer> getUsersPermissionMap(){
        return this.usersPermission;
    }

    public Integer getUsersPermission(String userUuid) {
        try{
            return this.usersPermission.get(userUuid);
        }
        catch (Exception e){
            return null;
        }
    }

    public void setUsersPermission(String userUuid, int permission) {
        try{
            this.usersPermission.put(userUuid, permission);
        }
        catch (Exception e){

        }
    }

    public int getPersistence() {
        return persistence;
    }

    public void setPersistence(int persistence) {
        this.persistence = persistence;
    }

    public boolean isSuscribed() {
        return suscribed;
    }

    public void setSuscribed(boolean suscribed) {
        this.suscribed = suscribed;
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.writeObject(uuid);
        o.writeObject(name);
        o.writeObject(type);
        o.writeObject(owner);
        o.writeObject(permission);
        o.writeObject(usersPermission);
        o.writeObject(key);
        o.writeObject(persistence);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        uuid = (String) o.readObject();
        name = (String) o.readObject();
        type = (Integer) o.readObject();
        owner = (String) o.readObject();
        permission = (Integer) o.readObject();
        usersPermission = (HashMap<String, Integer>) o.readObject();
        key = (String) o.readObject();
        persistence = (Integer) o.readObject();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
