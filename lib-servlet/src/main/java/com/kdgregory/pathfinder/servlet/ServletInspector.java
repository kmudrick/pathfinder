// Copyright (c) Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.pathfinder.servlet;

import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.kdgcommons.lang.ObjectUtil;

import com.kdgregory.pathfinder.core.Inspector;
import com.kdgregory.pathfinder.core.PathRepo;
import com.kdgregory.pathfinder.core.PathRepo.Destination;
import com.kdgregory.pathfinder.core.WarMachine;
import com.kdgregory.pathfinder.core.WarMachine.ServletMapping;


public class ServletInspector
implements Inspector
{
    private Logger logger = Logger.getLogger(getClass());

//----------------------------------------------------------------------------
//  Inspector
//----------------------------------------------------------------------------

    @Override
    public void inspect(WarMachine war, PathRepo paths)
    {
        logger.info("ServletInspector started");
        addServlets(war, paths);
        addJSPandHTML(war, paths);
        logger.info("ServletInspector finished");
    }


//----------------------------------------------------------------------------
//  The Destinations that we support
//----------------------------------------------------------------------------

    private static class ServletDestination
    implements Destination
    {
        private String servletClass;

        public ServletDestination(String servletClass)
        {
            this.servletClass = servletClass;
        }

        @Override
        public String toString()
        {
            return servletClass;
        }

        @Override
        public boolean isImplementedBy(String className)
        {
            return ObjectUtil.equals(className, servletClass);
        }
    }


    private static class JspDestination
    implements Destination
    {
        private String filename;

        public JspDestination(String filename)
        {
            this.filename = filename;
        }

        @Override
        public String toString()
        {
            return filename;
        }

        @Override
        public boolean isImplementedBy(String className)
        {
            return false;
        }
    }


    private static class HtmlDestination
    implements Destination
    {
        private String filename;

        public HtmlDestination(String filename)
        {
            this.filename = filename;
        }

        @Override
        public String toString()
        {
            return filename;
        }

        @Override
        public boolean isImplementedBy(String className)
        {
            return false;
        }
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private void addServlets(WarMachine war, PathRepo paths)
    {
        for (Map.Entry<String,ServletMapping> servlet : war.getServletMappings().entrySet())
        {
            String servletUrl = servlet.getKey();
            String servletClass = servlet.getValue().getServletClass();

            logger.debug("added servlet: " + servletUrl + " => " + servletClass);
            paths.put(servlet.getKey(), new ServletDestination(servletClass));
        }
    }


    private void addJSPandHTML(WarMachine war, PathRepo paths)
    {
        for (String filename : war.getPublicFiles())
        {
            filename = filename.toLowerCase();
            if (filename.endsWith(".jsp"))
            {
                logger.debug("added JSP: " + filename);
                paths.put(filename, new JspDestination(filename));
            }
            if (filename.endsWith(".html") || filename.endsWith(".htm"))
            {
                logger.debug("added static HTML: " + filename);
                paths.put(filename, new HtmlDestination(filename));
            }
        }
    }

}
