/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.capedwarf.shared.endpoints;

import javassist.CtClass;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CtClassWrapper {
    private CtClass ctClass;

    CtClassWrapper(CtClass ctClass) {
        this.ctClass = ctClass;
    }

    CtClass getCtClass() {
        return ctClass;
    }

    ClassLoader getClassLoader() {
        return ctClass.getClassPool().getClassLoader();
    }

    Class<?> toClass() throws ClassNotFoundException {
        return getClassLoader().loadClass(ctClass.getName());
    }

    @Override
    public int hashCode() {
        return ctClass.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CtClassWrapper == false) {
            return false;
        }

        String className = ctClass.getName();
        String classNameOther = CtClassWrapper.class.cast(obj).ctClass.getName();
        if (className.equals(classNameOther) == false) {
            return false;
        }
        ClassLoader cl = getClassLoader();
        ClassLoader clOther = CtClassWrapper.class.cast(obj).getClassLoader();
        return (cl == clOther);
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", ctClass.getName(), ctClass.getClassPool().getClassLoader());
    }
}
