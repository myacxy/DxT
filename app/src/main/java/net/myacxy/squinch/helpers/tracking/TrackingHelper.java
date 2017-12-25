package net.myacxy.squinch.helpers.tracking;

import net.myacxy.squinch.helpers.tracking.builder.ExceptionLogBuilder;
import net.myacxy.squinch.helpers.tracking.builder.LogBuilder;
import net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder;

public class TrackingHelper {

    public ExceptionLogBuilder exception() {
        return new ExceptionLogBuilder(this);
    }

    public LogBuilder log() {
        return new LogBuilder(this);
    }

    public UserLogBuilder user() {
        return new UserLogBuilder(this);
    }
}
