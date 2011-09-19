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

package org.teatrove.barista.util;

import org.teatrove.trove.util.PropertyMap;
import org.teatrove.trove.util.ThreadPool;
import org.teatrove.trove.log.Log;

/**
 * @author Brian S O'Neill
 */
public class ConfigWrapper implements Config {
    private final Config mConfig;

    public ConfigWrapper(Config config) {
        mConfig = config;
    }

    public PropertyMap getProperties() {
        return mConfig.getProperties();
    }

    public Log getLog() {
        return mConfig.getLog();
    }

    public ThreadPool getThreadPool() {
        return mConfig.getThreadPool();
    }
}