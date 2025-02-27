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

import gleam.util.Logger;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Scheme symbol factory.
 */
public final class Symbol extends AbstractEntity
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The unique symbol table
     */
    static final Map<String, Symbol> symtable = new HashMap<>(512);

    /*
     * common symbols (some are keywords, some are not)
     * defined here as constants for convenience
     */
    public static final Symbol AND = makeSymbol("and");
    public static final Symbol APPEND = makeSymbol("append");
//    public static final Symbol ARROW = makeSymbol("=>");
    public static final Symbol BEGIN = makeSymbol("begin");
    public static final Symbol CALL_CC = makeSymbol("call/cc");
    public static final Symbol CALL_WITH_CURRENT_CONTINUATION = makeSymbol("call-with-current-continuation");
    public static final Symbol CASE = makeSymbol("case");
    public static final Symbol COND = makeSymbol("cond");
    public static final Symbol CONS = makeSymbol("cons");
    public static final Symbol DEFINE = makeSymbol("define");
//    public static final Symbol DELAY = makeSymbol("delay"); // TODO implement delay / force
    public static final Symbol DO = makeSymbol("do");
    public static final Symbol ELSE = makeSymbol("else");
    public static final Symbol ERROBJ = makeSymbol("__errobj");
    public static final Symbol HELP = makeSymbol("help");
    public static final Symbol IF = makeSymbol("if");
    public static final Symbol LAMBDA = makeSymbol("lambda");
    public static final Symbol LET = makeSymbol("let");
    public static final Symbol LETREC = makeSymbol("letrec");
    public static final Symbol LETSTAR = makeSymbol("let*");
    public static final Symbol LIST = makeSymbol("list");
    public static final Symbol OR = makeSymbol("or");
    public static final Symbol QUASIQUOTE = makeSymbol("quasiquote");
    public static final Symbol QUOTE = makeSymbol("quote");
    public static final Symbol SET = makeSymbol("set!");
    public static final Symbol UNQUOTE = makeSymbol("unquote");
    public static final Symbol UNQUOTE_SPLICING = makeSymbol("unquote-splicing");

    /**
     * String representation
     */
    final String value;

    /**
     * Interned?
     */
    final boolean interned;

    /**
     * Can't instantiate directly.
     */
    private Symbol(String value)
    {
        this(value, true);
    }

    private Symbol(String value, boolean interned)
    {
        this.value = value;
        this.interned = interned;
    }

    /**
     * Obtains the string representation of this symbol
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Evaluates this symbol in the given environment.
     */
    @Override
    public Entity eval(Environment env, Continuation cont)
        throws GleamException
    {
        return env.lookup(this);
    }

    /**
     * Factory method to create and intern a symbol.
     */
    public synchronized static Symbol makeSymbol(String s)
    {
        Symbol o = symtable.get(s);
        if (o == null) {
            o = new Symbol(s);
            symtable.put(s, o);
        }

        return o;
    }

    /**
     * Factory method to create an uninterned symbol.
     */
    public static Symbol makeUninternedSymbol(String s)
    {
        return new Symbol(s, false);
    }

    /**
     * Prevents the release of multiple instances upon deserialization.
     */
    protected Object readResolve()
    {
        Logger.enter(Logger.Level.FINE, "readResolve() called! (Symbol)"); //DEBUG
        if (interned)
            return makeSymbol(value);
        else
            return this;
    }

    /**
     * Performs environment optimization on this symbol.
     */
    @Override
    public Entity optimize(Environment env)
    {
        Location loc = env.getLocationOrNull(this);
        if (loc == null) {
            // if unbound, return just the symbol (for syntax rewriters)
            return this;
        }
        if (loc.get() == Undefined.value) {
            /* this symbol is a function parameter, so let
             * name resolution take place at run time
             */
            return this;
        }

        return loc;
    }

    /** Writes this symbol */
    @Override
    public void write(PrintWriter out)
    {
        out.write(value);
    }
}
