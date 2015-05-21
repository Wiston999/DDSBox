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

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Victor
 * Date: 3/11/13
 * Time: 21:22
 * To change this template use File | Settings | File Templates.
 */
public class DishaDaemon implements Runnable{
    private volatile Boolean stop = false;
    public void DishaDeamon(MainController mC){

    }

    public void stop(){
        stop = true;
        this.setLastOnline();
    }

    public void run(){
        while(!stop){
            this.setLastOnline();
            try {
                Thread.sleep(1000*60*1);    //1 minutos
            } catch (InterruptedException e) {
                this.setLastOnline();
                return;
            }
        }
    }

    private void setLastOnline(){
        java.util.Date date= new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        InternalDBController.getInstance().saveConfigParam("lastOnline", timestamp.toString());
    }
}
