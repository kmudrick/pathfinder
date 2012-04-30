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

package com.kdgregory.pathfinder.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.kdgregory.pathfinder.core.Inspector;
import com.kdgregory.pathfinder.core.PathRepo;
import com.kdgregory.pathfinder.core.PathRepo.Destination;
import com.kdgregory.pathfinder.core.PathRepo.HttpMethod;
import com.kdgregory.pathfinder.core.WarMachine;


/**
 *  Finds any mappings to Spring's DispatcherServlet, and replaces them with mappings
 *  found by inspecting the various Spring context files. Supports both XML-driven and
 *  Annotation-driven configuration; does not support (at presnt) Code-driven config.
 */
public class SpringInspector
implements Inspector
{
    private Logger logger = Logger.getLogger(getClass());

//----------------------------------------------------------------------------
//  The Destinations that we support
//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
//  Inspector Implementation
//----------------------------------------------------------------------------

    @Override
    public void inspect(WarMachine war, PathRepo paths)
    {
        logger.info("SpringInspector started");
        List<String> springMappings = extractSpringMappings(paths);
        logger.debug("extracted " + springMappings.size() + " Spring mappings");
        logger.info("SpringInspector finished");
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private List<String> extractSpringMappings(PathRepo paths)
    {
        List<String> result = new ArrayList<String>();
        for (String url : paths)
        {
            Destination dest = paths.get(url, HttpMethod.ALL);
            if (dest.isImplementedBy("org.springframework.web.servlet.DispatcherServlet"))
            {
                result.add(url);
                paths.remove(url, HttpMethod.ALL);
            }
        }
        return result;
    }

}
