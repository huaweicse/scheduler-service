package org.apache.servicecomb.scheduler.common;

public interface IAlarm {
    default void recvAlarm(AlarmContext alarmContext) {
        return;
    }

    default void sendAlarm(AlarmContext alarmContext) {

    }
}
