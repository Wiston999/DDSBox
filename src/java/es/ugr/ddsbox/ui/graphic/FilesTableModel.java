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

import es.ugr.ddsbox.models.File;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilesTableModel extends AbstractTableModel {
    private String[] columnNames = {"Name", "Size", "Completed"};
    private ArrayList<RowData > rows;
    private Map<String, RowData> mapLookup;

    public FilesTableModel() {
        rows = new ArrayList<RowData>();
        mapLookup = new HashMap<String, RowData>();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RowData rowData = rows.get(rowIndex);
        Object value = null;
        switch (columnIndex) {
            case 0:
                value = rowData.getFile();
                break;
            case 1:
                value = rowData.getSize();
                break;
            case 2:
                value = rowData.getStatus();
                break;
        }
        return value;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        RowData rowData = rows.get(rowIndex);
        switch (columnIndex) {
            case 3:
                if (aValue instanceof Float) {
                    rowData.setStatus((Float) aValue);
                }
                break;
        }
    }

    public void addFile(File file) {
        RowData rowData = new RowData(file);
        mapLookup.put(file.getName(), rowData);
        rows.add(rowData);
        fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    public void updateStatus(File file, float progress) {
        RowData rowData = mapLookup.get(file.getName());
        if (rowData != null) {
            int row = rows.indexOf(rowData);
            setValueAt(progress, row, 3);
            fireTableCellUpdated(row, 3);
        }
        else{
            addFile(file);
        }
    }

    public class RowData {
        private String name;
        private String folder;
        private long size;
        private float status;

        public RowData(File file) {
            this.name = file.getName();
            //this.folder = folder.getName();
            this.size = file.getSize();
            this.status = 0f;
        }

        public String getFile() {
            return name;
        }

        public String getFolder() {
            return folder;
        }

        public long getSize() {
            return size;
        }

        public float getStatus() {
            return status;
        }

        public void setStatus(float status) {
            this.status = status;
        }
    }

    public static class ProgressCellRender extends JProgressBar implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int progress = 0;
            if (value instanceof Float) {
                progress = Math.round(((Float) value) * 100f);
            } else if (value instanceof Integer) {
                progress = (Integer) value;
            }
            setValue(progress);
            return this;
        }

    }

}
