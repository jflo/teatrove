/*
 *  Copyright 1997-2011 teatrove.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.teatrove.trove.log;

import java.io.*;
import java.util.*;
import java.lang.ref.WeakReference;

/**
 * General purpose logging class that operates using a hierarchy of Logs and
 * an event model. All LogEvents are categorized into one of four types: 
 * debugging, informational, warning and error.
 *
 * A log event can be generated by writing to one of the four PrintWriter
 * fields provided (debug, info, warn and error), or by calling the debug,
 * info, warn or error methods.
 *
 * Logs can have a parent Log, which by default, receives all the events
 * that the Log generates or receives. If a log is disabled, it will not
 * generate or propagate events.
 *
 * Examples:
 * <pre>
 * Log log = new Log("test", null);
 * log.addLogListener(new LogScribe(new PrintWriter(System.out)));
 *
 * log.debug().println("Printing a debugging message");
 * log.info("System running");
 *
 * Syslog.info().println("Creating child log...");
 * Log child = new Log("child", Syslog.log);
 * child.warn("This is a system warning...");
 *
 * try {
 *     ...
 * }
 * catch (Exception e) {
 *     child.error(e);
 * }
 *
 * Log saved = new Log("saved", Syslog.log);
 * File logDir = new File("/logs/");
 * OutputStream out = new DailyFileLogStream(logDir);
 * saved.addLogListener(new LogScribe(new PrintWriter(out)));
 * saved.info("Saved log file initialized");
 * </pre>
 *
 * @author Brian S O'Neill
 * @version
 * <!--$$Revision:--> 29 <!-- $-->, <!--$$JustDate:-->  4/19/04 <!-- $-->
 * @see LogScribe
 * @see Syslog
 * @see LogEventParsingWriter
 * @see DailyFileLogStream
 */
public class Log implements LogListener, Serializable {
    private static final int ENABLED_MASK = 0x01;
    private static final int DEBUG_ENABLED_MASK = 0x02;
    private static final int INFO_ENABLED_MASK = 0x04;
    private static final int WARN_ENABLED_MASK = 0x08;
    private static final int ERROR_ENABLED_MASK = 0x10;

    private final transient PrintWriter mDebug;
    private final transient PrintWriter mInfo;
    private final transient PrintWriter mWarn;
    private final transient PrintWriter mError;

    private Log mParent;
    private Collection mChildren = new ArrayList();
    private String mName;
    private String mDescription;
    private int mEnabledFlags = 0xfffffff;
    protected transient List mListeners = Collections.synchronizedList(new ArrayList());


    /**
     * Create a new Log that inherits enabled settings from its parent. If no
     * parent is specified, then the Log is fully enabled. If a parent is
     * specified, then it is added automatically as a listener to this Log.
     *
     * @param name The optional name of this Log.
     * @param parent The parent Log that will be added as a LogListener to this
     * Log. If null, then this Log has no parent Log.
     */
    public Log(String name, Log parent) {
        this();

        if (parent != null) {
            mParent = parent;
            parent.mChildren.add(new WeakReference(this));
            mEnabledFlags = parent.mEnabledFlags;
            addLogListener(parent);
        }

        mName = name;
        mDescription = name;
    }

    private Log() {
        LogEventParsingWriter writer;

        writer = new LogEventParsingWriter(this, LogEvent.DEBUG_TYPE, this) {
            public boolean isEnabled() {
                return isDebugEnabled();
            }
        };
        writer.addLogListener(this);
        mDebug = new PrintWriter(writer, true);

        writer = new LogEventParsingWriter(this, LogEvent.INFO_TYPE, this) {
            public boolean isEnabled() {
                return isInfoEnabled();
            }
        };
        writer.addLogListener(this);
        mInfo = new PrintWriter(writer, true);

        writer = new LogEventParsingWriter(this, LogEvent.WARN_TYPE, this) {
            public boolean isEnabled() {
                return isWarnEnabled();
            }
        };
        writer.addLogListener(this);
        mWarn = new PrintWriter(writer, true);

        writer = new LogEventParsingWriter(this, LogEvent.ERROR_TYPE, this) {
            public boolean isEnabled() {
                return isErrorEnabled();
            }
        };
        writer.addLogListener(this);
        mError = new PrintWriter(writer, true);
    }

    /**
     * adds a listener to the root log, the log with a null parent
     */
    public void addRootLogListener(LogListener listener) {
        if (mParent == null) {
            addLogListener(listener);
        }
        else {
            mParent.addRootLogListener(listener);
        }
    }

    public void removeRootLogListener(LogListener listener) {
        mListeners.remove(listener);
        if (mParent == null) {
            removeLogListener(listener);
        }
        else {
            mParent.removeRootLogListener(listener);
        }
    }

    public void addLogListener(LogListener listener) {
        mListeners.add(listener);
    }

    public void removeLogListener(LogListener listener) {
        mListeners.remove(listener);
    }

    public void removeAllLogListeners() {
        mListeners = Collections.synchronizedList(new ArrayList());
    }

    /**
     * If this Log is enabled, then dispatch the LogEvent to all of its
     * listeners as a logged message.
     */
    public void logMessage(LogEvent e) {
        if (isEnabled()) {
            dispatchLogMessage(e);
        }
    }

    /**
     * If this Log is enabled, then dispatch the LogEvent to all of its
     * listeners as a logged exception.
     */
    public void logException(LogEvent e) {
        if (isEnabled()) {
            dispatchLogException(e);
        }
    }

    private void dispatchLogMessage(LogEvent e) {
        int size = mListeners.size();
        try {
            for (int i=0; i<size; i++) {
                ((LogListener)mListeners.get(i)).logMessage(e);
            }
        }
        catch (IndexOutOfBoundsException ex) {
        }
    }

    private void dispatchLogException(LogEvent e) {
        int size = mListeners.size();
        try {
            for (int i=0; i<size; i++) {
                ((LogListener)mListeners.get(i)).logException(e);
            }
        }
        catch (IndexOutOfBoundsException ex) {
        }
    }

    /**
     * Returns a PrintWriter for debug messages.
     */
    public PrintWriter debug() {
        return mDebug;
    }

    /**
     * Simple method for logging a single debugging message.
     */
    public void debug(String s) {
        if (isEnabled() && isDebugEnabled()) {
            dispatchLogMessage(new LogEvent(this, LogEvent.DEBUG_TYPE, s));
        }
    }

    /**
     * Simple method for logging a single debugging exception.
     */
    public void debug(Throwable t) {
        if (isEnabled() && isDebugEnabled()) {
            dispatchLogException(new LogEvent(this, LogEvent.DEBUG_TYPE, t));
        }
    }

    /**
     * Returns a PrintWriter for information messages.
     */
    public PrintWriter info() {
        return mInfo;
    }

    /**
     * Simple method for logging a single information message.
     */
    public void info(String s) {
        if (isEnabled() && isInfoEnabled()) {
            dispatchLogMessage(new LogEvent(this, LogEvent.INFO_TYPE, s));
        }
    }

    /**
     * Simple method for logging a single information exception.
     */
    public void info(Throwable t) {
        if (isEnabled() && isInfoEnabled()) {
            dispatchLogException(new LogEvent(this, LogEvent.INFO_TYPE, t));
        }
    }

    /**
     * Returns a PrintWriter for warning messages.
     */
    public PrintWriter warn() {
        return mWarn;
    }

    /**
     * Simple method for logging a single warning message.
     */
    public void warn(String s) {
        if (isEnabled() && isWarnEnabled()) {
            dispatchLogMessage(new LogEvent(this, LogEvent.WARN_TYPE, s));
        }
    }

    /**
     * Simple method for logging a single warning exception.
     */
    public void warn(Throwable t) {
        if (isEnabled() && isWarnEnabled()) {
            dispatchLogException(new LogEvent(this, LogEvent.WARN_TYPE, t));
        }
    }

    /**
     * Returns a PrintWriter for error messages.
     */
    public PrintWriter error() {
        return mError;
    }

    /**
     * Simple method for logging a single error message.
     */
    public void error(String s) {
        if (isEnabled() && isErrorEnabled()) {
            dispatchLogMessage(new LogEvent(this, LogEvent.ERROR_TYPE, s));
        }
    }

    /**
     * Simple method for logging a single error exception.
     */
    public void error(Throwable t) {
        if (isEnabled() && isErrorEnabled()) {
            dispatchLogException(new LogEvent(this, LogEvent.ERROR_TYPE, t));
        }
    }

    /**
     * Returns a copy of the children Logs.
     */
    public Log[] getChildren() {
        Collection copy;

        synchronized (mChildren) {
            copy = new ArrayList(mChildren.size());
            Iterator it = mChildren.iterator();
            while (it.hasNext()) {
                Log child = (Log)((WeakReference)it.next()).get();
                if (child == null) {
                    it.remove();
                }
                else {
                    copy.add(child);
                }
            }
        }

        return (Log[])copy.toArray(new Log[copy.size()]);
    }

    /**
     * Returns null if this Log has no name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns a brief description of this Log. By default, returns the name.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Set the log's description text.
     */
    public void setDescription(String desc) {
        mDescription = desc;
    }

    /**
     * Returns true if this Log is enabled. If log is disabled, then no log 
     * events of any kind are generated or propagated to listeners.
     */
    public boolean isEnabled() {
        return isEnabled(ENABLED_MASK);
    }

    /**
     * When this Log is enabled, all parent Logs are also enabled. When this
     * Log is disabled, parent Logs are unaffected.
     */
    public void setEnabled(boolean enabled) {
        setEnabled(enabled, ENABLED_MASK);
        if (enabled) {
            Log parent;
            if ((parent = mParent) != null) {
                parent.setEnabled(true);
            }
        }
    }

    /**
     * Returns true if debug events for this Log are enabled. If debug events
     * are disabled, then no debug log events are generated, but they can be
     * propagated to listeners.
     */
    public boolean isDebugEnabled() {
        return isEnabled(DEBUG_ENABLED_MASK);
    }

    /**
     * When debug is enabled, this Log is enabled and all parent Logs are
     * enabled. Disabling debug only affects this Log's debug state.
     */
    public void setDebugEnabled(boolean enabled) {
        setEnabled(enabled, DEBUG_ENABLED_MASK);
        if (enabled) {
            setEnabled(true);
        }
    }

    /**
     * Returns true if info events for this Log are enabled. If info events
     * are disabled, then no info log events are generated, but they can be
     * propagated to listeners.
     */
    public boolean isInfoEnabled() {
        return isEnabled(INFO_ENABLED_MASK);
    }

    /**
     * When info is enabled, this Log is enabled and all parent Logs are
     * enabled. Disabling info only affects this Log's info state.
     */
    public void setInfoEnabled(boolean enabled) {
        setEnabled(enabled, INFO_ENABLED_MASK);
        if (enabled) {
            setEnabled(true);
        }
    }

    /**
     * Returns true if warn events for this Log are enabled. If warn events
     * are disabled, then no warn log events are generated, but they can be
     * propagated to listeners.
     */
    public boolean isWarnEnabled() {
        return isEnabled(WARN_ENABLED_MASK);
    }

    /**
     * When warn is enabled, this Log is enabled and all parent Logs are
     * enabled. Disabling warn only affects this Log's warn state.
     */
    public void setWarnEnabled(boolean enabled) {
        setEnabled(enabled, WARN_ENABLED_MASK);
        if (enabled) {
            setEnabled(true);
        }
    }

    /**
     * Returns true if error events for this Log are enabled. If error events
     * are disabled, then no error log events are generated, but they can be
     * propagated to listeners.
     */
    public boolean isErrorEnabled() {
        return isEnabled(ERROR_ENABLED_MASK);
    }

    /**
     * When error is enabled, this Log is enabled and all parent Logs are
     * enabled. Disabling error only affects this Log's error state.
     */
    public void setErrorEnabled(boolean enabled) {
        setEnabled(enabled, ERROR_ENABLED_MASK);
        if (enabled) {
            setEnabled(true);
        }
    }

    /**
     * Understands and applies the following boolean properties. True is the
     * default value if the value doesn't equal "false", ignoring case.
     *
     * <ul>
     * <li>enabled
     * <li>debug
     * <li>info
     * <li>warn
     * <li>error
     * </ul>
     */
    public void applyProperties(Map properties) {
        if (properties.containsKey("enabled")) {
            setEnabled(!"false".equalsIgnoreCase
                       ((String)properties.get("enabled")));
        }

        if (properties.containsKey("debug")) {
            setDebugEnabled(!"false".equalsIgnoreCase
                            ((String)properties.get("debug")));
        }

        if (properties.containsKey("info")) {
            setInfoEnabled(!"false".equalsIgnoreCase
                           ((String)properties.get("info")));
        }

        if (properties.containsKey("warn")) {
            setWarnEnabled(!"false".equalsIgnoreCase
                           ((String)properties.get("warn")));
        }

        if (properties.containsKey("error")) {
            setErrorEnabled(!"false".equalsIgnoreCase
                            ((String)properties.get("error")));
        }
    }

    /**
     * Returns a short description of this log.
     */
    public String toString() {
        return "Log[" + getDescription() + "]@" + 
            Integer.toHexString(hashCode());
    }

    private boolean isEnabled(int mask) {
        return (mEnabledFlags & mask) == mask;
    }

    private void setEnabled(boolean enabled, int mask) {
        if (enabled) {
            mEnabledFlags |= mask;
        }
        else {
            mEnabledFlags &= ~mask;
        }
    }
}