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

package es.ugr.ddsbox.ui.graphic;

import es.ugr.ddsbox.models.SharedFolder;
import es.ugr.ddsbox.models.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;

public class UsersTableModel extends AbstractTableModel {
    private String[] columnNames = {"Username", "Name", "Permission", "Id"};
    private ArrayList<User> data = new ArrayList<User>();
    private HashMap<String, Integer> permissions = new HashMap<String, Integer>();

    public UsersTableModel(ArrayList<User> users, HashMap<String, Integer> permissions){
        data = users;
        this.permissions = permissions;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        switch (columnIndex) {
            case 0:
                value = data.get(rowIndex).getUsername();
                break;
            case 1:
                value = data.get(rowIndex).getRealname();
                break;
            case 3:
                value = data.get(rowIndex).getUuid();
                break;
            case 2:
                int perm = permissions.get(data.get(rowIndex).getUuid());
                if(perm == SharedFolder.READER)
                    value = "Reader";
                else if(perm == SharedFolder.CONTRIBUTOR)
                    value = "Contributor";
                else if(perm == SharedFolder.EDITOR)
                    value = "Editor";
                else if(perm == SharedFolder.OWNER)
                    value = "Owner";
                break;
        }
        return value;
    }

    public User getUser(int rowIndex){
        return data.get(rowIndex);
    }

    public void add(User o, int permission){
        data.add(o);
        permissions.put(o.getUuid(), permission);
        this.fireTableDataChanged();
    }
}
