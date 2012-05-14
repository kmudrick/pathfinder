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

import net.sf.practicalxml.xpath.XPathWrapper;
import net.sf.practicalxml.xpath.XPathWrapperFactory;
import net.sf.practicalxml.xpath.XPathWrapperFactory.CacheType;


/**
 *  Holds an <code>XPathWrapperFactory</code> instance that's pre-configured
 *  for the various Spring namespaces, and which has a simple expression cache.
 *  The namespaces are accessible with the following prefixes (which are also
 *  exposed as constants):
 *  
 *  <table>
 *  <tr><th>Prefix
 *      <th>Namespace
 *      <th>Constant
 *  <tr><td>b
 *      <td>
 *      <td>{@link #NS_BEANS}
 *  <tr><td>
 *      <td>
 *      <td>
 *  </table>
 */
public class SpringXPathFactory
{
//----------------------------------------------------------------------------
//  Namespace Constants
//----------------------------------------------------------------------------
    
    public final static String  NS_BEANS    = "http://www.springframework.org/schema/beans";
    
    
//----------------------------------------------------------------------------
//  Factory implementation
//----------------------------------------------------------------------------
    
    private XPathWrapperFactory fact;
    
    public SpringXPathFactory()
    {
        fact = new XPathWrapperFactory(CacheType.SIMPLE)
               .bindNamespace("b", NS_BEANS);
    }
    
    
    public XPathWrapper newXPath(String expr)
    {
        return fact.newXPath(expr);
    }
}
