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

package org.teatrove.barista.udp;

import javax.servlet.*;

/**
 * @author Brian S O'Neill
 */
public class NotFoundServlet extends GenericServlet {
    private ServletConfig mConfig;


    public void init(ServletConfig config) {
        mConfig = config;
    }

    public void destroy() {
    }

    public ServletConfig getServletConfig() {
        return mConfig;
    }

    public String getServletInfo() {
        return "Filter chain terminator";
    }

    public void service(ServletRequest request,
                        ServletResponse response)
        throws java.io.IOException
    {
        //response.sendError(response.SC_NOT_FOUND, request.getRequestURI());
    }
}