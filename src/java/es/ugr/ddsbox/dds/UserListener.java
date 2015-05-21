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

package es.ugr.ddsbox.dds;

import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.subscription.*;
import es.ugr.ddsbox.MainController;
import es.ugr.ddsbox.idl.*;

public class UserListener extends DataReaderAdapter {
    private UserSeq _dataSeq = new UserSeq();
    private SampleInfoSeq _infoSeq = new SampleInfoSeq();
    private MainController mainController;

    public UserListener(MainController mc){
        mainController = mc;
    }

    public void on_data_available(DataReader reader){
        UserDataReader userReader = (UserDataReader)reader;

        try {
            userReader.take(
                    _dataSeq, _infoSeq,
                    ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                    SampleStateKind.ANY_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE);

            for(int i = 0; i < _dataSeq.size(); ++i) {
                SampleInfo info = (SampleInfo)_infoSeq.get(i);

                if (info.valid_data) {
                    mainController.userReceived((User) _dataSeq.get(i));
                }
            }
        } catch (RETCODE_NO_DATA noData) {
            // No data to process
        } finally {
            userReader.return_loan(_dataSeq, _infoSeq);
        }
    }
}
