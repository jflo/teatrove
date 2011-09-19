/*
 *  Copyright 1997-2011 teatrove.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.teatrove.trove.classfile;

import java.io.*;

/**
 * This class corresponds to the ConstantValue_attribute structure as defined
 * in section 4.7.3 of <i>The Java Virtual Machine Specification</i>.
 * 
 * @author Brian S O'Neill
 * @version
 * <!--$$Revision:--> 23 <!-- $-->, <!--$$JustDate:--> 00/11/27 <!-- $-->
 */
class ConstantValueAttr extends Attribute {
    private ConstantInfo mConstant;
    
    public ConstantValueAttr(ConstantPool cp, ConstantInfo constant) {
        super(cp, CONSTANT_VALUE);
        
        mConstant = constant;
    }
    
    public ConstantInfo getConstant() {
        return mConstant;
    }
    
    public int getLength() {
        return 2;
    }
    
    public void writeDataTo(DataOutput dout) throws IOException {
        dout.writeShort(mConstant.getIndex());
    }

    static Attribute define(ConstantPool cp,
                            String name,
                            int length,
                            DataInput din) throws IOException {
        
        int index = din.readUnsignedShort();
        if ((length -= 2) > 0) {
            din.skipBytes(length);
        }

        return new ConstantValueAttr(cp, cp.getConstant(index));
    }
}