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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.kdgregory.pathfinder.core.PathRepo;
import com.kdgregory.pathfinder.core.WarMachine;


/**
 *  This class contains the logic for inspecting beans defined in XML.
 */
public class BeanInspector
{
    private Logger logger = Logger.getLogger(getClass());

    private WarMachine war;
    private SpringContext context;
    private PathRepo paths;


    public BeanInspector(WarMachine war, SpringContext context, PathRepo paths)
    {
        this.war = war;
        this.context = context;
        this.paths = paths;
    }

//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  This is the entry point to the class.
     */
    public void inspect(String urlPrefix)
    {
        processSimpleUrlHandlerMappings(urlPrefix);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private void processSimpleUrlHandlerMappings(String urlPrefix)
    {
        List<BeanDefinition> defs = context.getBeansByClass(SpringConstants.SIMPLE_URL_HANDLER_CLASS);
        logger.debug("found " + defs.size() + " SimpleUrlHandlerMapping beans");

        for (BeanDefinition def : defs)
        {
            Properties mappings = def.getPropertyAsProperties("mappings");
            if ((mappings == null) || mappings.isEmpty())
            {
                logger.debug("SimpleUrlHandlerMapping bean " + def.getBeanName() + " has no mappings");
                continue;
            }

            logger.debug("SimpleUrlHandlerMapping bean " + def.getBeanName() + " has " + mappings.size() + " mappings");
            for (Map.Entry<Object,Object> mapping : mappings.entrySet())
            {
                String url = urlPrefix + mapping.getKey();
                String beanName = String.valueOf(mapping.getValue());
                BeanDefinition bean = context.getBean(beanName);
                logger.debug("mapped " + url + " to bean " + beanName);
                paths.put(url, new SpringDestination(bean));
            }
        }
    }
}
