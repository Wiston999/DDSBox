
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


public class User implements Copyable, Serializable
{

    public String uuid = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String userName = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String realName = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String email = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_NAME.VALUE)) */
    public String publicRSA = ""; /* maximum length = ((es.ugr.ddsbox.idl.MAX_PARAMETER.VALUE)) */
    public short online;


    public User() {

    }


    public User(User other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        return new User();
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        User otherObj = (User)o;



        if(!uuid.equals(otherObj.uuid)) {
            return false;
        }
            
        if(!userName.equals(otherObj.userName)) {
            return false;
        }
            
        if(!realName.equals(otherObj.realName)) {
            return false;
        }
            
        if(!email.equals(otherObj.email)) {
            return false;
        }
            
        if(!publicRSA.equals(otherObj.publicRSA)) {
            return false;
        }
            
        if(online != otherObj.online) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += uuid.hashCode();
                
        __result += userName.hashCode();
                
        __result += realName.hashCode();
                
        __result += email.hashCode();
                
        __result += publicRSA.hashCode();
                
        __result += (int)online;
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>UserTypeSupport</code>
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
        

        User typedSrc = (User) src;
        User typedDst = this;

        typedDst.uuid = typedSrc.uuid;
            
        typedDst.userName = typedSrc.userName;
            
        typedDst.realName = typedSrc.realName;
            
        typedDst.email = typedSrc.email;
            
        typedDst.publicRSA = typedSrc.publicRSA;
            
        typedDst.online = typedSrc.online;
            
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
        strBuffer.append("uuid: ").append(uuid).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("userName: ").append(userName).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("realName: ").append(realName).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("email: ").append(email).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("publicRSA: ").append(publicRSA).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("online: ").append(online).append("\n");
            
        return strBuffer.toString();
    }
    
}

