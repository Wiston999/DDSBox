
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package es.ugr.ddsbox.idl;
        
import com.rti.dds.typecode.*;


public class CommandTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[3];

        sm[i]=new StructMember("userUuid",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("idCommand",false,(short)-1,false,(TypeCode)TypeCode.TC_SHORT); i++;
        sm[i]=new StructMember("parameters",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.parameterTypeTypeCode.VALUE); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("es::ugr::ddsbox::idl::Command",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,sm);
        return tc;
    }
}
