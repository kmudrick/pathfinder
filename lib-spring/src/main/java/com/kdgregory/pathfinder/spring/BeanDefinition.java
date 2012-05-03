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

import org.w3c.dom.Element;


/**
 *  Holds information extracted from the bean definition. The amount of
 *  information available depends on how the bean was defined: wehether
 *  by XML or a component scan. All beans will have at least name and
 *  class.
 */
public class BeanDefinition
{

    private String beanName;
    private String beanClass;
    private Element beanDef;

    /**
     *  Called for beans defined in XML; will extract information from the
     *  XML subtree, and retain a reference to the tree.
     */
    public BeanDefinition(Element def)
    {
        beanName = def.getAttribute("id").trim();
        beanClass = def.getAttribute("class").trim();
        beanDef = def;
    }


    public String getBeanName()
    {
        return beanName;
    }


    public String getBeanClass()
    {
        return beanClass;
    }


    public Element getBeanDef()
    {
        return beanDef;
    }
}
