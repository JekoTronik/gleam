/*
 * Copyright (c) 2001 Guglielmo Nigri.  All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it would be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Further, this software is distributed without any warranty that it is
 * free of the rightful claim of any third person regarding infringement
 * or the like.  Any license provided herein, whether implied or
 * otherwise, applies only to this software file.  Patent licenses, if
 * any, provided herein do not apply to combinations of this program with
 * other software, or any other product whatsoever.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write the Free Software Foundation, Inc., 59
 * Temple Place - Suite 330, Boston MA 02111-1307, USA.
 *
 * Contact information: Guglielmo Nigri <guglielmonigri@yahoo.it>
 *
 */

package gleam.lang;

/*
 * EmptyList.java
 *
 * Created on October 24, 2001, 1:55 AM
 */

import gleam.util.Logger;

import java.io.PrintWriter;
import java.util.Iterator;

import static gleam.util.Logger.Level.FINE;

/**
 * This class represents the distinct type of the Scheme empty list.
 * There is only one instance of this class, i.e. the empty list itself '().
 * EmptyList is a specialization of Pair uniquely for efficiency reasons in
 * procedure evaluation.
 */
public final class EmptyList extends AbstractEntity implements List {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** the EmptyList singleton */
    static final EmptyList value = new EmptyList();

    /** Can't create an empty list */
    private EmptyList() {
    }

    /**
     * factory method
     */
    public static EmptyList value() {
        return value;
    }

    /**
     * Prevents the release of multiple instances upon deserialization.
     */
    protected Object readResolve()
    {
        Logger.enter(FINE, "readResolve() called! (EmptyList)"); //DEBUG
        return value;
    }

    /**
     * The empty combination is an error in Scheme.
     */
    @Override
    public Entity eval(Environment env, Continuation cont)
        throws GleamException
    {
        throw new GleamException("invalid combination: empty list", this);
    }

    /**
     * Writes the empty list value.
     */
    @Override
    public void write(PrintWriter out)
    {
        out.print("()");
    }

    @Override
    public Entity getCar() throws GleamException {
        throw new GleamException("car: invalid arguments", this);
    }

    @Override
    public Entity getCdr() throws GleamException {
        throw new GleamException("cdr: invalid arguments", this);
    }

    @Override
    public void setCar(Entity obj) throws GleamException {
        throw new GleamException("set-car!: invalid arguments", this);
    }

    @Override
    public void setCdr(Entity obj) throws GleamException {
        throw new GleamException("set-cdr!: invalid arguments", this);
    }

    /**
     * Returns an iterator for the empty list.
     *
     * @return an Entity iterator.
     */
    @Override
    public Iterator<Entity> iterator() {
        return new ListIterator(this);
    }
}
