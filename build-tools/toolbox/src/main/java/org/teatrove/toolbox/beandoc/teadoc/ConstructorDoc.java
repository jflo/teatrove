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

package org.teatrove.toolbox.beandoc.teadoc;

/**
 *
 * @author Brian S O'Neill
 * @version
 * <!--$$Revision:--> 1 <!-- $-->, <!--$$JustDate:-->  3/01/01 <!-- $-->
 */
public class ConstructorDoc extends ExecutableMemberDoc {

    private com.sun.javadoc.ConstructorDoc mDoc;

    public static ConstructorDoc[] convert(RootDoc root,
                                           com.sun.javadoc.ConstructorDoc[]
                                           docs) {
        int length = docs.length;
        ConstructorDoc[] newDocs = new ConstructorDoc[length];
        for (int i=0; i<length; i++) {
            newDocs[i] = new ConstructorDoc(root, docs[i]);
        }
        return newDocs;
    }

    public ConstructorDoc(RootDoc root, com.sun.javadoc.ConstructorDoc doc) {
        super(root, doc);
        mDoc = doc;
    }

    public String getQualifiedName() {
        return mDoc.qualifiedName();
    }
}