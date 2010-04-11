/**
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.easymock.itests;

import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.jar.Manifest;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.internal.MocksControl;
import org.easymock.internal.MocksControl.MockType;
import org.easymock.internal.matchers.Equals;
import org.objenesis.Objenesis;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.util.OsgiStringUtils;

/**
 * Note: This is a JUnit 3 test because of the Spring OSGi test framework
 * 
 * @author Henri Tremblay
 */
public class OsgiTest extends AbstractConfigurableBundleCreatorTests {

    private final EasyMockSupport support = new EasyMockSupport();

    public <T> T createMock(Class<T> toMock) {
        return support.createMock(toMock);
    }

    public void replayAll() {
        support.replayAll();
    }

    public void verifyAll() {
        support.verifyAll();
    }

    @Override
    protected String[] getTestBundlesNames() {

        ClassLoader cl = String.class.getClassLoader();
        final String ceVersion = EasyMock.class.getPackage()
                .getImplementationVersion();
        final String cglibVersion = "2.2.0";
        final String objenesisVersion = Objenesis.class.getPackage()
                .getImplementationVersion();
        final String easymockVersion = org.easymock.EasyMock.class.getPackage()
                .getImplementationVersion();

        return new String[] {
                "net.sourceforge.cglib, com.springsource.net.sf.cglib, "
                        + cglibVersion,
                "org.easymock, easymock, " + easymockVersion,
                "org.objenesis, objenesis, " + objenesisVersion };
    }

    @Override
    protected Manifest getManifest() {
        Manifest mf = super.getManifest();

        String imports = mf.getMainAttributes().getValue(
                Constants.IMPORT_PACKAGE);
        imports = imports.replace("org.easymock.internal,",
                "org.easymock.internal;poweruser=true,");
        imports = imports.replace("org.easymock.internal.matchers,",
                "org.easymock.internal.matchers;poweruser=true,");

        imports += ",net.sf.cglib.reflect,net.sf.cglib.core,net.sf.cglib.proxy";

        mf.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, imports);

        return mf;
    }

    public void testOsgiPlatformStarts() throws Exception {
        System.out.println("Framework vendor: "
                + this.bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        System.out.println("Framework version: "
                + bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        System.out.println("Execution environment: "
                + bundleContext
                        .getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

        System.out.println("-----------------------------");

        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            System.out.println(OsgiStringUtils.nullSafeName(bundles[i]) + ": "
                    + bundles[i].getHeaders().get(Constants.BUNDLE_VERSION));
        }
    }

    public void testCanMock() throws IOException {
        Appendable mock = createMock(Appendable.class);
        expect(mock.append("test")).andReturn(mock);
        replayAll();
        assertSame(mock, mock.append("test"));
        verifyAll();
    }

    public void testCanUseMatchers() {
        Equals equals = new Equals(new Object());
    }

    public void testCanUseInternal() {
        MocksControl ctrl = new MocksControl(MockType.DEFAULT);
    }
}