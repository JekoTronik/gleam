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

import static gleam.util.Logger.Level.WARNING;

/**
 * Scheme closure, a procedure with a definition environment.
 *
 */
public class Closure extends Procedure
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /* TODO: should implement scan-out of defines, as follows:
     * when a closure is created, a scanOut method should
     * retrieve a proper list of internally defined variables,
     * i.e. for (lambda (x y)
     *            (define a 1)
     *            (define b (foo x y))
     *            (begin
     *              (define c 1)
     *              (define d 2))
     *            (b (+ a c d)))
     * the scan out should give (a b c d).
     * At application time, after creating the local environment,
     * the scanned-out variables should be bound in the new
     * environment with an Undefined value.
     * This should suffice to preserve the "simultaneous definition"
     * semantics of Scheme defines inside procedures.
     *
     * The scan-out could be either done internally by the existing
     * constructor, or externally by the interpreter and passed on
     * to a new constructor.
     */
    protected final Entity param;
    protected final List body;
    protected final Environment definitionEnv;

    /**
     * Constructor.
     */
    public Closure(Entity param, List body, Environment env)
    {
        this.param = param;
        this.body = body;
        this.definitionEnv = env;
    }

    /**
     * Applies this closure.
     */
    @Override
    public Entity apply(List args, Environment env, Continuation cont)
        throws GleamException
    {
        Environment localenv = new Environment(definitionEnv);
        Entity currparam = param;
        List prev = null;
        boolean dotparam = false;

        /* bind actual arguments to formals (long)
         */
        try {
            // for each passed arg
            while (args != EmptyList.value) {
                // get next already-evaluated arg
                Entity obj = args.getCar();

                if (!dotparam) {
                    if (currparam == EmptyList.value) {
                        throw new GleamException("apply: too many arguments", this);
                    }
                    else if (currparam instanceof Pair) {
                        // normal case: get param symbol
                        // and bind it to argument in
                        // local env
                        Entity p = ((Pair) currparam).getCar();
                        if (p instanceof Symbol) {
                            localenv.define((Symbol)p, obj);
                        }
                        else {
                            Logger.enter(WARNING, "apply: param is not a symbol");
                        }
                        // next param, please
                        currparam = ((Pair) currparam).getCdr();
                    }
                    else if (currparam instanceof Symbol) {
                        // this is the unusual case
                        // we have a "." notation parameter
                        // so we accumulate this and next
                        // parameters in a cons bound to
                        // this param in local env
                        prev = new Pair(obj, EmptyList.value);
                        localenv.define((Symbol) currparam, prev);
                        dotparam = true;
                    }
                    else {
                        throw new GleamException("apply: invalid formal parameter", currparam);
                    }
                }
                else {
                    // accumulate argument
                    prev.setCdr(new Pair(obj, EmptyList.value));
                    prev = (List) prev.getCdr();
                }
                // next argument, please
                args = (List) args.getCdr();
            }
        }
        catch (ClassCastException e) {
            throw new GleamException("apply: improper list", currparam);
        }

        if (currparam instanceof Symbol && !dotparam) {
            // special case:
            // a "." notation parameter taking the empty list
            localenv.define((Symbol) currparam, EmptyList.value);
        }
        else if (currparam != EmptyList.value && !dotparam) {
            throw new GleamException("apply: too few arguments", this);
        }

        /* we have bound params, let's eval body
         * by adding to the continuation
         */
        cont.addCommandSequenceActions(body, localenv);
        return null;
    }

    /**
     * Writes a Closure
     */
    @Override
    public void write(PrintWriter out)
    {
        out.write("#<procedure");
        if (Logger.getLevelValue() < Logger.Level.INFO.getValue()) {
            out.write(" ");
            new Pair(Symbol.LAMBDA, new Pair(param, body)).write(out);
        }
        out.write(">");
    }

    /**
     * @return the max number of arguments for this closure, or -1 in case of var args
     * @throws GleamException in case of errors
     */
    public int getMaxArity()
            throws GleamException
    {
        int count = 0;
        Entity currParam = param;
        while (currParam instanceof Pair) {
            count++;
            currParam = ((List) currParam).getCdr();
        }
        if (currParam instanceof Symbol) {
            return -1;
        }
        return count;
    }
}
