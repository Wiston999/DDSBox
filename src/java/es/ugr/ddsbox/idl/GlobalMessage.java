
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


public class GlobalMessage implements Copyable, Serializable
{

    public String userName = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String text = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public int timestamp;


    public GlobalMessage() {

    }


    public GlobalMessage(GlobalMessage other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        return new GlobalMessage();
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        GlobalMessage otherObj = (GlobalMessage)o;



        if(!userName.equals(otherObj.userName)) {
            return false;
        }
            
        if(!text.equals(otherObj.text)) {
            return false;
        }
            
        if(timestamp != otherObj.timestamp) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += userName.hashCode();
                
        __result += text.hashCode();
                
        __result += (int)timestamp;
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>GlobalMessageTypeSupport</code>
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
        

        GlobalMessage typedSrc = (GlobalMessage) src;
        GlobalMessage typedDst = this;

        typedDst.userName = typedSrc.userName;
            
        typedDst.text = typedSrc.text;
            
        typedDst.timestamp = typedSrc.timestamp;
            
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
        strBuffer.append("userName: ").append(userName).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("text: ").append(text).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("timestamp: ").append(timestamp).append("\n");
            
        return strBuffer.toString();
    }
    
}

