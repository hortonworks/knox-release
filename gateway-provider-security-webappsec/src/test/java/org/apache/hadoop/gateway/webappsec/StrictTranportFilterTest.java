/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.gateway.webappsec;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.gateway.webappsec.filter.StrictTranportFilter;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class StrictTranportFilterTest {
  /**
   * 
   */
  private static final String STRICT_TRANSPORT = "Strict-Transport-Security";
  String options = null;
  Collection<String> headerNames = null;
  Collection<String> headers = null;

  @Test
  public void testDefaultOptionsValue() throws Exception {
    try {
      StrictTranportFilter filter = new StrictTranportFilter();
      Properties props = new Properties();
      props.put("strict.transport.enabled", "true");
      filter.init(new TestFilterConfig(props));

      HttpServletRequest request = EasyMock.createNiceMock(
          HttpServletRequest.class);
      HttpServletResponse response = EasyMock.createNiceMock(
          HttpServletResponse.class);
      EasyMock.replay(request);
      EasyMock.replay(response);

      TestFilterChain chain = new TestFilterChain();
      filter.doFilter(request, response, chain);
      Assert.assertTrue("doFilterCalled should not be false.",
          chain.doFilterCalled );
      Assert.assertTrue("Options value incorrect should be max-age=31536000 but is: "
          + options, "max-age=31536000".equals(options));

      Assert.assertTrue("Strict-Transport-Security count not equal to 1.", headers.size() == 1);
    } catch (ServletException se) {
      fail("Should NOT have thrown a ServletException.");
    }
  }

  @Test
  public void testConfiguredOptionsValue() throws Exception {
    try {
      StrictTranportFilter filter = new StrictTranportFilter();
      Properties props = new Properties();
      props.put("strict.transport.enabled", "true");
      props.put("strict.transport", "max-age=31536010; includeSubDomains");
      filter.init(new TestFilterConfig(props));

      HttpServletRequest request = EasyMock.createNiceMock(
          HttpServletRequest.class);
      HttpServletResponse response = EasyMock.createNiceMock(
          HttpServletResponse.class);
      EasyMock.replay(request);
      EasyMock.replay(response);

      TestFilterChain chain = new TestFilterChain();
      filter.doFilter(request, response, chain);
      Assert.assertTrue("doFilterCalled should not be false.",
          chain.doFilterCalled );
      Assert.assertTrue("Options value incorrect should be max-age=31536010; includeSubDomains but is: "
          + options, "max-age=31536010; includeSubDomains".equals(options));

      Assert.assertTrue("Strict-Transport-Security count not equal to 1.", headers.size() == 1);
    } catch (ServletException se) {
      fail("Should NOT have thrown a ServletException.");
    }
  }

  class TestFilterConfig implements FilterConfig {
    Properties props = null;

    public TestFilterConfig(Properties props) {
      this.props = props;
    }

    @Override
    public String getFilterName() {
      return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.FilterConfig#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
      return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.FilterConfig#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
      return props.getProperty(name, null);
    }

    /* (non-Javadoc)
     * @see javax.servlet.FilterConfig#getInitParameterNames()
     */
    @Override
    public Enumeration<String> getInitParameterNames() {
      return null;
    }
    
  }

  class TestFilterChain implements FilterChain {
    boolean doFilterCalled = false;

    /* (non-Javadoc)
     * @see javax.servlet.FilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, ServletException {
      doFilterCalled = true;
      options = ((HttpServletResponse)response).getHeader(STRICT_TRANSPORT);
      headerNames = ((HttpServletResponse)response).getHeaderNames();
      headers = ((HttpServletResponse)response).getHeaders(STRICT_TRANSPORT);
    }
    
  }

}
