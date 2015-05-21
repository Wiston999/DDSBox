
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package es.ugr.ddsbox.idl;
        

import com.rti.dds.infrastructure.Copyable;

import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;


public class Command implements Copyable, Serializable
{

    public String userUuid = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public short idCommand;
    public String parameters = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_PARAMETER.VALUE)) */


    public Command() {

    }


    public Command(Command other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        return new Command();
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        Command otherObj = (Command)o;



        if(!userUuid.equals(otherObj.userUuid)) {
            return false;
        }
            
        if(idCommand != otherObj.idCommand) {
            return false;
        }
            
        if(!parameters.equals(otherObj.parameters)) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += userUuid.hashCode();
                
        __result += (int)idCommand;
                
        __result += parameters.hashCode();
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>CommandTypeSupport</code>
     * rather than here by using the <code>-noCopyable</code> option
     * to rtiddsgen.
     * 
     * @param src The Object which contains the data to be copied.
     * @return Returns <code>this</code>.
     * @exception NullPointerException If <code>src</code> is null.
     * @exception ClassCastException If <code>src</code> is not the 
     * same type as <code>this</code>.
     * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
     */
    public Object copy_from(Object src) {
        

        Command typedSrc = (Command) src;
        Command typedDst = this;

        typedDst.userUuid = typedSrc.userUuid;
            
        typedDst.idCommand = typedSrc.idCommand;
            
        typedDst.parameters = typedSrc.parameters;
            
        return this;
    }


    
    public String toString(){
        return toString("", 0);
    }
        
    
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        
                        
        
        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }
        
        
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("userUuid: ").append(userUuid).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("idCommand: ").append(idCommand).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("parameters: ").append(parameters).append("\n");
            
        return strBuffer.toString();
    }
    
}

