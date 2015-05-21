
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package es.ugr.ddsbox.idl;
        
import com.rti.dds.typecode.*;


public class UserTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[6];

        sm[i]=new StructMember("uuid",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("userName",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("realName",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("email",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.nameTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("publicRSA",false,(short)-1,false,(TypeCode)es.ugr.ddsbox.idl.parameterTypeTypeCode.VALUE); i++;
        sm[i]=new StructMember("online",false,(short)-1,false,(TypeCode)TypeCode.TC_SHORT); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("es::ugr::ddsbox::idl::User",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,sm);
        return tc;
    }
}
