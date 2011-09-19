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

package org.teatrove.teaservlet.io;

import java.io.File;

/**
 * A ByteData implementation that reads the contents of a file.
 *
 * @author Brian S O'Neill
 * @version
 * <!--$$Revision:--> 19 <!-- $-->, <!--$$JustDate:--> 00/12/06 <!-- $-->
 * @deprecated Moved to org.teatrove.trove.io package.
 */
public class FileByteData extends org.teatrove.trove.io.FileByteData
    implements ByteData
{
    public FileByteData(File file) {
        super(file);
    }
}