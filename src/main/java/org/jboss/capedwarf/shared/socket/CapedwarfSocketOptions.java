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

package org.jboss.capedwarf.shared.socket;

import java.net.SocketException;
import java.net.SocketOptions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Modeled after AppEngineSocketOptions.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CapedwarfSocketOptions {
    private static final Set<Integer> SET_OPTIONS;
    private static final Set<Integer> GET_OPTIONS;

    static {
        SET_OPTIONS = Collections.emptySet(); // atm all are disabled

        GET_OPTIONS = new HashSet<>();
        GET_OPTIONS.add(SocketOptions.IP_TOS);
        GET_OPTIONS.add(SocketOptions.SO_BINDADDR); // OK?
        GET_OPTIONS.add(SocketOptions.SO_KEEPALIVE);
        // GET_OPTIONS.add(SocketOptions.SO_DEBUG); // doesn't exist
        GET_OPTIONS.add(SocketOptions.SO_LINGER);
        GET_OPTIONS.add(SocketOptions.SO_OOBINLINE);
        GET_OPTIONS.add(SocketOptions.SO_RCVBUF);
        GET_OPTIONS.add(SocketOptions.SO_REUSEADDR);
        GET_OPTIONS.add(SocketOptions.SO_SNDBUF);
        GET_OPTIONS.add(SocketOptions.SO_TIMEOUT); // extra?
        GET_OPTIONS.add(SocketOptions.TCP_NODELAY);
    }

    private abstract static class CheckFunction<T> {
        abstract Class<T> equivalenceClass();

        @SuppressWarnings("unused")
        void check(Option opt, T value, boolean isDatagramSocket) throws SocketException {
        }

        void apply(Option option, SocketOptionsInternal socketImpl, T value) throws SocketException {
            option.setOption(socketImpl, value);
        }
    }

    private static class IntegerCheckFunction extends CheckFunction<Integer> {
        Class<Integer> equivalenceClass() {
            return Integer.class;
        }
    }

    private static class BooleanCheckFunction extends CheckFunction<Boolean> {
        Class<Boolean> equivalenceClass() {
            return Boolean.class;
        }
    }

    private static class UnimplimentedCheckFunction extends CheckFunction<Object> {
        Class<Object> equivalenceClass() {
            return Object.class;
        }

        @Override
        void check(Option opt, Object value, boolean isDatagramSocket) throws SocketException {
            throw new SocketException(opt.optionName() + " is not implimented.");
        }
    }

    private static class GreaterThanZeroCheckFunction extends IntegerCheckFunction {
        @Override
        void check(Option opt, Integer value, boolean isDatagramSocket) throws SocketException {
            if (value <= 0) {
                throw new SocketException("bad parameter for '" + opt.optionName() + "'" + " Must be greater than zero. value = " + value);
            }
        }
    }

    private static class OnlyAllowedForTCP extends IntegerCheckFunction {
        @Override
        void check(Option opt, Integer value, boolean isDatagramSocket) throws SocketException {
            if (isDatagramSocket) {
                throw new SocketException("Option '" + opt.optionName() + "'" + " is not allowed for datagram sockets.");
            }
        }
    }

    private static final CheckFunction INTEGER_CHECK = new IntegerCheckFunction();
    private static final CheckFunction INTEGER_GT0_CHECK = new GreaterThanZeroCheckFunction();
    private static final CheckFunction BOOLEAN_CHECK = new BooleanCheckFunction();
    private static final CheckFunction UNIMPLIMENTED_CHECK = new UnimplimentedCheckFunction();
    private static final CheckFunction ONLY_ALLOWED_FOR_TCP = new OnlyAllowedForTCP();

    /**
     * Enumeration of all available socket options.
     */
    static enum Option {
        SO_LINGER_OPT(
            SocketOptions.SO_LINGER,
            INTEGER_CHECK,
            new BooleanCheckFunction() {
                @Override
                void apply(Option option, SocketOptionsInternal socketImpl, Boolean val) throws SocketException {
                    super.apply(option, socketImpl, false);
                }
            },
            ONLY_ALLOWED_FOR_TCP),
        SO_TIMEOUT_OPT(
            SocketOptions.SO_TIMEOUT,
            new IntegerCheckFunction() {
                @Override
                void check(Option opt, Integer value, boolean isDatagramSocket) {
                    if (value < 0) {
                        throw new IllegalArgumentException(opt.optionName() + " requires timeout value >= 0: timeout given = " + value);
                    }
                }
            }),
        IP_TOS_OPT(SocketOptions.IP_TOS, INTEGER_CHECK, UNIMPLIMENTED_CHECK),
        SO_BINDADDR_OPT(SocketOptions.SO_BINDADDR, UNIMPLIMENTED_CHECK),
        TCP_NODELAY_OPT(SocketOptions.TCP_NODELAY, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        SO_SNDBUF_OPT(SocketOptions.SO_SNDBUF, INTEGER_GT0_CHECK),
        SO_RCVBUF_OPT(SocketOptions.SO_RCVBUF, INTEGER_GT0_CHECK),
        SO_KEEPALIVE_OPT(SocketOptions.SO_KEEPALIVE, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        SO_OOBINLINE_OPT(SocketOptions.SO_OOBINLINE, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        SO_REUSEADDR_OPT(SocketOptions.SO_REUSEADDR, BOOLEAN_CHECK),
        SO_DEBUG_OPT(null, BOOLEAN_CHECK),
        SO_TYPE_OPT(null),
        SO_ERROR_OPT(null),
        SO_DONTROUTE_OPT(null, BOOLEAN_CHECK),
        SO_BROADCAST_OPT(null, BOOLEAN_CHECK),
        IP_TTL_OPT(null, INTEGER_CHECK),
        IP_HDRINCL_OPT(null, BOOLEAN_CHECK),
        IP_OPTIONS_OPT(null, UNIMPLIMENTED_CHECK),
        TCP_MAXSEG_OPT(null, INTEGER_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_CORK_OPT(null, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_KEEPIDLE_OPT(null, INTEGER_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_KEEPINTVL_OPT(null, INTEGER_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_KEEPCNT_OPT(null, INTEGER_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_SYNCNT_OPT(null, INTEGER_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_LINGER2_OPT(null, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_DEFER_ACCEPT_OPT(null, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_WINDOW_CLAMP_OPT(null, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_INFO_OPT(null, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),
        TCP_QUICKACK_OPT(null, BOOLEAN_CHECK, ONLY_ALLOWED_FOR_TCP),;

        private Integer opt;
        private CheckFunction[] checkFuncs;

        Option(Integer opt, CheckFunction... checkFuncs) {
            this.opt = opt;
            this.checkFuncs = checkFuncs;
        }

        String optionName() {
            return name().substring(0, name().length() - "_OPT".length());
        }

        /**
         * Perform validation for this option and apply the set option changes.
         *
         * @throws SocketException for any error
         */
        @SuppressWarnings("unchecked")
        private void validateAndApply(SocketOptionsInternal socketImpl, Object val, boolean isDatagramSocket) throws SocketException {
            if (val == null) {
                throw new SocketException("Bad value 'null' for option " + optionName());
            }
            if (checkFuncs.length == 0) {
                throw new SocketException("Option " + optionName() + " is not allowed to be set.");
            }
            for (CheckFunction checkFunc : checkFuncs) {
                if (checkFunc.equivalenceClass().isInstance(val)) {
                    checkFunc.check(this, val, isDatagramSocket);
                }
            }
            for (CheckFunction checkFunc : checkFuncs) {
                if (checkFunc.equivalenceClass().isInstance(val)) {
                    checkFunc.apply(this, socketImpl, val);
                    return;
                }
            }
            throw new SocketException("Bad parameter type of '" + val.getClass().getName() + "' for option " + optionName());
        }

        void validateAndApply(SocketOptionsInternal socketImpl, Object val) throws SocketException {
            validateAndApply(socketImpl, val, false);
        }

        Integer getOpt() {
            return opt;
        }

        Object getOption(SocketOptionsInternal socketImpl) throws SocketException {
            return (isGetEnabled()) ? socketImpl.getOptionInternal(opt) : null;
        }

        void setOption(SocketOptionsInternal socketImpl, Object value) throws SocketException {
            if (isSetEnabled()) {
                socketImpl.setOptionInternal(getOpt(), value);
            }
        }

        private boolean isGetEnabled() {
            return (opt != null && GET_OPTIONS.contains(opt));
        }

        private boolean isSetEnabled() {
            return (opt != null && SET_OPTIONS.contains(opt));
        }
    }

    static Option getOptionById(int optionId) {
        for (Option option : Option.values()) {
            if (option.opt == null) {
                continue;
            }
            if (option.opt == optionId) {
                return option;
            }
        }
        return null;
    }
}
